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

class DocumentService {
    def dao = new DocumentDao()
    
    void fetchDocs() {
        dao.docUrls().each { String strUrl ->
            URL url

    		try {
                def http = new HTTPBuilder(strUrl)
                def html = http.get([:])
                
                //println html.toString()
                
    			// get URL content
    			url = new URL(strUrl)
    			URLConnection conn = url.openConnection()
    
    			// open the stream and put it into BufferedReader
    			BufferedReader br = new BufferedReader(
                                   new InputStreamReader(conn.getInputStream()))
    
    			String inputLine
    
    			//save to this filename
    			String fileName = "/home/ubuntu/workspace/data/${strUrl.hashCode()}.html"
    			File file = new File(fileName)
    
    			if (!file.exists()) {
    				file.createNewFile()
    			}
    
    			//use FileWriter to write file
    			FileWriter fw = new FileWriter(file.getAbsoluteFile())
    			BufferedWriter bw = new BufferedWriter(fw)
    
    			while ((inputLine = br.readLine()) != null) {
    				bw.write(inputLine)
    			}
    
    			bw.close()
    			br.close()
    
    			println "'${strUrl}' successfully fetched!"
    
    		} catch (MalformedURLException e) {
    			e.printStackTrace()
    		} catch (IOException e) {
    			e.printStackTrace()
    		}
        }
        //googleIt()
    }
    
    void googleIt() {
        def http = new HTTPBuilder()
        def json =  new JsonSlurper()

        for (int pageNum = 0; pageNum < 18; pageNum = pageNum + 8) {
            http.request( 'http://ajax.googleapis.com', Method.GET, ContentType.TEXT ) { req ->
              uri.path = '/ajax/services/search/web'
              uri.query = [ v:'1.0', q: 'test', rsz: 'large', start: "$pageNum" ]
              headers.'User-Agent' = "Mozilla/5.0 Firefox/3.0.4"
              headers.Accept = 'application/json'
            
              response.success = { resp, reader ->
                assert resp.statusLine.statusCode == 200
                println "Got response: ${resp.statusLine}"
                println "Content-Type: ${resp.headers.'Content-Type'}"
                def values = json.parseText(reader.text)
                values['responseData']['results'].each {
                    println it['url']
                }
              }
            
              response.'404' = {
                println 'Not found'
              }
            }
            
            if (pageNum == 0) {
                pageNum = 1
            }
        }
    }
}