package ge.ak.elasticsearchappender.config;

import ge.ak.elasticsearchappender.logging.LogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ConditionalOnProperty(name = "elasticsearch.appender.elastic.enable", havingValue = "true", matchIfMissing = false)
@Configuration
public class LoggingInterceptorAdapter implements WebMvcConfigurer {

    private LogInterceptor logInterceptor;

    @Autowired
    public void setLogInterceptor(LogInterceptor logInterceptor) {
        this.logInterceptor = logInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);
    }
}