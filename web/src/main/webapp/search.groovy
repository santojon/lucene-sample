import com.sample.lucene.controller.IndexingController

def controller = new IndexingController()

html.html {
    head {
        // Page title
        title "Results"
        
        // CSS!String
        link(rel: 'stylesheet', href: 'https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css')
        link(rel: 'stylesheet', href: '/assets/css/style.css')
    }
    body {
            /*
       * This part is the main page
       */
      
      div(class: 'container-fluid') {
            h1 "Results"
            controller.getResultsFor('war', true).each { String result ->
                p result
            }
      }
      
    }
}