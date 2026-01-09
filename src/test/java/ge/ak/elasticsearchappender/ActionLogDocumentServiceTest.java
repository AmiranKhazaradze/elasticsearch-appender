package ge.ak.elasticsearchappender;

import ge.ak.elasticsearchappender.logging.ActionLogDocument;
import ge.ak.elasticsearchappender.service.ActionLogDocumentService;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.math.BigInteger;
import java.util.Random;

@SpringBootTest
class ActionLogDocumentServiceTest {

    @Autowired
    private ActionLogDocumentService actionLogDocumentService;
    @Autowired
    private ApplicationContext context;


    @Test
    void testIndexAsync() {
        // Arrange
        ActionLogDocument actionLogDocument = new ActionLogDocument();
        String traceId = new BigInteger(64, new Random()).toString(16);
        MDC.put("traceId", traceId);
        actionLogDocument.setAppName(context.getId());

        actionLogDocument.setRequestBody("Test Log");

        actionLogDocumentService.indexAsync(actionLogDocument);

    }
}