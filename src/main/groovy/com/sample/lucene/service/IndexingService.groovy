package com.sample.lucene.service

//import java.io.*
//import java.util.*
import org.apache.commons.io.*

import org.apache.lucene.analysis.*
import org.apache.lucene.analysis.en.*
import org.apache.lucene.analysis.core.*
import org.apache.lucene.analysis.tokenattributes.*
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

import com.sample.lucene.dao.DocumentDao

/**
 * Service responsible to handle searchs and indexing documents
 */
class IndexingService {
	def docDao = new DocumentDao()
	
	List<List<Integer>> matrix = [[]]
	
    // Directories used to get indexes/set indexes
    public static final String FILES_TO_INDEX_DIRECTORY = '/home/ubuntu/workspace/data'
	public static final String INDEX_DIRECTORY = '/home/ubuntu/workspace/index/base'

	// Index parameters
	public static final String FIELD_PATH = 'path'
	public static final String FIELD_CONTENTS = 'contents'
	
	// Pagination value
	public static final int MAX = 10000
	
	// Words used to Stemming and Stopwords
	public static final List WORDS = [
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
		    'your', 'yours', 'yourself', 'yourselves', 'último', 'é', 'acerca',
		    'agora', 'algumas', 'alguns', 'ali', 'ambos', 'antes', 'apontar',
		    'aquela', 'aquelas', 'aquele', 'aqueles', 'aqui', 'atrás', 'bem', 'bom',
		    'cada', 'caminho', 'cima', 'com', 'como', 'comprido', 'conhecido',
		    'corrente', 'das', 'debaixo', 'dentro', 'desde', 'desligado', 'deve',
		    'devem', 'deverá', 'direita', 'diz', 'dizer', 'dois', 'dos', 'e', 'ela',
		    'ele', 'eles', 'em', 'enquanto', 'então', 'está', 'estão', 'estado',
		    'estar', 'estará', 'este', 'estes', 'esteve', 'estive', 'estivemos',
		    'estiveram', 'eu', 'fará', 'faz', 'fazer', 'fazia', 'fez', 'fim', 'foi',
		    'fora', 'horas', 'iniciar', 'inicio', 'ir', 'irá', 'ista', 'iste', 'isto',
		    'ligado', 'maioria', 'maiorias', 'mais', 'mas', 'mesmo', 'meu', 'muito',
		    'muitos', 'nós', 'não', 'nome', 'nosso', 'novo', 'o', 'onde', 'os', 'ou',
		    'outro', 'para', 'parte', 'pegar', 'pelo', 'pessoas', 'pode', 'poderá',
		    'podia', 'por', 'porque', 'povo', 'promeiro', 'quê', 'qual', 'qualquer',
		    'quando', 'quem', 'quieto', 'são', 'saber', 'sem', 'ser', 'seu', 'somente',
		    'têm', 'tal', 'também', 'tem', 'tempo', 'tenho', 'tentar', 'tentaram',
		    'tente', 'tentei', 'teu', 'teve', 'tipo', 'tive', 'todos', 'trabalhar',
		    'trabalho', 'tu', 'um', 'uma', 'umas', 'uns', 'usa', 'usar', 'valor',
		    'veja', 'ver', 'verdade', 'verdadeiro', 'você'
		]
	
	// Stopwords
	public static final CharArraySet STOP_WORDS =
		new CharArraySet(Version.LUCENE_40, WORDS, true)
    
