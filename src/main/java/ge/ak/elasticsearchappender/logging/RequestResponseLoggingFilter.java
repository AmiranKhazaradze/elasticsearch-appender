package ge.ak.elasticsearchappender.logging;

import ge.ak.elasticsearchappender.service.ActionLogDocumentService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;

public class RequestResponseLoggingFilter implements Filter {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    private static final List<String> LOGGING_ALLOW_REQUEST_TYPES = List.of(
            "application/json"
    );

    private final ApplicationContext context;
    private final ActionLogDocumentService actionLogDocumentService;

    public RequestResponseLoggingFilter(ApplicationContext context, ActionLogDocumentService actionLogDocumentService) {
        this.context = context;
        this.actionLogDocumentService = actionLogDocumentService;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        long startTime = System.currentTimeMillis();

        //Generate traceId to connect action log with console log
        String traceId = new BigInteger(64, new Random()).toString(16);
        MDC.put("traceId", traceId);

        HttpServletRequest req = (HttpServletRequest) request;
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String clientHost = req.getRemoteAddr();
        MDC.put("clientHost", clientHost);

        HttpServletResponse res = (HttpServletResponse) response;
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Throwable throwable = null;

        HttpServletResponse responseToCache = new ContentCachingResponseWrapper(res);
        Optional<String> contentType = Optional.ofNullable(req.getHeader("content-type"))
                .map(a -> a.split(";"))
                .map(a -> a[0]);

        if (contentType.isPresent() && LOGGING_ALLOW_REQUEST_TYPES.contains(contentType.get())) {
            req = new CachedBodyHttpServletRequest(req);
        }

        try {
            chain.doFilter(req, responseToCache);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throwable = e;

            throw e;
        } finally {
            logRequestResponse(req, responseToCache, throwable, startTime);
            ActionLogContext.clear();
        }
    }

    private void logRequestResponse(HttpServletRequest req, HttpServletResponse res, Throwable th, long startTime) {
        try {

            ActionLogDocument actionLogDocument = new ActionLogDocument();
            actionLogDocument.setId(UUID.randomUUID().toString());
            actionLogDocument.setAppName(context.getId());

            String traceId = MDC.get("traceId");
            actionLogDocument.setTraceId(traceId);
            actionLogDocument.setSpanId(traceId);

            actionLogDocument.setTimestamp(ZonedDateTime.now());
            actionLogDocument.setRemoteAddress(req.getRemoteAddr());

            //Set headers
            Enumeration<String> headerNames = req.getHeaderNames();
            Map<String, String> headers = new HashMap<>();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = req.getHeader(headerName);
                headers.put(headerName, headerValue);
            }
            actionLogDocument.setRequestHeaders(headers);
            actionLogDocument.setRequestURI(req.getRequestURI());

            String queryString = req.getQueryString();
            if (queryString != null) {
                try {
                    String decodedQueryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8);
                    actionLogDocument.setQueryString(decodedQueryString);
                } catch (Exception e) {
                    log.error("Error decoding query string", e);
                    actionLogDocument.setQueryString(queryString);
                }
            }

            String requestData = ActionLogContext.getRequestData() == null ? null : ActionLogContext.getRequestData().getRequestData();
            actionLogDocument.setRequestBody(requestData);

            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof UserDetails) {
                        UserDetails userDetails = (UserDetails) principal;
                        actionLogDocument.setUserName(userDetails.getUsername());
                    } else {
                        actionLogDocument.setUserName(principal.toString());
                    }
                }
            } catch (Exception ignored) {
                actionLogDocument.setUserName("ANONYMOUS");
            }

            //Response
            actionLogDocument.setResponseStatus(res.getStatus());
            headers = new HashMap<>();
            Collection<String> responseHeaderNames = res.getHeaderNames();
            for (String responseHeaderName : responseHeaderNames) {
                headers.put(responseHeaderName, res.getHeader(responseHeaderName));
            }
            actionLogDocument.setResponseHeaders(headers);

            if (Objects.nonNull(th)) {
                actionLogDocument.setError(th.getMessage());
            }

            String responseData = ActionLogContext.getResponseData(res);
            actionLogDocument.setResponseBody(responseData);

            long endTime = System.currentTimeMillis();
            actionLogDocument.setExecutionTimeMs(endTime - startTime);

            actionLogDocumentService.indexAsync(actionLogDocument);
        } catch (Exception e) {
            log.error("Error while saving action log");
            log.error(e.getMessage(), e);
        }
    }
}