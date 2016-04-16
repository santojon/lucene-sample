package com.sample.lucene.service

//import java.io.*
//import java.util.*
import org.apache.commons.io.*

import org.apache.lucene.analysis.*
import org.apache.lucene.analysis.util.*
import org.apache.lucene.analysis.standard.*
import org.apache.lucene.document.*
import org.apache.lucene.index.*
import org.apache.lucene.queryparser.*
import org.apache.lucene.queryparser.classic.*
import org.apache.lucene.queryparser.flexible.standard.*
import org.apache.lucene.store.*
import org.apache.lucene.search.*
import org.apache.lucene.util.*

/**
 * Service responsible to handle searchs and indexing documents
 */
class IndexingService {
	
    // Directories used to get indexes/set indexes
    public static final String FILES_TO_INDEX_DIRECTORY = '../data'
	public static final String INDEX_DIRECTORY = '../index'

	// Index parameters
	public static final String FIELD_PATH = 'path'
	public static final String FIELD_CONTENTS = 'contents'
	
	// Pagination value
	public static final int MAX_PAGINATION = 10000
	
	// Stopwords
	public static final CharArraySet ENGLISH_STOP_WORDS =
		new CharArraySet(Version.LUCENE_40, [
		    'a', 'about', 'above', 'after', 'again', 'against', 'all', 'am', 'an',
		    'and', 'any', 'are', 'aren\'t', 'as', 'at', 'be', 'because', 'been',
		    'before', 'being', 'below', 'between', 'both', 'but', 'by', 'can\'t',
		    'cannot', 'could', 'couldn\'t', 'did', 'didn\'t', 'do', 'does', 'doesn\'t',
		    'doing', 'don\'t', 'down', 'during', 'each', 'few', 'for', 'from',
		    'further', 'had', 'hadn\'t', 'has', 'hasn\'t', 'have', 'haven\'t',
		    'having', 'he', 'he\'d', 'he\'ll', 'he\'s', 'her', 'here', 'here\'s',
		    'hers', 'herself', 'him', 'himself', 'his', 'how', 'how\'s', 'i', 'i\'d',
		    'i\'ll', 'i\'m', 'i\'ve', 'if', 'in', 'into', 'is', 'isn\'t', 'it',
		    'it\'s', 'its', 'itself', 'let\'s', 'me', 'more', 'most', 'mustn\'t',
		    'my', 'myself', 'no', 'nor', 'not', 'of', 'off', 'on', 'once', 'only',
		    'or', 'other', 'ought', 'our', 'ours','ourselves', 'out', 'over', 'own',
		    'same', 'shan\'t', 'she', 'she\'d', 'she\'ll', 'she\'s', 'should',
		    'shouldn\'t', 'so', 'some', 'such', 'than', 'that', 'that\'s', 'the',
		    'their', 'theirs', 'them', 'themselves', 'then', 'there', 'there\'s',
		    'these', 'they', 'they\'d', 'they\'ll', 'they\'re', 'they\'ve', 'this',
		    'those', 'through', 'to', 'too', 'under', 'until', 'up', 'very', 'was',
		    'wasn\'t', 'we', 'we\'d', 'we\'ll', 'we\'re', 'we\'ve', 'were', 'weren\'t',
		    'what', 'what\'s', 'when', 'when\'s', 'where', 'where\'s', 'which',
		    'while', 'who', 'who\'s', 'whom', 'why', 'why\'s', 'with', 'won\'t',
		    'would', 'wouldn\'t', 'you', 'you\'d', 'you\'ll', 'you\'re', 'you\'ve',
		    'your', 'yours', 'yourself', 'yourselves'
		], true)
    
    /**
     * Create the indexes for searchs
     */
    public static void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
    	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40, ENGLISH_STOP_WORDS)
        
        // Directory to put indexes
		File inDir = new File(INDEX_DIRECTORY)
		Directory indexDirectory = FSDirectory.open(inDir)
		println 'dir --> ' + inDir.path
		
		// Set up directory to write
		IndexWriterConfig writerConfiguration = new IndexWriterConfig(Version.LUCENE_40, analyzer)
        IndexWriter indexWriter = new IndexWriter(indexDirectory, writerConfiguration)
		
		// Set up the files directory
		File dir = new File(FILES_TO_INDEX_DIRECTORY)
		File[] files = dir.listFiles()
		println 'folder (' + files.length + ') --> ' + dir.path
		
		// Create indexes for files
		for (int i = 0; i < files.length; i++) {
			
			// try to create a new indexed document from file
		    try {
    		    File file = files[i]
    			Document document = new Document()
    
    			// file path
    			String path = file.getCanonicalPath()
    			document.add(new TextField(FIELD_PATH, path, Field.Store.YES))
    
    			// file content
    			String reader = FileUtils.readFileToString(file)
    			document.add(new TextField(FIELD_CONTENTS, reader, Field.Store.YES))
    			println 'file (' + (i + 1) + ') --> ' + document."$FIELD_PATH"
    
    			// add it to index
    			indexWriter.addDocument(document)
		    } catch (Exception e) {
		    	// bypass errors
		        i++
		    }
		}
		
		// close used resources
		indexWriter.close()
		indexDirectory.close()
	}

	/**
     * Searches for values in documents content
     * @param searchString: The searching value
     * @return: A list of results
     */
	public static List searchIndex(String searchString) throws IOException, ParseException {
	    List result = []
		println 'Searching for --> \'' + searchString + '\''
		
		// Directory to read indexes
		File inDir = new File(INDEX_DIRECTORY)
		Directory directory = FSDirectory.open(inDir)
		
		// set up structures used to search indexes
		IndexReader indexReader = DirectoryReader.open(directory)
		IndexSearcher indexSearcher = new IndexSearcher(indexReader)
		println 'dir --> ' + inDir.path

		// create query structure
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40, ENGLISH_STOP_WORDS)
		Query query = new StandardQueryParser(analyzer).parse("$FIELD_CONTENTS:$searchString", '')
		
		// Search
		List hits =  indexSearcher.search(query, MAX_PAGINATION).scoreDocs
		println 'Number of hits --> ' + hits.size
        
        // Collect data from results
        hits.collect{indexSearcher.doc(it.doc)}.each { document ->
        	String path = document."$FIELD_PATH"
			println 'Hit --> ' + path
			
			result.add(document."$FIELD_PATH")
        }
        
        // close resources and return
		directory.close()
        return result
	}
	
	/**
	 * Verifies if the files are indexed
	 * @return: true if indexed, false, otherwise
	 */
	public static boolean isIndexed() {
		File inDir = new File(INDEX_DIRECTORY)
		return (inDir?.isDirectory() && (inDir?.list()?.length > 0))
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