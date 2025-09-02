/*    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Sep 3, 2005
 */
package io.github.remmerw.thor.dom

import cz.vutbr.web.css.CSSException
import cz.vutbr.web.css.ElementMatcher
import cz.vutbr.web.css.MediaSpec
import cz.vutbr.web.css.StyleSheet
import cz.vutbr.web.csskit.ElementMatcherSafeCS
import cz.vutbr.web.csskit.ElementMatcherSafeStd
import cz.vutbr.web.csskit.antlr4.CSSParserFactory
import cz.vutbr.web.domassign.Analyzer
import cz.vutbr.web.domassign.AnalyzerUtil
import io.github.remmerw.thor.core.Urls
import io.github.remmerw.thor.css.StyleSheetWrapper
import io.github.remmerw.thor.css.StyleSheetWrapper.Companion.getStyleSheets
import io.github.remmerw.thor.css.StyleSheetBridge
import io.github.remmerw.thor.dom.NodeFilter.AnchorFilter
import io.github.remmerw.thor.dom.NodeFilter.AppletFilter
import io.github.remmerw.thor.dom.NodeFilter.ElementNameFilter
import io.github.remmerw.thor.dom.NodeFilter.FormFilter
import io.github.remmerw.thor.dom.NodeFilter.LinkFilter
import io.github.remmerw.thor.dom.NodeFilter.TagNameFilter
import io.github.remmerw.thor.parser.EmptyReader
import io.github.remmerw.thor.parser.HtmlParser
import io.github.remmerw.thor.parser.WritableLineReader
import io.github.remmerw.thor.style.CSSNorm
import io.github.remmerw.thor.style.RenderState
import io.github.remmerw.thor.style.StyleSheetRenderState
import io.github.remmerw.thor.ua.UserAgentContext
import org.w3c.dom.Attr
import org.w3c.dom.CDATASection
import org.w3c.dom.Comment
import org.w3c.dom.DOMConfiguration
import org.w3c.dom.DOMException
import org.w3c.dom.DOMImplementation
import org.w3c.dom.DocumentFragment
import org.w3c.dom.DocumentType
import org.w3c.dom.Element
import org.w3c.dom.EntityReference
import org.w3c.dom.Node
import org.w3c.dom.Node.DOCUMENT_NODE
import org.w3c.dom.NodeList
import org.w3c.dom.ProcessingInstruction
import org.w3c.dom.Text
import org.w3c.dom.UserDataHandler
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLDocument
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.ranges.Range
import org.w3c.dom.stylesheets.DocumentStyle
import org.w3c.dom.stylesheets.LinkStyle
import org.w3c.dom.stylesheets.StyleSheetList
import org.w3c.dom.views.AbstractView
import org.w3c.dom.views.DocumentView
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXException
import java.io.IOException
import java.io.LineNumberReader
import java.io.Reader
import java.io.StringReader
import java.net.MalformedURLException
import java.net.URL
import java.util.logging.Level
import kotlin.concurrent.Volatile


/**
 * Implementation of the W3C `HTMLDocument` interface.
 */
