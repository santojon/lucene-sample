package com.sample.lucene.controller

import com.sample.lucene.service.LanguageService

class LanguageController {
    def service = new LanguageService()
    
    String getGroovyValue() {
        service.findGroovy()?.name ?: 'Groovy language not found'
    }
}