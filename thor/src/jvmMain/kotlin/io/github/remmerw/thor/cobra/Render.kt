package io.github.remmerw.thor.cobra

import io.github.remmerw.thor.cobra.html.parser.DocumentBuilderImpl
import io.github.remmerw.thor.cobra.html.parser.InputSourceImpl
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.net.URI
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Render - This object is a wrapper for the Cobra Toolkit, which is part
 * of the Lobo Project (http://html.xamjwg.org/index.jsp).  Cobra is a
 * "pure Java HTML renderer and DOM parser."
 *
 *
 * Render opens a URL, uses Cobra to render that HTML and apply JavaScript.
 * It then does a simple tree traversal of the DOM to print beginning and
 * end tag names.
 *
 *
 * Subclass this class and override the
 * *doElement(org.w3c.dom.Element element)* and
 * *doTagEnd(org.w3c.dom.Element element)* methods to do some real
 * work.  In the base class, doElement() prints the tag name and
 * doTagEnd() prints a closing version of the tag.
 *
 *
 * This class is a rewrite of org.benjysbrain.htmlgrab.Render that uses
 * JRex.
 *
 *
 * Copyright (c) 2008 by Ben E. Cline.  This code is presented as a teaching
 * aid.  No warranty is expressed or implied.
 *
 *
 * http://www.benjysbrain.com/
 *
 * @author Benjy Cline
 * @version 1/2008
 */
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
    fun parsePage(): Boolean {
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
            return false
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

            val context: UserAgentContext = SimpleUserAgentContext()
            val dbi = DocumentBuilderImpl(context)
            val document = dbi.parse(
                InputSourceImpl(
                    inputStream, url,
                    "ISO-8859-1"
                )
            )

            // Do a recursive traversal on the top-level DOM node.
            val ex = document.documentElement
            doTree(ex)
        } catch (e: Exception) {
            println("parsePage(" + url + "):  " + e)
            return false
        }

        return true
    }

    /**
     * Recurse the DOM starting with Node node.  For each Node of
     * type Element, call doElement() with it and recurse over its
     * children.  The Elements refer to the HTML tags, and the children
     * are tags contained inside the parent tag.
     */
    fun doTree(node: Node?) {
        if (node is Element) {
            // Visit tag.

            doElement(node)

            // Visit all the children, i.e., tags contained in this tag.
            val nl = node.childNodes
            if (nl == null) return
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
    fun doElement(element: Element) {
        println("<" + element.tagName + ">")
    }

    /**
     * Simple doTagEnd() to print the closing tag of the Element.
     * Override to do something real.
     */
    fun doTagEnd(element: Element) {
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
