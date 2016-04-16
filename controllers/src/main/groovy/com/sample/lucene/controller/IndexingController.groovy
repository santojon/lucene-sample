package com.sample.lucene.controller

import com.sample.lucene.service.IndexingService

class IndexingController {
    def service = new IndexingService()
    
    List results() {
        service.createIndex()
        return service.searchIndex('marvel')
    }
    
    List getResultsFor(String term) {
        return service.search(term)
    }
}