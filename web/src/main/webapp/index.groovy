import com.sample.lucene.controller.IndexingController

def controller = new IndexingController()

html.html {
    head {
        title "Results"
        link(rel: 'stylesheet', href: 'https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css')
    }
    body {
        div(class: 'container') {
          div(class: 'navbar') {
            div(class: 'navbar-inner') {
              a(class: 'brand',
                  href: 'http://beta.groovy-lang.org/docs/groovy-2.3.2/html/documentation/markup-template-engine.html',
                  'Groovy - Template Engine docs')
              a(class: 'brand',
                  href: 'hhttp://projects.spring.io/spring-boot/') {
                yield 'Spring Boot docs'
              }
            }
            
            h1 "Results"
            controller.results().each { String result ->
                p result
            }
          }
        }
    }
}