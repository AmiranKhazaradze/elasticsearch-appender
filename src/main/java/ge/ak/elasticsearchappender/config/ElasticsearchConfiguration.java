package ge.ak.elasticsearchappender.config;


import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import ge.ak.elasticsearchappender.logging.ElasticsearchAppenderProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "elasticsearch.appender.elastic.enable", havingValue = "true", matchIfMissing = false)
public class ElasticsearchConfiguration {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ElasticsearchConfiguration.class);

    private ElasticsearchAppenderProperties properties;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return new ElasticsearchClient(elasticsearchTransport());
    }

    @Bean
    public ElasticsearchAsyncClient elasticsearchAsyncClient() {
        return new ElasticsearchAsyncClient(elasticsearchTransport());
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport() {
        if(properties.getHost() == null || properties.getHost().isBlank()) {
            return null;
        }
        log.info("elasticsearchTransport host:{} port:{} schema:{} username:{}",
                properties.getHost(),
                properties.getPort(),
                properties.getSchema(),
                properties.getUsername());

        boolean isHttps = "https".equalsIgnoreCase(properties.getSchema());

        RestClientBuilder builder = RestClient.builder(
                new HttpHost(properties.getHost(), properties.getPort(), isHttps ? "https" : "http")
        ).setHttpClientConfigCallback(httpClientBuilder -> configureHttpClient(httpClientBuilder, isHttps));

        return new RestClientTransport(builder.build(), new JacksonJsonpMapper());
    }

    private HttpAsyncClientBuilder configureHttpClient(HttpAsyncClientBuilder httpClientBuilder, boolean isHttps) {
        httpClientBuilder.disableAuthCaching();
        httpClientBuilder.setDefaultCredentialsProvider(getCredentialsProvider());

        if (isHttps) {
            httpClientBuilder.setSSLHostnameVerifier((hostname, session) -> true);
        }
        return httpClientBuilder;
    }

    private CredentialsProvider getCredentialsProvider() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword())
        );
        return credentialsProvider;
    }

    @Autowired
    public void setProperties(ElasticsearchAppenderProperties properties) {
        this.properties = properties;
    }
}
