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
       * NAVBAR!!!
       */
       
      div(class: 'navbar navbar-inverse navbar-fixed-top') {
          a(class: 'brand',
            href: 'http://beta.groovy-lang.org/docs/groovy-2.3.2/html/documentation/markup-template-engine.html',
              'Groovy - Template Engine docs')
      }
      
      /*
       * This part is the main page
       */
      
      div(class: 'container-fluid') {
            h1 "Results"
            controller.search('war').each { String result ->
                p result
            }
      }
      
    }
}