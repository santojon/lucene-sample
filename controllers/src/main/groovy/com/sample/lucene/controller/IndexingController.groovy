package com.sample.lucene.controller

import com.sample.lucene.service.IndexingService
/**
 * Controller Responsible for indexing and searching
 */
class IndexingController {
    // The repated service
    def service = new IndexingService()
    
    /**
     * Return search results for queries from UI
     * @param term: the term to search
     * @return: a list of results
     */
    List getResultsFor(String term = 'marvel', boolean force = false) {
        if (force) {
            service.createIndex()
        } else {
            service.isIndexed() ?: service.createIndex()
        }
        
        return service.searchIndex(term ?: 'marvel')
    }
}