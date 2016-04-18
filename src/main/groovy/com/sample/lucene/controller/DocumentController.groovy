package com.sample.lucene.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

import com.sample.lucene.service.DocumentService

@Controller
public class DocumentController {
    def service = new DocumentService()
    
    @RequestMapping("/documents")
    String documents(Model model) {
        List failed = service.fetchDocs()
        model.addAttribute("documents", failed)
        return "documents"
    }
}