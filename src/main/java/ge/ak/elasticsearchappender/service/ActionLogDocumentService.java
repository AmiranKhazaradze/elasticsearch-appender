package ge.ak.elasticsearchappender.service;


import ge.ak.elasticsearchappender.logging.ActionLogDocument;

public interface ActionLogDocumentService {

    void indexAsync(ActionLogDocument actionLogDocument);
}
