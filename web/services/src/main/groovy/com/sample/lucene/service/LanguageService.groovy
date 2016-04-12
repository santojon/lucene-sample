package com.sample.lucene.service

import com.sample.lucene.domain.Language
import com.sample.lucene.data.LanguageDao

class LanguageService {
    def dao = new LanguageDao()
    
    Language findGroovy() {
       dao.findAll().find { it.name == 'Groovy' }
    }
}