package com.sample.lucene.web

import com.sample.lucene.service.*

class LanguageHelper {
    def service = new LanguageService()
    def i = new IndexingService()
    
    String getGroovyValue() {
        i.run()
        service.findGroovy()?.name ?: 'Groovy language not found'
    }
}