    /**
     * Create the indexes for searchs
     */
    public static void createIndex(boolean stop = true, boolean stem = true)
    		throws CorruptIndexException, LockObtainFailedException, IOException {
    	
    	Analyzer analyzer = stop ?
    			new StandardAnalyzer(Version.LUCENE_40, STOP_WORDS) :
    			new StandardAnalyzer(Version.LUCENE_40)
        
        // Directory to put indexes
		File inDir
		if (stop) {
			if (stem) {
				inDir = new File(INDEX_DIRECTORY + '4')
			} else {
				inDir = new File(INDEX_DIRECTORY + '2')
			}
		} else if (stem) {
			inDir = new File(INDEX_DIRECTORY + '3')
		} else {
			inDir = new File(INDEX_DIRECTORY + '1')
		}
		
		// ensure it's clean
		FileUtils.cleanDirectory(inDir)
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
    			
    			if (stem) {
		    		TokenStream tokenStream = new StandardTokenizer(
		    			Version.LUCENE_40, new StringReader(reader)
		    		)
			        
			        if (stop) {
			        	tokenStream = new StopFilter(Version.LUCENE_40, tokenStream, STOP_WORDS)
			        }
			        tokenStream = new PorterStemFilter(tokenStream)
			 
			        StringBuilder sb = new StringBuilder()
			        
			        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class)
			        CharTermAttribute charTermAttr = tokenStream.getAttribute(CharTermAttribute.class)
			        try{
			            while (tokenStream.incrementToken()) {
			                if (sb.length() > 0) {
			                    sb.append(" ")
			                }
			                sb.append(charTermAttr.toString())
			            }
			        }
			        catch (IOException e){
			            println(e.message)
			        }
			        reader = sb.toString()
    			}
    			
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
	public List searchIndex(
			String searchString,
			boolean stop = true,
			boolean stem = true) throws IOException, ParseException {
				
	    List result = []
	    
		println 'Searching for --> \'' + searchString + '\''
		
		// Directory to read indexes
		File inDir
		if (stop) {
			if (stem) {
				inDir = new File(INDEX_DIRECTORY + '4')
			} else {
				inDir = new File(INDEX_DIRECTORY + '2')
			}
		} else if (stem) {
			inDir = new File(INDEX_DIRECTORY + '3')
		} else {
			inDir = new File(INDEX_DIRECTORY + '1')
		}
		Directory directory = FSDirectory.open(inDir)
		
		// set up structures used to search indexes
		IndexReader indexReader = DirectoryReader.open(directory)
		IndexSearcher indexSearcher = new IndexSearcher(indexReader)
		println 'dir --> ' + inDir.path

		// create query structure
		Analyzer analyzer = stop ?
    			new StandardAnalyzer(Version.LUCENE_40, STOP_WORDS) :
    			new StandardAnalyzer(Version.LUCENE_40)
		Query query = new StandardQueryParser(analyzer).parse("$FIELD_CONTENTS:$searchString", '')
		
		// Search
		List hits =  indexSearcher.search(query, MAX).scoreDocs
		println 'Number of hits --> ' + hits.size
        
        //hits.each { document ->
        //	println ' Score --> ' + document.score
        //}
        
        List hitsIds = []
        
        // Collect data from results
        hits.collect{indexSearcher.doc(it.doc)}.each { document ->
        	String path = document."$FIELD_PATH"
			println 'Hit --> ' + path
			
			// parse path to retrieve real URL
			List parts = path.split('-doc')
			List anotherParts = parts[parts.size - 1].split('.html')
			String pos = anotherParts[anotherParts.size - 1]
			
			hitsIds.add(Integer.parseInt(pos))
			// Get real URL from position ID and put it into results
			String originalLink = docDao.docUrls[Integer.parseInt(pos)]
			result.add(originalLink)
        }
        
        List failures = docDao.indexList - hitsIds
        println 'Hits --> ' + hitsIds
        println 'Misses --> ' + failures
        
        
        //initMatrix(3, docDao.indexList.size)
        
        // close resources and return
		directory.close()
        return result.unique { a, b -> a <=> b }
	}
	
	/**
	 * Verifies if the files are indexed
	 * @return: true if indexed, false, otherwise
	 */
	public static boolean isIndexed(int baseNum = 4) {
		File inDir = new File(INDEX_DIRECTORY + "$baseNum")
		return (inDir?.isDirectory() && (inDir?.list()?.length > 0))
	}
	
	//void initMatrix(int queriesNumber, int docBaseSize) {
	//	for () {
	//		for () {
	//			matrix[i][j] = 0
	//		}
	//	}
	//}
}