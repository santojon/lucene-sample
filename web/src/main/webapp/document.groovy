import com.sample.lucene.controller.DocumentController

def controller = new DocumentController()

html.html {
    head {
        title "Documents renoval"
    }
    body {
        h4 "Fetching documents from web to server..."
        controller.fetch().each { String failure ->
            p "Document from '$failure' fails to fetch.. :("
        }
    }
}