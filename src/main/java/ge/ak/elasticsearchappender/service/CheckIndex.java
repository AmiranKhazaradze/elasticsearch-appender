package ge.ak.elasticsearchappender.service;

import ch.qos.logback.core.util.StringUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch.ilm.GetLifecycleRequest;
import co.elastic.clients.elasticsearch.ilm.IlmPolicy;
import co.elastic.clients.elasticsearch.ilm.PutLifecycleRequest;
import co.elastic.clients.elasticsearch.indices.CreateDataStreamRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.ExistsTemplateRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.PutIndexTemplateRequest;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import ge.ak.elasticsearchappender.logging.ElasticsearchAppenderProperties;
import ge.ak.elasticsearchappender.logging.ILM;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("checkIndex")
@ConditionalOnProperty(name = "elasticsearch.appender.elastic.create-index", havingValue = "true", matchIfMissing = false)
public class CheckIndex {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CheckIndex.class);

    private ElasticsearchAppenderProperties properties;
    private ElasticsearchClient elasticsearchClient;

    @Value("${elasticsearch.appender.elastic.enable}")
    Boolean enabled;

    private static String ILM_POLICY_NAME = "log-policy";
    private static String HOT_PHASE = "30d";
    private static String WARM_PHASE = "60d";
    private static String COLD_PHASE = "90d";
    private static String DELETE_PHASE = "365d";

    @PostConstruct
    public void checkIndexes() {
        if (enabled == null || !enabled) {
            return;
        }
        ILM iml = properties.getIml();
        if (iml != null) {
            if (StringUtil.notNullNorEmpty(iml.getPolicyName())) {
                ILM_POLICY_NAME = iml.getPolicyName();
            }
            if (StringUtil.notNullNorEmpty(iml.getHotPhase())) {
                HOT_PHASE = iml.getHotPhase();
            }
            if (StringUtil.notNullNorEmpty(iml.getWarmPhase())) {
                WARM_PHASE = iml.getWarmPhase();
            }
            if (StringUtil.notNullNorEmpty(iml.getColdPhase())) {
                COLD_PHASE = iml.getColdPhase();
            }
            if (StringUtil.notNullNorEmpty(iml.getDeletePhase())) {
                DELETE_PHASE = iml.getDeletePhase();
            }
        }

        String actionLogIndexName = properties.getActionLogIndexName();
        String systemLogIndexName = properties.getSystemLogIndexName();

        try {
            ensureILMPolicy();
        } catch (IOException e) {
            log.error("Error ensuring ILM policy: {}", e.getMessage(), e);
            return;
        }

        if (StringUtil.notNullNorEmpty(actionLogIndexName)) {
            ensureDataStream(actionLogIndexName);
        }

        if (StringUtil.notNullNorEmpty(systemLogIndexName)) {
            ensureDataStream(systemLogIndexName);
        }
    }

    private void ensureDataStream(String indexName) {
        try {
            boolean exists = indexExists(indexName);
            if (!exists) {
                log.info("Index {} does not exist. Creating data stream...", indexName);
                createIndexTemplate(indexName);
                createDataStream(indexName);
            } else {
                log.info("Index {} already exists.", indexName);
            }
        } catch (Exception e) {
            log.error("Error checking/creating data stream for index {}: {}", indexName, e.getMessage(), e);
        }
    }

    private boolean indexExists(String indexName) {
        try {
            ExistsRequest request = new ExistsRequest.Builder().index(indexName).build();
            BooleanResponse exists = elasticsearchClient.indices().exists(request);
            return exists.value();
        } catch (Exception e) {
            log.error("Error checking index existence: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean templateExists(String templateName) {
        try {
            ExistsTemplateRequest request = new ExistsTemplateRequest.Builder().name(templateName).build();
            BooleanResponse exists = elasticsearchClient.indices().existsTemplate(request);
            return exists.value();
        } catch (IOException e) {
            log.error("Error checking template existence: {}", e.getMessage(), e);
            return false;
        }
    }

    private void createIndexTemplate(String indexName) throws IOException {
        String templateName = indexName + "-template";
        if (templateExists(templateName)) {
            log.info("Index template {} already exists.", templateName);
            return;
        }

        Map<String, JsonData> customSettings = new HashMap<>();
        customSettings.put("index.lifecycle.name", JsonData.of(ILM_POLICY_NAME));
        customSettings.put("index.number_of_shards", JsonData.of("1"));
        customSettings.put("index.auto_expand_replicas", JsonData.of("0-1"));
        customSettings.put("index.number_of_replicas", JsonData.of("0"));

        IndexSettings settings = new IndexSettings.Builder()
                .otherSettings(customSettings)
                .build();

        PutIndexTemplateRequest request = new PutIndexTemplateRequest.Builder()
                .name(templateName)
                .indexPatterns(List.of(indexName + "*"))
                .template(t -> t
                        .settings(settings)
                        .mappings(m -> m.properties("timestamp", p -> p.date(d -> d)))
                )
                .dataStream(d -> d)
                .priority(100)
                .build();

        elasticsearchClient.indices().putIndexTemplate(request);
        log.info("Created index template: {}", templateName);
    }


    private void createDataStream(String indexName) throws IOException {
        CreateDataStreamRequest request = new CreateDataStreamRequest.Builder()
                .name(indexName)
                .build();

        elasticsearchClient.indices().createDataStream(request);
        log.info("Created data stream: {}", indexName);
    }

    private boolean ilmPolicyExists() {
        try {
            GetLifecycleRequest request = new GetLifecycleRequest.Builder().build();
            return elasticsearchClient.ilm().getLifecycle(request).result().containsKey(ILM_POLICY_NAME);
        } catch (Exception e) {
            log.error("Error checking ILM policy existence: {}", e.getMessage());
            return false;
        }
    }

    private void ensureILMPolicy() throws IOException {
        if (ilmPolicyExists()) {
            log.info("ILM policy {} already exists.", ILM_POLICY_NAME);
            return;
        }

        log.info("Creating ILM policy {}", ILM_POLICY_NAME);

        Map<String, Object> rolloverAction = Map.of("max_age", HOT_PHASE);
        Map<String, Object> hotActions = Map.of("rollover", rolloverAction);
        Map<String, Object> warmActions = Map.of("set_priority", Map.of("priority", 50));
        Map<String, Object> coldActions = Map.of("set_priority", Map.of("priority", 20));
        Map<String, Object> deleteActions = Map.of("delete", Map.of());

        PutLifecycleRequest request = new PutLifecycleRequest.Builder()
                .name(ILM_POLICY_NAME)
                .policy(new IlmPolicy.Builder()
                        .phases(p -> p
                                .hot(h -> h.minAge(Time.of(t -> t.time("0ms")))
                                        .actions(JsonData.of(hotActions)))
                                .warm(w -> w.minAge(Time.of(t -> t.time(WARM_PHASE)))
                                        .actions(JsonData.of(warmActions)))
                                .cold(c -> c.minAge(Time.of(t -> t.time(COLD_PHASE)))
                                        .actions(JsonData.of(coldActions)))
                                .delete(d -> d.minAge(Time.of(t -> t.time(DELETE_PHASE)))
                                        .actions(JsonData.of(deleteActions)))
                        )
                        .build()
                )
                .build();

        elasticsearchClient.ilm().putLifecycle(request);
        log.info("Created ILM policy: {}", ILM_POLICY_NAME);
    }

    @Autowired
    public void setProperties(ElasticsearchAppenderProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setElasticsearchClient(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }
}