class HTMLDocumentImpl(
    private val context: UserAgentContext,
    private var reader: WritableLineReader? = null,
    private var documentURI: String? = null,
    private val contentType: String? = null
) : NodeImpl(), DocumentModel, HTMLDocument, DocumentView, DocumentStyle {
    val styleSheetManager: StyleSheetManager = StyleSheetManager()
    private val factory: ElementFactory
    private var documentURL: URL? = null


    private val elementsById: MutableMap<String, Element?> = mutableMapOf()
    private val elementsByName: MutableMap<String?, Element?> = HashMap<String?, Element?>(0)
    private val documentNotificationListeners = ArrayList<DocumentNotificationListener>(1)

    /**
     * Gets an *immutable* set of locales previously set for this document.
     */

    @Volatile
    private var baseURI: String? = null
    private var title: String? = null
    private var referrer: String? = null
    private var domain: String? = null
    private var images: HTMLCollection? = null
    private var applets: HTMLCollection? = null
    private var links: HTMLCollection? = null
    private var forms: HTMLCollection? = null
    private var anchors: HTMLCollection? = null
    private var doctype: DocumentType? = null
    private var isDocTypeXHTML = false
    private var inputEncoding: String? = null
    private var xmlEncoding: String? = null
    private var xmlStandalone = false
    private var xmlVersion: String? = null
    private var strictErrorChecking = true
    private var domConfig: DOMConfiguration? = null
    private var domImplementation: DOMImplementation? = null
    private var body: HTMLElement? = null


    private var oldPendingTaskId = -1
    private var classifiedRules: Analyzer.Holder? = null


    init {
        this.factory = ElementFactory.Companion.instance
        try {
            val docURL = URL(documentURI)

            this.documentURL = docURL
            this.domain = docURL.host
        } catch (mfu: MalformedURLException) {
            logger.warning("HTMLDocumentImpl(): Document URI [" + documentURI + "] is malformed.")
        }

        // TODO: This should be inside the try block above. That is, if there is a malformed URL, the below shouldn't be allowed.
        //       It is currently being allowed to quickly bootstrap and run web-platform-tests.
        //       One failure case is: The methods in DOMImplemenationImpl call those constructors which have null document URIs.
        //       Such constructors should be ideally removed.
        this.document = this
        // Get Window object

    }

    override fun getDocumentURL(): URL? {
        return documentURL
    }

    val documentHost: String?
        get() {
            val docUrl = this.getDocumentURL()
            return if (docUrl == null) null else docUrl.host
        }

    /**
     * Caller should synchronize on document.
     */
    fun setElementById(id: String, element: Element?) {
        synchronized(this) {
            // TODO: Need to take care of document order. The following check is crude and only takes
            //       care of document order for elements in static HTML.
            if (!elementsById.containsKey(id)) {
                this.elementsById.put(id, element)
            }
        }
    }

    fun removeElementById(id: String?) {
        synchronized(this) {
            this.elementsById.remove(id)
        }
    }


    override fun getBaseURI(): String? {
        val buri = this.baseURI
        return if (buri == null) this.documentURI else buri
    }

    fun setBaseURI(value: String?) {
        if (value != null) {
            try {
                URL(value)

                // this is a full url if it parses
                this.baseURI = value
            } catch (mfe: MalformedURLException) {
                try {
                    Urls.createURL(documentURL, value)
                } catch (mfe2: MalformedURLException) {
                    throw IllegalArgumentException(mfe2)
                }
            }
        } else {
            this.baseURI = null
        }
    }

    override fun getDefaultView(): AbstractView? {
        return null
    }

    @Throws(DOMException::class)
    override fun getTextContent(): String? {
        return null
    }

    @Throws(DOMException::class)
    override fun setTextContent(textContent: String) {
        // NOP, per spec
    }

    override fun getTitle(): String? {
        return title
    }


    override fun setTitle(title: String?) {
        this.title = title
    }

    override fun getReferrer(): String? {
        return this.referrer
    }

    fun setReferrer(value: String?) {
        this.referrer = value
    }

    override fun getDomain(): String? {
        return this.domain
    }

    fun setDomain(domain: String) {
        val oldDomain = this.domain
        if ((oldDomain != null)) {
            this.domain = domain
        } else {
            throw SecurityException("Cannot set domain to '" + domain + "' when current domain is '" + oldDomain + "'")
        }
    }

    override fun getBody(): HTMLElement? {
        synchronized(this) {
            return this.body
        }
    }

    override fun setBody(body: HTMLElement?) {
        synchronized(this) {
            this.body = body
        }
    }

    override fun getImages(): HTMLCollection {
        synchronized(this) {
            if (this.images == null) {
                this.images =
                    DescendantHTMLCollection(this, NodeFilter.ImageFilter(), this.treeLock)
            }
            return this.images!!
        }
    }

    override fun getApplets(): HTMLCollection {
        synchronized(this) {
            if (this.applets == null) {
                // TODO: Should include OBJECTs that are applets?
                this.applets = DescendantHTMLCollection(this, AppletFilter(), this.treeLock)
            }
            return this.applets!!
        }
    }

    override fun getLinks(): HTMLCollection {
        synchronized(this) {
            if (this.links == null) {
                this.links = DescendantHTMLCollection(this, LinkFilter(), this.treeLock)
            }
            return this.links!!
        }
    }

    override fun getForms(): HTMLCollection {
        synchronized(this) {
            if (this.forms == null) {
                this.forms = DescendantHTMLCollection(this, FormFilter(), this.treeLock)
            }
            return this.forms!!
        }
    }

    override fun getAnchors(): HTMLCollection {
        synchronized(this) {
            if (this.anchors == null) {
                this.anchors = DescendantHTMLCollection(this, AnchorFilter(), this.treeLock)
            }
            return this.anchors!!
        }
    }

    override fun getCookie(): String? {
        // Justification: A caller (e.g. Google Analytics script)
        // might want to get cookies from the parent document.
        // If the caller has access to the document, it appears
        // they should be able to get cookies on that document.
        // Note that this Document instance cannot be created
        // with an arbitrary URL.

        // TODO: Security: Review rationale.

        return context.getCookie(documentURL)
    }

    @Throws(DOMException::class)
    override fun setCookie(cookie: String?) {
        // Justification: A caller (e.g. Google Analytics script)
        // might want to set cookies on the parent document.
        // If the caller has access to the document, it appears
        // they should be able to set cookies on that document.
        // Note that this Document instance cannot be created
        // with an arbitrary URL.

        context.setCookie(documentURL, cookie)

    }

    override fun open() {
        synchronized(this.treeLock) {
            if (this.reader != null) {
                if (this.reader is LocalWritableLineReader) {
                    try {
                        this.reader!!.close()
                    } catch (ioe: IOException) {
                        // ignore
                    }
                    this.reader = null
                } else {
                    // Already open, return.
                    // Do not close http/file documents in progress.
                    return
                }
            }
            this.removeAllChildrenImpl()
            this.reader = LocalWritableLineReader(EmptyReader())
        }
    }

    /**
     * Loads the document from the reader provided when the current instance of
     * `HTMLDocumentImpl` was constructed. It then closes the reader.
     *
     * @throws IOException
     * @throws SAXException
     * @throws UnsupportedEncodingException
     */
    @JvmOverloads
    @Throws(IOException::class, SAXException::class)
    fun load(closeReader: Boolean = true) {
        val reader: WritableLineReader?
        synchronized(this.treeLock) {
            this.removeAllChildrenImpl()
            this.title = null
            this.setBaseURI(null)

            this.styleSheetManager.invalidateStyles()
            reader = this.reader
        }
        if (reader != null) {
            try {
                val errorHandler: ErrorHandler = LocalErrorHandler()
                val systemId = this.documentURI
                val publicId = systemId
                val parser = HtmlParser(
                    this.context, this, errorHandler, publicId, systemId,
                    this.isXML, true
                )
                parser.parse(reader)
            } finally {
                if (closeReader) {
                    try {
                        reader.close()
                    } catch (err: Exception) {
                        logger.log(
                            Level.WARNING,
                            "load(): Unable to close stream",
                            err
                        )
                    }
                    synchronized(this.treeLock) {
                        this.reader = null
                    }
                }
            }
        }
    }


    val isXML: Boolean
        get() = isDocTypeXHTML || "application/xhtml+xml" == contentType

    override fun close() {
        synchronized(this.treeLock) {
            if (this.reader is LocalWritableLineReader) {
                try {
                    this.reader!!.close()
                } catch (ioe: IOException) {
                    // ignore
                }
                this.reader = null
            } else {
                // do nothing - could be parsing document off the web.
            }
        }
    }

    override fun write(text: String?) {
        synchronized(this.treeLock) {
            if (this.reader != null) {
                try {
                    // This can end up in openBufferChanged
                    this.reader!!.write(text)
                } catch (ioe: IOException) {
                    // ignore
                }
            }
        }
    }

    override fun writeln(text: String?) {
        synchronized(this.treeLock) {
            if (this.reader != null) {
                try {
                    // This can end up in openBufferChanged
                    this.reader!!.write(text + "\r\n")
                } catch (ioe: IOException) {
                    // ignore
                }
            }
        }
    }

    private fun openBufferChanged(text: String) {
        // Assumed to execute in a lock
        // Assumed that text is not broken up HTML.
        val errorHandler: ErrorHandler = LocalErrorHandler()
        val systemId = this.documentURI
        val publicId = systemId
        val parser = HtmlParser(
            this.context,
            this,
            errorHandler,
            publicId,
            systemId,
            false,  /* TODO */
            true
        )
        val strReader = StringReader(text)
        try {
            // This sets up another Javascript scope Window. Does it matter?
            parser.parse(strReader)
        } catch (err: Exception) {
            this.warn(
                "Unable to parse written HTML text. BaseURI=[" + this.getBaseURI() + "].",
                err
            )
        }
    }

    /**
     * Gets the collection of elements whose `name` attribute is
     * `elementName`.
     */
    override fun getElementsByName(elementName: String): NodeList? {
        return this.getNodeList(ElementNameFilter(elementName))
    }

    override fun getDoctype(): DocumentType? {
        return this.doctype
    }

    fun setDoctype(doctype: DocumentType?) {
        this.doctype = doctype
        isDocTypeXHTML = (doctype != null) && (doctype.name == "html")
                && (doctype.publicId == XHTML_STRICT_PUBLIC_ID) && (doctype.systemId == XHTML_STRICT_SYS_ID)
    }

    override fun getDocumentElement(): Element? {
        synchronized(this.treeLock) {


            this.nodes().forEach { nodeModel ->
                val node: Any? = nodeModel
                if (node is Element) {
                    return node
                }
            }

            return null
        }
    }

    @Throws(DOMException::class)
    override fun createElement(tagName: String): Element {
        return this.factory.createElement(this, tagName)
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.Document#createDocumentFragment()
     */
    override fun createDocumentFragment(): DocumentFragment {
        // TODO: According to documentation, when a document
        // fragment is added to a node, its children are added,
        // not itself.
        val node = DocumentFragmentModel()
        node.setOwnerDocument(this)
        return node
    }

    override fun createTextNode(data: String): Text {
        val node = TextImpl(data)
        node.setOwnerDocument(this)
        return node
    }

    override fun createComment(data: String): Comment {
        val node = CommentImpl(data)
        node.setOwnerDocument(this)
        return node
    }

    @Throws(DOMException::class)
    override fun createCDATASection(data: String): CDATASection {
        val node = CDataSectionImpl(data)
        node.setOwnerDocument(this)
        return node
    }

    @Throws(DOMException::class)
    override fun createProcessingInstruction(
        target: String,
        data: String?
    ): ProcessingInstruction {
        val node = HTMLProcessingInstruction(target, data)
        node.setOwnerDocument(this)
        return node
    }

    @Throws(DOMException::class)
    override fun createAttribute(name: String): Attr {
        return AttrImpl(name)
    }

    @Throws(DOMException::class)
    override fun createEntityReference(name: String?): EntityReference? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "HTML document")
    }

    /**
     * Gets all elements that match the given tag name.
     *
     * @param classNames The element tag name or an asterisk character (*) to match all
     * elements.
     */
    override fun getElementsByTagName(classNames: String): NodeList {
        if ("*" == classNames) {
            return this.getNodeList(NodeFilter.ElementFilter())
        } else {
            return this.getNodeList(TagNameFilter(classNames))
        }
    }

    @Throws(DOMException::class)
    override fun importNode(importedNode: Node?, deep: Boolean): Node? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented")
    }

    @Throws(DOMException::class)
    override fun createElementNS(namespaceURI: String?, qualifiedName: String): Element? {
        if (namespaceURI == null || (namespaceURI.trim { it <= ' ' }.length == 0) || "http://www.w3.org/1999/xhtml".equals(
                namespaceURI,
                ignoreCase = true
            )
        ) {
            return createElement(qualifiedName)
        } else if ("http://www.w3.org/2000/svg".equals(namespaceURI, ignoreCase = true)) {
            // TODO: This is a plug
            return createElement(qualifiedName)
        }
        println("unhandled request to create element in NS: " + namespaceURI + " with tag: " + qualifiedName)
        return null
        // TODO
        // throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented: createElementNS");
    }

    @Throws(DOMException::class)
    override fun createAttributeNS(namespaceURI: String?, qualifiedName: String?): Attr? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented: createAttributeNS")
    }

    override fun getElementsByTagNameNS(namespaceURI: String?, localName: String?): NodeList? {
        throw DOMException(
            DOMException.NOT_SUPPORTED_ERR,
            "Not implemented: getElementsByTagNameNS"
        )
    }

    override fun getElementById(elementId: String?): Element? {
        if ((elementId != null) && (elementId.length > 0)) {
            synchronized(this) {
                return this.elementsById.get(elementId)
            }
        } else {
            return null
        }
    }

    fun namedItem(name: String?): Element? {
        val element: Element?
        synchronized(this) {
            element = this.elementsByName.get(name)
        }
        return element
    }

    fun setNamedItem(name: String?, element: Element?) {
        synchronized(this) {
            this.elementsByName.put(name, element)
        }
    }

    fun removeNamedItem(name: String?) {
        synchronized(this) {
            this.elementsByName.remove(name)
        }
    }

    override fun getInputEncoding(): String? {
        return this.inputEncoding
    }

    override fun getXmlEncoding(): String? {
        return this.xmlEncoding
    }

    override fun getXmlStandalone(): Boolean {
        return this.xmlStandalone
    }

    @Throws(DOMException::class)
    override fun setXmlStandalone(xmlStandalone: Boolean) {
        this.xmlStandalone = xmlStandalone
    }

    override fun getXmlVersion(): String? {
        return this.xmlVersion
    }

    @Throws(DOMException::class)
    override fun setXmlVersion(xmlVersion: String?) {
        this.xmlVersion = xmlVersion
    }

    override fun getStrictErrorChecking(): Boolean {
        return this.strictErrorChecking
    }

    override fun setStrictErrorChecking(strictErrorChecking: Boolean) {
        this.strictErrorChecking = strictErrorChecking
    }

    override fun getDocumentURI(): String? {
        return this.documentURI
    }

    override fun setDocumentURI(documentURI: String) {
        // TODO: Security considerations? Chaging documentURL?
        this.documentURI = documentURI
    }

    @Throws(DOMException::class)
    override fun adoptNode(source: Node?): Node {
        if (source is NodeImpl) {
            source.setOwnerDocument(this, true)
            return source
        } else {
            throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Invalid Node implementation")
        }
    }

    override fun getDomConfig(): DOMConfiguration {
        synchronized(this) {
            if (this.domConfig == null) {
                this.domConfig = DOMConfigurationImpl()
            }
            return this.domConfig!!
        }
    }

    override fun normalizeDocument() {
        // TODO: Normalization options from domConfig
        synchronized(this.treeLock) {
            this.visitImpl(object : NodeVisitor {
                override fun visit(node: Node) {
                    node.normalize()
                }
            })
        }
    }

    @Throws(DOMException::class)
    override fun renameNode(n: Node?, namespaceURI: String?, qualifiedName: String?): Node? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "No renaming")
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.Document#getImplementation()
     */
    override fun getImplementation(): DOMImplementation {
        synchronized(this) {
            if (this.domImplementation == null) {
                this.domImplementation = DOMImplementationImpl(this.context)
            }
            return this.domImplementation!!
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#getLocalName()
     */
    override fun getLocalName(): String? {
        // Always null for document
        return null
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#getNodeName()
     */
    override fun getNodeName(): String {
        return "#document"
    }


    override fun getNodeType(): Short {
        return DOCUMENT_NODE
    }


    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        // Always null for document
        return null
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#setNodeValue(java.lang.String)
     */
    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
        throw DOMException(
            DOMException.INVALID_MODIFICATION_ERR,
            "Cannot set node value of document"
        )
    }


    fun userAgentContext(): UserAgentContext {
        return this.context
    }

    @Throws(MalformedURLException::class)
    override fun getFullURL(uri: String): URL {
        try {
            val baseURI = this.getBaseURI()
            val documentURL = if (baseURI == null) null else URL(baseURI)
            return Urls.createURL(documentURL, uri)
        } catch (mfu: MalformedURLException) {
            return URL(uri)
        }
    }


    override fun getURL(): String? {
        return this.documentURI
    }

    fun allInvalidated(forgetRenderStates: Boolean) {
        if (forgetRenderStates) {
            synchronized(this.treeLock) {
                // Need to invalidate all children up to
                // this point.
                this.forgetRenderState()
                // TODO: this might be ineffcient.

                this.nodes().forEach { nodeModel ->
                    val node: Any? = nodeModel
                    if (node is HTMLElementModel) {
                        node.forgetStyle(true)
                    }
                }

            }
        }
        this.allInvalidated()
    }

    override fun getStyleSheets(): StyleSheetList {
        return styleSheetManager.constructStyleSheetList()
    }

    /**
     * Adds a document notification listener, which is informed about changes to
     * the document.
     *
     * @param listener An instance of [DocumentNotificationListener].
     */
    fun addDocumentNotificationListener(listener: DocumentNotificationListener?) {
        val listenersList = this.documentNotificationListeners
        synchronized(listenersList) {
            listenersList.add(listener!!)
        }
    }

    fun removeDocumentNotificationListener(listener: DocumentNotificationListener?) {
        val listenersList = this.documentNotificationListeners
        synchronized(listenersList) {
            listenersList.remove(listener)
        }
    }

    fun sizeInvalidated(node: NodeImpl) {
        val listenersList = this.documentNotificationListeners
        val size: Int
        synchronized(listenersList) {
            size = listenersList.size
        }
        // Traverse list outside synchronized block.
        // (Shouldn't call listener methods in synchronized block.
        // Deadlock is possible). But assume list could have
        // been changed.
        for (i in 0..<size) {
            try {
                val dnl = listenersList.get(i)
                dnl.sizeInvalidated(node)
            } catch (iob: IndexOutOfBoundsException) {
                // ignore
            }
        }
    }

    /**
     * Called if something such as a color or decoration has changed. This would
     * be something which does not affect the rendered size, and can be
     * revalidated with a simple repaint.
     *
     * @param node
     */
    fun lookInvalidated(node: NodeImpl) {
        val listenersList = this.documentNotificationListeners
        val size: Int
        synchronized(listenersList) {
            size = listenersList.size
        }
        // Traverse list outside synchronized block.
        // (Shouldn't call listener methods in synchronized block.
        // Deadlock is possible). But assume list could have
        // been changed.
        for (i in 0..<size) {
            try {
                val dnl = listenersList.get(i)
                dnl.lookInvalidated(node)
            } catch (iob: IndexOutOfBoundsException) {
                // ignore
            }
        }
    }

    /**
     * Changed if the position of the node in a parent has changed.
     *
     * @param node
     */
    fun positionInParentInvalidated(node: NodeImpl) {
        val listenersList = this.documentNotificationListeners
        val size: Int
        synchronized(listenersList) {
            size = listenersList.size
        }
        // Traverse list outside synchronized block.
        // (Shouldn't call listener methods in synchronized block.
        // Deadlock is possible). But assume list could have
        // been changed.
        for (i in 0..<size) {
            try {
                val dnl = listenersList.get(i)
                dnl.positionInvalidated(node)
            } catch (iob: IndexOutOfBoundsException) {
                // ignore
            }
        }
    }

    /**
     * This is called when the node has changed, but it is unclear if it's a size
     * change or a look change. An attribute change should trigger this.
     *
     * @param node
     */
    fun invalidated(node: NodeImpl) {
        val listenersList = this.documentNotificationListeners
        val size: Int
        synchronized(listenersList) {
            size = listenersList.size
        }
        // Traverse list outside synchronized block.
        // (Shouldn't call listener methods in synchronized block.
        // Deadlock is possible). But assume list could have
        // been changed.
        for (i in 0..<size) {
            try {
                val dnl = listenersList.get(i)
                dnl.invalidated(node)
            } catch (iob: IndexOutOfBoundsException) {
                // ignore
            }
        }
    }

    /**
     * This is called when children of the node might have changed.
     *
     * @param node
     */
    fun structureInvalidated(node: NodeImpl) {
        val listenersList = this.documentNotificationListeners
        val size: Int
        synchronized(listenersList) {
            size = listenersList.size
        }
        // Traverse list outside synchronized block.
        // (Shouldn't call listener methods in synchronized block.
        // Deadlock is possible). But assume list could have
        // been changed.
        for (i in 0..<size) {
            try {
                val dnl = listenersList.get(i)
                dnl.structureInvalidated(node)
            } catch (iob: IndexOutOfBoundsException) {
                // ignore
            }
        }
    }

    fun nodeLoaded(node: NodeImpl) {
        val listenersList = this.documentNotificationListeners
        val size: Int
        synchronized(listenersList) {
            size = listenersList.size
        }
        // Traverse list outside synchronized block.
        // (Shouldn't call listener methods in synchronized block.
        // Deadlock is possible). But assume list could have
        // been changed.
        for (i in 0..<size) {
            try {
                val dnl = listenersList.get(i)
                dnl.nodeLoaded(node)
            } catch (iob: IndexOutOfBoundsException) {
                // ignore
            }
        }
    }

    fun externalScriptLoading(node: NodeImpl) {
        val listenersList = this.documentNotificationListeners
        val size: Int
        synchronized(listenersList) {
            size = listenersList.size
        }
        // Traverse list outside synchronized block.
        // (Shouldn't call listener methods in synchronized block.
        // Deadlock is possible). But assume list could have
        // been changed.
        for (i in 0..<size) {
            try {
                val dnl = listenersList.get(i)
                dnl.externalScriptLoading(node)
            } catch (iob: IndexOutOfBoundsException) {
                // ignore
            }
        }
    }

    /**
     * Informs listeners that the whole document has been invalidated.
     */
    fun allInvalidated() {
        val listenersList = this.documentNotificationListeners
        val size: Int
        synchronized(listenersList) {
            size = listenersList.size
        }
        // Traverse list outside synchronized block.
        // (Shouldn't call listener methods in synchronized block.
        // Deadlock is possible). But assume list could have
        // been changed.
        for (i in 0..<size) {
            try {
                val dnl = listenersList.get(i)
                dnl.allInvalidated()
            } catch (iob: IndexOutOfBoundsException) {
                // ignore
            }
        }
    }

    override fun createRenderState(prevRenderState: RenderState?): RenderState {
        return StyleSheetRenderState(this)
    }


    override fun setUserData(key: String, data: Any?, handler: UserDataHandler?): Any? {
        // if (org.cobraparser.html.parser.HtmlParser.MODIFYING_KEY.equals(key) && data == Boolean.FALSE) {
        // dispatchLoadEvent();
        // }
        return super.setUserData(key, data, handler)
    }


    override fun createSimilarNode(): Node {
        return HTMLDocumentImpl(
            this.context,
            this.reader,
            this.documentURI,
            this.contentType
        )
    }


    private fun updateStyleRules() {
        synchronized(treeLock) {
            if (classifiedRules == null) {
                val jSheets: MutableList<StyleSheet?> = ArrayList()
                jSheets.add(if (this.isXML) recommendedStyleXML else recommendedStyle)
                jSheets.add(if (this.isXML) userAgentStyleXML else userAgentStyle)
                jSheets.addAll(styleSheetManager.getEnabledJStyleSheets()!!)
                classifiedRules = AnalyzerUtil.getClassifiedRules(jSheets, MediaSpec("screen"))
            }
        }
    }

    val matcher: ElementMatcher
        get() = if (this.isXML) xhtmlMatcher else stdMatcher

    /**
     * Visits all elements and computes their styles. This is faster than
     * computing them separately when needed. Note: If styles were to be stored as
     * soft / weak references, this method will lose its value.
     */

    fun primeNodeData() {
        visit({ node: Node ->
            if (node is HTMLElementModel) {
                node.cssProperties()
            }
        })
    }

    fun getClassifiedRules(): Analyzer.Holder? {
        synchronized(treeLock) {
            if (classifiedRules == null) {
                updateStyleRules()
            }
            return classifiedRules
        }
    }


    fun createRange(): Range {
        return RangeImpl(this)
    }

    fun hasFocus(): Boolean {
        // TODO: Plug
        return true
    }

    /**
     * Tag class that also notifies document when text is written to an open
     * buffer.
     *
     * @author J. H. S.
     */
    private inner class LocalWritableLineReader : WritableLineReader {
        /**
         * @param reader
         */
        constructor(reader: LineNumberReader) : super(reader)

        /**
         * @param reader
         */
        constructor(reader: Reader) : super(reader)

        @Throws(IOException::class)
        override fun write(text: String?) {
            super.write(text)
            if ("" == text) {
                openBufferChanged(text)
            }
        }
    }

    inner class StyleSheetManager {
        @Volatile
        private var styleSheets: MutableList<StyleSheetWrapper>? = null

        @Volatile
        private var enabledJStyleSheets: MutableList<StyleSheet?>? = null
        val bridge: StyleSheetBridge = object : StyleSheetBridge {
            override fun notifyStyleSheetChanged(styleSheet: CSSStyleSheet) {
                val ownerNode = styleSheet.ownerNode
                if (ownerNode != null) {
                    val disabled = styleSheet.disabled
                    if (ownerNode is HTMLStyleElementModel) {
                        if (ownerNode.disabled != disabled) {
                            ownerNode.setDisabledImpl(disabled)
                        }
                    } else if (ownerNode is HTMLLinkElementModel) {
                        if (ownerNode.disabled != disabled) {
                            ownerNode.setDisabledInternal(disabled)
                        }
                    }
                }
                invalidateStyles()
                allInvalidated()
            }

            override val docStyleSheets: MutableList<StyleSheetWrapper>?
                get() = TODO("Not yet implemented")


        }

        private val docStyleSheetList: MutableList<StyleSheetWrapper>
            get() {
                synchronized(this) {
                    if (styleSheets == null) {
                        styleSheets = ArrayList<StyleSheetWrapper>()
                        val docStyles: MutableList<StyleSheetWrapper> =
                            ArrayList<StyleSheetWrapper>()
                        synchronized(treeLock) {
                            scanElementStyleSheets(docStyles, this@HTMLDocumentImpl)
                        }
                        styleSheets!!.addAll(docStyles)
                        // System.out.println("Found stylesheets: " + this.styleSheets.size());
                    }
                    return this.styleSheets!!
                }
            }

        private fun scanElementStyleSheets(styles: MutableList<StyleSheetWrapper>, node: Node) {
            if (node is LinkStyle) {
                val sheet = node.sheet as StyleSheetWrapper?
                if (sheet != null) {
                    styles.add(sheet)
                }
            }

            if (node.hasChildNodes()) {
                val nodeList = node.childNodes
                for (i in 0..<nodeList.length) {
                    scanElementStyleSheets(styles, nodeList.item(i))
                }
            }
        }

        // TODO enabled style sheets can be cached
        fun getEnabledJStyleSheets(): MutableList<StyleSheet?>? {
            synchronized(this) {
                if (enabledJStyleSheets != null) {
                    return enabledJStyleSheets
                }
                val documentStyles =
                    this.docStyleSheetList
                val jStyleSheets: MutableList<StyleSheet?> = ArrayList<StyleSheet?>()
                for (style in documentStyles) {
                    if ((!style.disabled) && (style.styleSheet != null)) {
                        jStyleSheets.add(style.styleSheet)
                    }
                }
                enabledJStyleSheets = jStyleSheets
                return jStyleSheets
            }
        }

        fun invalidateStyles() {
            synchronized(treeLock) {
                this.styleSheets = null
                this.docStyleSheetList
            }
            synchronized(this) {
                this.enabledJStyleSheets = null
            }
            synchronized(treeLock) {
                this@HTMLDocumentImpl.classifiedRules = null
            }
            // System.out.println("Stylesheets set to null");
            allInvalidated(true)
        }

        fun constructStyleSheetList(): StyleSheetList {
            return getStyleSheets(bridge)
        }
    }

    companion object {
        val xhtmlMatcher: ElementMatcher = ElementMatcherSafeCS()
        val stdMatcher: ElementMatcher = ElementMatcherSafeStd()
        private const val XHTML_STRICT_PUBLIC_ID = "-//W3C//DTD XHTML 1.0 Strict//EN"
        private const val XHTML_STRICT_SYS_ID = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
        private val recommendedStyle: StyleSheet =
            parseStyle(CSSNorm.stdStyleSheet(), StyleSheet.Origin.AGENT, false)
        private val userAgentStyle: StyleSheet =
            parseStyle(CSSNorm.userStyleSheet(), StyleSheet.Origin.AGENT, false)
        private val recommendedStyleXML: StyleSheet =
            parseStyle(CSSNorm.stdStyleSheet(), StyleSheet.Origin.AGENT, true)
        private val userAgentStyleXML: StyleSheet =
            parseStyle(CSSNorm.userStyleSheet(), StyleSheet.Origin.AGENT, true)

        private fun parseStyle(
            cssdata: String?,
            origin: StyleSheet.Origin?,
            isXML: Boolean
        ): StyleSheet {
            try {
                val newsheet = CSSParserFactory.getInstance()
                    .parse(cssdata, null, null, CSSParserFactory.SourceType.EMBEDDED, null)
                newsheet.origin = origin
                return newsheet
            } catch (e: IOException) {
                throw RuntimeException(e)
            } catch (e: CSSException) {
                throw RuntimeException(e)
            }
        }
    }
}
