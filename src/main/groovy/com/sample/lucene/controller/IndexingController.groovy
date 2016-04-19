package com.sample.lucene.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.*
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import com.sample.lucene.domain.*
import com.sample.lucene.service.IndexingService
/**
 * Controller Responsible for indexing and searching
 */
@Controller
class IndexingController {
    // The repated service
    def service = new IndexingService()
    
    /**
     * Return search results for queries from UI
     * @param term: the term to search
     * @return: a list of results
     */
    @RequestMapping("/")
    String indexing (
        @RequestParam(
            value="term",
            required=false,
            defaultValue="marvel"
        ) String term,
        @RequestParam(
            value="force",
            required=false,
            defaultValue="false"
        ) String force,
        Model model
    ) {
        if (Boolean.parseBoolean(force)) {
            service.createIndex()
        } else {
            service.isIndexed() ?: service.createIndex()
        }
        
        List result = service.searchIndex(term ?: 'marvel')
        model.addAttribute("searchData", new SearchData(term: term, results: result))
        return "indexing"
    }
    
    /**
     * Return search results for queries from UI
     * @param term: the term to search
     * @return: a list of results
     */
    @RequestMapping("/search")
    String search (SearchData model) {
        
        if (model.force) {
            service.createIndex()
        } else {
            service.isIndexed() ?: service.createIndex()
        }
        model.results = service.searchIndex(model.term ?: 'marvel')
        
        return "indexing"
    }
}