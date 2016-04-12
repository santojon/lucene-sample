package com.sample.lucene.data

import com.sample.lucene.domain.Language

class LanguageDao {
    List findAll() {
        [new Language(name: 'Java'), new Language(name: 'Groovy'), new Language(name: 'Scala')]
    }
}