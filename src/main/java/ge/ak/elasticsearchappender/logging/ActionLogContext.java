package ge.ak.elasticsearchappender.logging;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ActionLogContext {

    private static final ThreadLocal<ActionLogContextBean> LOG_CONTEXT = new ThreadLocal<>();

    private static final List<String> LOGGING_ALLOW_RESPONSE_TYPES = Arrays.asList(
            "application/json",
            "text/plain"
    );

    public static void setRequestData(String requestData) {
        ActionLogContextBean actionLogContextBean = new ActionLogContextBean();
        actionLogContextBean.setRequestData(requestData);

        LOG_CONTEXT.set(actionLogContextBean);
    }

    public static ActionLogContextBean getRequestData() {
        ActionLogContextBean actionLogContextBean = LOG_CONTEXT.get();

        return actionLogContextBean;
    }

    public static String getResponseData(final HttpServletResponse response) throws IOException {
        String payload = null;
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String contentType = response.getContentType();
                if (contentType != null) {
                    String[] contentTypeParts = contentType.split(";");
                    if (LOGGING_ALLOW_RESPONSE_TYPES.contains(contentTypeParts[0])) {
                        payload = new String(buf);
                    }
                }
                wrapper.copyBodyToResponse();
            }
        }

        return payload;
    }

    public static void clear() {
        LOG_CONTEXT.remove();
    }
}
