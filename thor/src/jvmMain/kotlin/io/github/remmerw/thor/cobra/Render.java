package io.github.remmerw.thor.cobra;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.remmerw.thor.cobra.html.parser.DocumentBuilderImpl;
import io.github.remmerw.thor.cobra.html.parser.InputSourceImpl;
import io.github.remmerw.thor.cobra.ua.UserAgentContext;

/**
 * Render - This object is a wrapper for the Cobra Toolkit, which is part
 * of the Lobo Project (http://html.xamjwg.org/index.jsp).  Cobra is a
 * "pure Java HTML renderer and DOM parser."
 * <p>
 * Render opens a URL, uses Cobra to render that HTML and apply JavaScript.
 * It then does a simple tree traversal of the DOM to print beginning and
 * end tag names.
 * <p>
 * Subclass this class and override the
 * <i>doElement(org.w3c.dom.Element element)</i> and
 * <i>doTagEnd(org.w3c.dom.Element element)</i> methods to do some real
 * work.  In the base class, doElement() prints the tag name and
 * doTagEnd() prints a closing version of the tag.
 * <p>
 * This class is a rewrite of org.benjysbrain.htmlgrab.Render that uses
 * JRex.
 * <p>
 * Copyright (c) 2008 by Ben E. Cline.  This code is presented as a teaching
 * aid.  No warranty is expressed or implied.
 * <p>
 * http://www.benjysbrain.com/
 *
 * @author Benjy Cline
 * @version 1/2008
 */

public class Render {
    String url;            // The page to be processed.

    // These variables can be used in subclasses and are created from
    // url.  baseURL can be used to construct the absolute URL of the
    // relative URL's in the page.  hostBase is just the http://host.com/
    // part of the URL and can be used to construct the full URL of
    // URLs in the page that are site relative, e.g., "/xyzzy.jpg".
    // Variable host is set to the host part of url, e.g., host.com.

    String baseURL;
    String hostBase;
    String host;

    /**
     * Create a Render object with a target URL.
     */

    public Render(String url) {
        this.url = url;
    }


    public static void main(String[] args) {
        String url = "http://www.cnn.com/";
        if (args.length == 1) url = args[0];
        Render p = new Render(url);
        p.parsePage();
        System.exit(0);
    }

    /**
     * Load the given URL using Cobra.  When the page is loaded,
     * recurse on the DOM and call doElement()/doTagEnd() for
     * each Element node.  Return false on error.
     */

    public boolean parsePage() {
        // From Lobo forum.  Disable all logging.

        Logger.getLogger("").setLevel(Level.OFF);

        // Parse the URL and build baseURL and hostURL for use by doElement()
        // and doTagEnd() ;

        URI uri = null;
        URL urlObj = null;
        try {
            uri = new URI(url);
            urlObj = new URL(url);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

        String path = uri.getPath();

        host = uri.getHost();
        String port = "";
        if (uri.getPort() != -1) port = Integer.toString(uri.getPort());
        if (!port.equals("")) port = ":" + port;

        baseURL = "http://" + uri.getHost() + port + path;
        hostBase = "http://" + uri.getHost() + port;

        // Open a connection to the HTML page and use Cobra to parse it.
        // Cobra does not return until page is loaded.

        try {
            URLConnection connection = urlObj.openConnection();
            InputStream in = connection.getInputStream();

            UserAgentContext context = new SimpleUserAgentContext();
            DocumentBuilderImpl dbi = new DocumentBuilderImpl(context);
            Document document = dbi.parse(new InputSourceImpl(in, url,
                    "ISO-8859-1"));

            // Do a recursive traversal on the top-level DOM node.

            Element ex = document.getDocumentElement();
            doTree(ex);
        } catch (Exception e) {
            System.out.println("parsePage(" + url + "):  " + e);
            return false;
        }

        return true;
    }

    /**
     * Recurse the DOM starting with Node node.  For each Node of
     * type Element, call doElement() with it and recurse over its
     * children.  The Elements refer to the HTML tags, and the children
     * are tags contained inside the parent tag.
     */

    public void doTree(Node node) {
        if (node instanceof Element element) {

            // Visit tag.

            doElement(element);

            // Visit all the children, i.e., tags contained in this tag.

            NodeList nl = element.getChildNodes();
            if (nl == null) return;
            int num = nl.getLength();
            for (int i = 0; i < num; i++)
                doTree(nl.item(i));

            // Process the end of this tag.

            doTagEnd(element);
        }
    }

    /**
     * Simple doElement to print the tag name of the Element.  Override
     * to do something real.
     */

    public void doElement(Element element) {
        System.out.println("<" + element.getTagName() + ">");
    }

    /**
     * Simple doTagEnd() to print the closing tag of the Element.
     * Override to do something real.
     */

    public void doTagEnd(Element element) {
        System.out.println("</" + element.getTagName() + ">");
    }
}
