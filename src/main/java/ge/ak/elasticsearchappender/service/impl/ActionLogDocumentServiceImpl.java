package ge.ak.elasticsearchappender.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.OpType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.ak.elasticsearchappender.logging.ActionLogDocument;
import ge.ak.elasticsearchappender.logging.ElasticsearchAppenderProperties;
import ge.ak.elasticsearchappender.service.ActionLogDocumentService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.StringReader;


@Service
@ConditionalOnProperty(name = "elasticsearch.appender.elastic.enable", havingValue = "true", matchIfMissing = false)
public class ActionLogDocumentServiceImpl implements ActionLogDocumentService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ActionLogDocumentServiceImpl.class);

    private ElasticsearchAppenderProperties properties;
    private ElasticsearchAsyncClient elasticsearchAsyncClient;
    private ObjectMapper objectMapper;

    public void indexAsync(ActionLogDocument actionLogDocument) {

        elasticsearchAsyncClient.index(i -> i.index(properties.getActionLogIndexName()).id(actionLogDocument.getId()).opType(OpType.Create)
                .withJson(new StringReader(stringify(actionLogDocument)))
        ).whenComplete((response, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            }
        });
    }

    public String stringify(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error while stringify log object");
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setElasticsearchAsyncClient(ElasticsearchAsyncClient elasticsearchAsyncClient) {
        this.elasticsearchAsyncClient = elasticsearchAsyncClient;
    }

    @Autowired
    public void setProperties(ElasticsearchAppenderProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}