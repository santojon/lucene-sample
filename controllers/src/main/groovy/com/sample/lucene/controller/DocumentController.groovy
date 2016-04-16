package com.sample.lucene.controller

import com.sample.lucene.service.DocumentService

class DocumentController {
    def service = new DocumentService()
    
    String fetch() {
        service.fetchDocs()
        return 'Done!'
    }
}