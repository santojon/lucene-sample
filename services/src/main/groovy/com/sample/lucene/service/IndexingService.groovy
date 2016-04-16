package com.sample.lucene.service

import java.io.*
import java.util.*
import org.apache.commons.io.*
import org.apache.lucene.analysis.*
import org.apache.lucene.analysis.standard.*
import org.apache.lucene.document.*
import org.apache.lucene.index.*
import org.apache.lucene.queryparser.*
import org.apache.lucene.queryparser.classic.*
import org.apache.lucene.queryparser.flexible.standard.*
import org.apache.lucene.store.*
import org.apache.lucene.search.*
import org.apache.lucene.util.*

 
class IndexingService {
    
    public static final String FILES_TO_INDEX_DIRECTORY = '../data'
	public static final String INDEX_DIRECTORY = '../index'

	public static final String FIELD_PATH = 'path'
	public static final String FIELD_CONTENTS = 'contents'
    
    public static void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		def inDir = new File(INDEX_DIRECTORY)
		def indexDirectory = FSDirectory.open(inDir)
		println 'dir --> ' indexDirectory
		
		def analyzer = new StandardAnalyzer(Version.LUCENE_40)
		def writerConfiguration = new IndexWriterConfig(Version.LUCENE_40, analyzer)
        def indexWriter = new IndexWriter(indexDirectory, writerConfiguration)
		
		File dir = new File(FILES_TO_INDEX_DIRECTORY)
		File[] files = dir.listFiles()
		println 'dir ' + files.length + ' --> ' files
		
		for (int i = 0; i < files.length; i++) {
		    try {
    		    File file = files[i]
    			Document document = new Document()
    
    			String path = file.getCanonicalPath()
    			document.add(new TextField(FIELD_PATH, path, Field.Store.YES))
    
    			String reader = FileUtils.readFileToString(file)
    			document.add(new TextField(FIELD_CONTENTS, reader, Field.Store.YES))
    			println 'dir ' + (i + 1) + ' --> ' document."$FIELD_PATH"
    
    			indexWriter.addDocument(document)
		    } catch (Exception e) {
		        i++
		    }
		}
		
		//indexWriter.optimize()
		indexWriter.close()
	}

	public static List searchIndex(String searchString) throws IOException, ParseException {
	    List result = []
		println("Searching for '" + searchString + "'")
		
		def directory = FSDirectory.open(INDEX_DIRECTORY)
		def indexReader = IndexReader.open(directory)
		def indexSearcher = new IndexSearcher(indexReader)

		def analyzer = new StandardAnalyzer(Version.LUCENE_40)
		QueryParser queryParser = new QueryParser(FIELD_CONTENTS, analyzer)
		Query query = queryParser.parse(searchString)
		ScoreDoc hits = indexSearcher.search(query)
		println("Number of hits: " + hits.length())

		Iterator<ScoreDoc> it = hits.iterator()
		while (it.hasNext()) {
			ScoreDoc hit = it.next()
			Document document = hit.getDocument()
			String path = document.get(FIELD_PATH)
			println("Hit: " + path)
			result.add(document."$FIELD_CONTENTS")
		}

        return result
	}
    
    List index() {
        List result = []
        // This script indexes the text from shakespeare.txt and indexes each line. 
        // It then search this index for a value passed in as an argument.
        
        // Search for a line containing the first past argument if passed, 
        // otherwise search for lines with monkey. 
        def searchTerm = "line:monkey"
        
        // Setup required lucene objects for writing to the lucene index. 
        def indexDirectory = new RAMDirectory()
        def analyzer = new StandardAnalyzer(Version.LUCENE_40)
        def writerConfiguration = new IndexWriterConfig(Version.LUCENE_40, analyzer)
        def indexWriter = new IndexWriter(indexDirectory, writerConfiguration)
    
        // Index the shakespeare text file line by line.
        new File("/home/ubuntu/workspace/Text.txt").readLines().eachWithIndex { line, lineNumber ->
        	Document doc = new Document()
        	doc.add(new IntField("lineNumber", lineNumber, Field.Store.YES))
        	doc.add(new TextField("line", line, Field.Store.YES))
        	indexWriter.addDocument(doc)
        }
        
        // Print out each line which matches the search term, with a return limit of 10000 matches.
        def indexReader = indexWriter.getReader()
        def query = new StandardQueryParser(analyzer).parse(searchTerm, "")
        def indexSearcher = new IndexSearcher(indexReader)
        def hits =  indexSearcher.search(query, 10000).scoreDocs
        
        hits.collect{indexSearcher.doc(it.doc)}.each {
            result.add("${it.lineNumber} ${it.line}")
        }
        
        println "${hits.length} matches for ${searchTerm - 'line:'} found."
        
        // Tidy up resources
        indexReader.close()
        indexWriter.close()
    }
}