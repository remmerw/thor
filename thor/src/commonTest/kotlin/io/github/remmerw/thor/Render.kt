package io.github.remmerw.thor

import io.github.remmerw.thor.dom.Model
import io.github.remmerw.thor.dom.Element
import io.github.remmerw.thor.dom.Node
import io.github.remmerw.thor.dom.parseModel
import io.ktor.http.Url
import java.net.URL

class Render(var url: Url) {


    fun parse(model: Model) {
        var urlObj: URL?
        try {
            urlObj = URL(url.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

        // Open a connection to the HTML page and use Cobra to parse it.
        // Cobra does not return until page is loaded.
        try {
            val connection = urlObj.openConnection()
            val inputStream = connection.getInputStream()
            requireNotNull(inputStream)


            val document = parseModel( model, inputStream, "UTF-8")

            // Do a recursive traversal on the top-level DOM node.
            doTree( document.getDocumentElement()!!)
        } catch (e: Exception) {
            e.printStackTrace()
            println("parsePage($url):  $e")
            throw e
        }

    }

    /**
     * Recurse the DOM starting with Node node.  For each Node of
     * type Element, call doElement() with it and recurse over its
     * children.  The Elements refer to the HTML tags, and the children
     * are tags contained inside the parent tag.
     */
    fun doTree(node: Node) {
        if (node is Element) {
            // Visit tag.

            doElement(node)

            // Visit all the children, i.e., tags contained in this tag.
           node.children().forEach { node ->
                doTree(node)
            }


            println("</" + node.name + ">")
        }
    }


    fun doElement(element: Element) {
        println("<" + element.name + ">")

        if(element.hasAttributes()) {
            println("Attributes : " + element.attributes().toString())
        }

    }




}