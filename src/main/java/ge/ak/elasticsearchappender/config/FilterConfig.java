package ge.ak.elasticsearchappender.config;

import ge.ak.elasticsearchappender.logging.AppenderProperties;
import ge.ak.elasticsearchappender.logging.RequestResponseLoggingFilter;
import ge.ak.elasticsearchappender.service.ActionLogDocumentService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "elasticsearch.appender.elastic.enable", havingValue = "true", matchIfMissing = false)
@Configuration
public class FilterConfig {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(FilterConfig.class);


    private ApplicationContext applicationContext;
    private ActionLogDocumentService actionLogDocumentService;
    private AppenderProperties appenderProperties;

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilter() {
        String[] urlPatterns = appenderProperties.getUrlPatterns();
        if (urlPatterns == null || urlPatterns.length == 0) {
            return null;
        }
        log.info("Starting logging filter urlPatterns:{}", String.join(",", urlPatterns));

        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RequestResponseLoggingFilter(applicationContext, actionLogDocumentService));
        registrationBean.setOrder(Integer.MIN_VALUE);
        registrationBean.addUrlPatterns(urlPatterns);
        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setActionLogDocumentService(ActionLogDocumentService actionLogDocumentService) {
        this.actionLogDocumentService = actionLogDocumentService;
    }

    @Autowired
    public void setAppenderProperties(AppenderProperties appenderProperties) {
        this.appenderProperties = appenderProperties;
    }
}