package ge.ak.elasticsearchappender.logging;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.stream.Collectors;


@Component
public class LogInterceptor implements HandlerInterceptor {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(LogInterceptor.class);

    private ObjectMapper loggingObjectMapper;

    @PostConstruct
    public void setLoggingObjectMapper() {
        loggingObjectMapper = new ObjectMapper();
        loggingObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        loggingObjectMapper.setAnnotationIntrospector(new MaskLogSensitiveDataAnnotationIntrospector());
        loggingObjectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getInputStream() != null && request.getInputStream().getClass().equals(CachedBodyServletInputStream.class)) {
            if (handler instanceof HandlerMethod) {
                Method method = ((HandlerMethod) handler).getMethod();
                if (method.getParameterCount() > 0) {
                    Parameter[] parameters = method.getParameters();
                    for (Parameter parameter : parameters) {
                        if (parameter.isAnnotationPresent(RequestBody.class)) {
                            Class<?> type = parameter.getType();
                            String requestDate = getRequestData(request, type);
                            ActionLogContext.setRequestData(requestDate);

                            break;
                        }
                    }
                }
            }
        } else {
            ActionLogContext.setRequestData(null);
        }

        return true;
    }

    private String getRequestData(final HttpServletRequest request, Class<?> clazz) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            if (Objects.nonNull(inputStream)) {
                byte[] bytes = inputStream.readAllBytes();
                if (bytes.length > 0) {
                    Object serialized = loggingObjectMapper.readValue(bytes, clazz);
                    return loggingObjectMapper.writeValueAsString(serialized);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            log.error("Error while reading request body");
            try {
                return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (Exception e1) {
                log.error(e1.getMessage(), e1);
            }
        }

        return null;
    }
}
