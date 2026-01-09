package ge.ak.elasticsearchappender.config;

import ge.ak.elasticsearchappender.logging.AppenderProperties;
import ge.ak.elasticsearchappender.logging.ElasticsearchAppenderProperties;
import ge.ak.elasticsearchappender.logging.ILM;
import ge.ak.elasticsearchappender.logging.LogInterceptor;
import ge.ak.elasticsearchappender.service.CheckIndex;
import ge.ak.elasticsearchappender.service.impl.ActionLogDocumentServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ElasticsearchConfiguration.class,
        FilterConfig.class,
        LogbackConfiguration.class,
        LoggingInterceptorAdapter.class,
        ObjectMapperConfiguration.class,
        LogInterceptor.class,
        ActionLogDocumentServiceImpl.class,
        ElasticsearchAppenderProperties.class,
        ILM.class,
        CheckIndex.class,
        AppenderProperties.class,
})
@ConditionalOnProperty(name = "elasticsearch.appender.elastic.enable", havingValue = "true", matchIfMissing = false)
public class AutoConfiguration {

}
