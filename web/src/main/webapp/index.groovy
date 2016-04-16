import com.sample.lucene.controller.IndexingController

def controller = new IndexingController()

html.html {
    head {
        // Page title
        title "Results"
        
        // CSS
        link(rel: 'stylesheet', href: '/assets/css/bootstrap.min.css')
        link(rel: 'stylesheet', href: '/assets/css/bootstrap-theme.css')
        link(rel: 'stylesheet', href: '/assets/css/style.css')
        
        // JS
        script(type: 'text/javascript', src: '/assets/js/bootstrap.min.js')
    }
    body {
      
      /*
       * NAVBAR!!!
       */
       
      div(class: 'navbar navbar-inverse navbar-fixed-top') {
          a(class: 'navbar-brand',  href: '/', 'LS')
          form(class: 'navbar-form navbar-left') {
            div(class: 'form-group') {
              input(type: 'text', class: 'form-control width300', placeholder: 'Search')
              button(class: 'btn btn-default btn-big') {
                span(class: 'glyphicon glyphicon-search')
              }
            }
          }
      }
      
      /*
       * This part is the main page
       */
      
      div(class: 'container-fluid') {
            h1 "Results"
            ol {
              controller.getResultsFor().each { String result ->
                  li {
                    a(href: result, result)
                  }
              }
            }
      }
      
    }
}