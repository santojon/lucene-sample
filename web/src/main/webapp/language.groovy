import com.sample.lucene.controller.LanguageController

def controller = new LanguageController()

html.html {
    head {
        title "Simple page"
    }
    body {
        h1 "Simple page"
        p "My favorite language is '$controller.groovyValue'."
    }
}