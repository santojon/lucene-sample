package com.sample.lucene.controller

import com.sample.lucene.service.IndexingService

class IndexingController {
    def service = new IndexingService()
    
    List results() {
        service.run()
    }
}