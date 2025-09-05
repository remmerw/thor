package io.github.remmerw.thor

import io.github.remmerw.thor.dom.ElementImpl
import io.github.remmerw.thor.parser.DocumentModelBuilder
import io.github.remmerw.thor.parser.InputSource
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.net.URI
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger

class Render(var url: String) {
    // These variables can be used in subclasses and are created from
    // url.  baseURL can be used to construct the absolute URL of the
    // relative URL's in the page.  hostBase is just the http://host.com/
    // part of the URL and can be used to construct the full URL of
    // URLs in the page that are site relative, e.g., "/xyzzy.jpg".
    // Variable host is set to the host part of url, e.g., host.com.
    var baseURL: String? = null
    var hostBase: String? = null
    var host: String? = null


    /**
     * Load the given URL using Cobra.  When the page is loaded,
     * recurse on the DOM and call doElement()/doTagEnd() for
     * each Element node.  Return false on error.
     */
    fun parsePage(): Document {
        // From Lobo forum.  Disable all logging.

        Logger.getLogger("").level = Level.OFF

        // Parse the URL and build baseURL and hostURL for use by doElement()
        // and doTagEnd() ;
        var uri: URI?
        var urlObj: URL?
        try {
            uri = URI(url)
            urlObj = URL(url)
        } catch (e: Exception) {
            println(e)
            throw e
        }

        val path = uri.path

        host = uri.host
        var port = ""
        if (uri.port != -1) port = uri.port.toString()
        if (port != "") port = ":" + port

        baseURL = "http://" + uri.host + port + path
        hostBase = "http://" + uri.host + port

        // Open a connection to the HTML page and use Cobra to parse it.
        // Cobra does not return until page is loaded.
        try {
            val connection = urlObj.openConnection()
            val inputStream = connection.getInputStream()
            requireNotNull(inputStream)


            val dbi = DocumentModelBuilder()
            val document = dbi.parse(
                InputSource(
                    inputStream,
                    url,
                    "ISO-8859-1"
                )
            )!!

            // Do a recursive traversal on the top-level DOM node.
            document.documentElement
            // doTree(ex)
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

            // Process the end of this tag.
            doTagEnd(node)
        }
    }

    /**
     * Simple doElement to print the tag name of the Element.  Override
     * to do something real.
     */
    fun doElement(element: ElementImpl) {
        println("<" + element.tagName + ">")

        println("Attributes : " + element.attributes().toString())

        println("Properties : " + element.properties().toString())

    }

    /**
     * Simple doTagEnd() to print the closing tag of the Element.
     * Override to do something real.
     */
    fun doTagEnd(element: ElementImpl) {
        println("</" + element.tagName + ">")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var url = "http://www.cnn.com/"
            if (args.size == 1) url = args[0]
            val p = Render(url)
            p.parsePage()
            System.exit(0)
        }
    }
}