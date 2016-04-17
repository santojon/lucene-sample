package com.sample.lucene.service

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection

import groovyx.net.http.*
import groovy.json.JsonSlurper

import com.sample.lucene.data.DocumentDao

/**
 * Service responsible to handle document related things
 * This service uses to refresh doc base
 */
class DocumentService {
    // related data access object
    def dao = new DocumentDao()
    
    /**
     * Renew document base with last version of docs from web
     * @return: the list of failures
     */
    public List fetchDocs() {
        List failures = []
        
        // for all registered sites, do
        dao.docUrls.eachWithIndex { String strUrl, int i ->
        
            URL url
    		try {
    			// get URL content
    			url = new URL(strUrl)
    			URLConnection conn = url.openConnection()
    
    			// open the stream and put it into BufferedReader
    			BufferedReader br = new BufferedReader(
                                   new InputStreamReader(conn.getInputStream()))
    
    			// save to this filename
    			String fileName = "/home/ubuntu/workspace/data/${strUrl.hashCode()}-doc${i}.html"
    			File file = new File(fileName)
    
    			if (!file.exists()) {
    				file.createNewFile()
    			}
    
    			// use FileWriter to write file
    			String inputLine
    			FileWriter fw = new FileWriter(file.getAbsoluteFile())
    			BufferedWriter bw = new BufferedWriter(fw)
    
    			while ((inputLine = br.readLine()) != null) {
    				bw.write(inputLine)
    			}
    
                // free the resources
    			bw.close()
    			br.close()
    
    		} catch (MalformedURLException e) {
    			e.printStackTrace()
    			failures.add(strUrl)
    		} catch (IOException e) {
    		    if (!e.message.contains('403')) {
    		        e.printStackTrace()
    		    }
    		    failures.add(strUrl)
    		}
    		println "'${strUrl}' successfully fetched!"
        }
        googleIt()
        return failures
    }
    
    /**
     * Search for a theme in google
     */
    void googleIt(String query = 'marvel') {
        
        // create resources
        def http = new HTTPBuilder()
        def json =  new JsonSlurper()

        // to get some pages
        for (int pageNum = 0; pageNum < 250; pageNum = pageNum + 8) {
            // make the request
            http.request( 'http://ajax.googleapis.com', Method.GET, ContentType.TEXT ) { req ->
              uri.path = '/ajax/services/search/web'
              uri.query = [ v:'1.0', q: query, rsz: 'large', start: "$pageNum" ]
              headers.'User-Agent' = "Mozilla/5.0 Firefox/3.0.4"
              headers.Accept = 'application/json'
            
              // consume succes response
              response.success = { resp, reader ->
                assert resp.statusLine.statusCode == 200
                println "Got response: ${resp.statusLine}"
                println "Content-Type: ${resp.headers.'Content-Type'}"
                def values = json.parseText(reader.text)
                values['responseData']['results'].each {
                    println it['url']
                }
              }
            
              // Not Found :(
              response.'404' = {
                println 'Not found'
              }
              
              // Denied :(
              response.'403' = {
                println 'Denied'
              }
            }
            
            // WA
            if (pageNum == 0) {
                pageNum = 1
            }
        }
    }
}