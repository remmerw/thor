package io.github.remmerw.thor

import io.github.remmerw.thor.dom.DocumentImpl
import io.github.remmerw.thor.dom.ElementImpl
import io.github.remmerw.thor.dom.parseDocument
import io.ktor.http.Url
import org.w3c.dom.Node
import java.net.URL

class Render(var url: Url) {


    fun parsePage(): DocumentImpl {
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


            val document = parseDocument(inputStream, url, "UTF-8")

            // Do a recursive traversal on the top-level DOM node.
            doTree( document.documentElement)
            return document
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
    fun doTree(node: Node?) {
        if (node is ElementImpl) {
            // Visit tag.

            doElement(node)

            // Visit all the children, i.e., tags contained in this tag.
            val nl = node.childNodes
            val num = nl.length
            for (i in 0..<num) doTree(nl.item(i))

        }
    }


    fun doElement(element: ElementImpl) {
        println("<" + element.tagName + ">")

        println("Attributes : " + element.attributes().toString())

    }




}