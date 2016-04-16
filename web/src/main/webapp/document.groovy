import com.sample.lucene.controller.DocumentController

def controller = new DocumentController()

html.html {
    head {
        title "Simple page"
    }
    body {
        h1 "Simple page"
        p controller.fetch()
    }
}