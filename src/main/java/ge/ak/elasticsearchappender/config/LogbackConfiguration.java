package ge.ak.elasticsearchappender.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.agido.logback.elasticsearch.ElasticsearchAppender;
import com.agido.logback.elasticsearch.config.Authentication;
import com.agido.logback.elasticsearch.config.ElasticsearchProperties;
import com.agido.logback.elasticsearch.config.Property;
import com.agido.logback.elasticsearch.config.Settings;
import ge.ak.elasticsearchappender.logging.ElasticsearchAppenderProperties;
import ge.ak.elasticsearchappender.model.BasicAuthentication;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@DependsOn("checkIndex")
@ConditionalOnProperty(name = "elasticsearch.appender.elastic.enable", havingValue = "true", matchIfMissing = false)
@Configuration
public class LogbackConfiguration {
    private ElasticsearchAppenderProperties properties;

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LogbackConfiguration.class);

    @Bean
    public ElasticsearchAppender configureLogback() throws IOException, URISyntaxException {
        if (properties.getSystemLogIndexName() == null || properties.getSystemLogIndexName().isEmpty()) {
            log.warn("System log index name is null or empty. Elasticsearch logging will be skipped.");
            return null;
        }

        Level level;
        try {
            level = Level.valueOf(properties.getLogLevel());
        } catch (Exception e) {
            log.error("Invalid log level, setting to INFO by default", e);
            level = Level.INFO;
        }

        Settings settings = getSettings();

        ElasticsearchAppender appender = new ElasticsearchAppender(settings);
        appender.setName("elasticsearch-appender");

        ElasticsearchProperties elasticsearchProperties = new ElasticsearchProperties();
        elasticsearchProperties.addEsProperty(new Property("appName", properties.getAppName(), true));
        elasticsearchProperties.addEsProperty(new Property("host", " %X{clientHost}", true));
        elasticsearchProperties.addEsProperty(new Property("severity", "%level", true));
        elasticsearchProperties.addEsProperty(new Property("thread", "%thread", true));
        elasticsearchProperties.addEsProperty(new Property("stacktrace", "%ex{full}", false));
        elasticsearchProperties.addEsProperty(new Property("logger", "%logger", true));
        elasticsearchProperties.addEsProperty(new Property("traceId", "%X{traceId}", true));
        elasticsearchProperties.addEsProperty(new Property("spanId", "%X{spanId}", true));

        appender.setProperties(elasticsearchProperties);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        appender.setContext(context);
        appender.start();

        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(appender);
        rootLogger.setLevel(level);

        Logger systemLogLogger = context.getLogger(properties.getSystemLogLoggerName());
        systemLogLogger.setLevel(Level.OFF);

        Logger systemLogErrorLogger = context.getLogger(properties.getSystemLogErrorLoggerName());
        systemLogErrorLogger.setLevel(Level.OFF);

        return appender;
    }

    private Settings getSettings() throws MalformedURLException, URISyntaxException {
        Settings settings = new Settings();
        settings.setIndex(properties.getSystemLogIndexName());
        settings.setLoggerName(properties.getSystemLogLoggerName());
        settings.setErrorLoggerName(properties.getSystemLogErrorLoggerName());
        settings.setConnectTimeout(30000);
        settings.setReadTimeout(30000);
        settings.setErrorsToStderr(false);
        settings.setIncludeCallerData(false);
        settings.setLogsToStderr(false);
        settings.setMaxQueueSize(104857600);
        settings.setMaxRetries(3);
        settings.setSleepTime(250);
        settings.setRawJsonMessage(false);
        settings.setIncludeMdc(false);


        String baseUrl = properties.getSchema() + "://" + properties.getHost() + ":" + properties.getPort() + "/_bulk";
        URI uri = new URI(baseUrl);
        settings.setUrl(uri.toURL());

        Authentication authentication = new BasicAuthentication(properties.getUsername(), properties.getPassword());
        settings.setAuthentication(authentication);

        return settings;
    }

    @Autowired
    public void setProperties(ElasticsearchAppenderProperties properties) {
        this.properties = properties;
    }
}
