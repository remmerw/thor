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
import io.github.remmerw.thor.dom.NodeFilter.AnchorFilter
import io.github.remmerw.thor.dom.NodeFilter.AppletFilter
import io.github.remmerw.thor.dom.NodeFilter.ElementNameFilter
import io.github.remmerw.thor.dom.NodeFilter.FormFilter
import io.github.remmerw.thor.dom.NodeFilter.LinkFilter
import io.github.remmerw.thor.dom.NodeFilter.TagNameFilter
import io.github.remmerw.thor.parser.HtmlParser
import io.github.remmerw.thor.parser.WritableLineReader
import io.github.remmerw.thor.style.CSSNorm
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
import java.io.IOException
import java.io.Reader
import java.net.MalformedURLException
import java.net.URL
import kotlin.concurrent.Volatile
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch


class DocumentImpl(
    private var reader: WritableLineReader? = null,
    private var documentURI: String? = null,
    private val contentType: String? = null
) : NodeImpl(null, 0), DocumentModel {

    @OptIn(ExperimentalAtomicApi::class)
    private val uids = AtomicLong(0L)

    private val factory: ElementFactory
    private var documentURL: URL? = null

    @Volatile
    private var baseURI: String? = null
    private var title: String? = null
    private var referrer: String? = null
    private var domain: String? = null
    private var images: Collection? = null
    private var applets: Collection? = null
    private var links: Collection? = null
    private var forms: Collection? = null
    private var anchors: Collection? = null
    private var doctype: DocumentType? = null
    private var isDocTypeXHTML = false
    private var inputEncoding: String? = null
    private var xmlEncoding: String? = null
    private var xmlStandalone = false
    private var xmlVersion: String? = null
    private var strictErrorChecking = true
    private var domConfig: DOMConfiguration? = null
    private var domImplementation: DOMImplementation? = null
    private var body: Element? = null
    private var classifiedRules: Analyzer.Holder? = null


    init {
        this.factory = ElementFactory.Companion.instance
        try {
            val docURL = URL(documentURI)

            this.documentURL = docURL
            this.domain = docURL.host
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }

        this.setOwnerDocument(this)

    }

    @OptIn(ExperimentalAtomicApi::class)
    fun nextUid(): Long {
        return uids.incrementAndFetch()
    }

    override fun getDocumentURL(): URL? {
        return documentURL
    }

    val documentHost: String?
        get() {
            val docUrl = this.getDocumentURL()
            return if (docUrl == null) null else docUrl.host
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

    @Throws(DOMException::class)
    override fun getTextContent(): String? {
        return null
    }

    @Throws(DOMException::class)
    override fun setTextContent(textContent: String) {
        // NOP, per spec
    }

    fun getTitle(): String? {
        return title
    }


    fun setTitle(title: String?) {
        this.title = title
    }

    fun getReferrer(): String? {
        return this.referrer
    }

    fun setReferrer(value: String?) {
        this.referrer = value
    }

    fun getDomain(): String? {
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

    fun getBody(): Element? {
        synchronized(this) {
            return this.body
        }
    }

    fun setBody(body: Element?) {
        synchronized(this) {
            this.body = body
        }
    }

    fun getImages(): Collection {
        synchronized(this) {
            if (this.images == null) {
                this.images =
                    DescendantHTMLCollection(this, NodeFilter.ImageFilter())
            }
            return this.images!!
        }
    }

    fun getApplets(): Collection {
        synchronized(this) {
            if (this.applets == null) {
                // TODO: Should include OBJECTs that are applets?
                this.applets = DescendantHTMLCollection(this, AppletFilter())
            }
            return this.applets!!
        }
    }

    fun getLinks(): Collection {
        synchronized(this) {
            if (this.links == null) {
                this.links = DescendantHTMLCollection(this, LinkFilter())
            }
            return this.links!!
        }
    }

    fun getForms(): Collection {
        synchronized(this) {
            if (this.forms == null) {
                this.forms = DescendantHTMLCollection(this, FormFilter())
            }
            return this.forms!!
        }
    }

    fun getAnchors(): Collection {
        synchronized(this) {
            if (this.anchors == null) {
                this.anchors = DescendantHTMLCollection(this, AnchorFilter())
            }
            return this.anchors!!
        }
    }

    fun getCookie(): String? {
        // Justification: A caller (e.g. Google Analytics script)
        // might want to get cookies from the parent document.
        // If the caller has access to the document, it appears
        // they should be able to get cookies on that document.
        // Note that this Document instance cannot be created
        // with an arbitrary URL.

        // cookies not supported

        return null
    }

    @Throws(DOMException::class)
    fun setCookie(cookie: String?) {
        // Justification: A caller (e.g. Google Analytics script)
        // might want to set cookies on the parent document.
        // If the caller has access to the document, it appears
        // they should be able to set cookies on that document.
        // Note that this Document instance cannot be created
        // with an arbitrary URL.

        // cookies not supported

    }


    fun load() {

        this.title = null
        this.setBaseURI(null)


        val reader = this.reader


        if (reader != null) {
            try {
                val parser = HtmlParser(
                    this, this.isXML, true
                )
                parser.parse(reader)

            } finally {
                try {
                    reader.close()
                } catch (_: Throwable) {
                }
            }
        }
    }


    val isXML: Boolean
        get() = isDocTypeXHTML || "application/xhtml+xml" == contentType


    /**
     * Gets the collection of elements whose `name` attribute is
     * `elementName`.
     */
    fun getElementsByName(elementName: String): NodeList? {
        return this.getNodeList(ElementNameFilter(elementName))
    }

    override fun getDoctype(): DocumentType? {
        return this.doctype
    }

    fun setDoctype(doctype: DocumentType) {
        this.doctype = doctype
        isDocTypeXHTML = (doctype.name == "html")
                && (doctype.publicId == XHTML_STRICT_PUBLIC_ID)
                && (doctype.systemId == XHTML_STRICT_SYS_ID)
    }

    override fun getDocumentElement(): Element? {
        this.nodes().forEach { nodeModel ->
            val node: Any? = nodeModel
            if (node is Element) {
                return node
            }
        }
        return null

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
        TODO()
    }

    override fun createTextNode(data: String): Text {
        val node = TextImpl(this, nextUid(), data)
        return node
    }

    override fun createComment(data: String): Comment {
        val node = CommentImpl(this, nextUid(), data)
        return node
    }

    @Throws(DOMException::class)
    override fun createCDATASection(data: String): CDATASection {
        val node = CDataSectionImpl(this, nextUid(), data)
        return node
    }

    @Throws(DOMException::class)
    override fun createProcessingInstruction(
        target: String,
        data: String?
    ): ProcessingInstruction {
        val node = ProcessingInstructionImpl(
            this, nextUid(),
            target, data
        )
        return node
    }

    @Throws(DOMException::class)
    override fun createAttribute(name: String): Attr {
        return AttrImpl(this, nextUid(), name)
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
        TODO()
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
        this.visitImpl(object : NodeVisitor {
            override fun visit(node: Node) {
                node.normalize()
            }
        })
    }

    @Throws(DOMException::class)
    override fun renameNode(n: Node?, namespaceURI: String?, qualifiedName: String?): Node? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "No renaming")
    }


    override fun getImplementation(): DOMImplementation {
        synchronized(this) {
            if (this.domImplementation == null) {
                this.domImplementation = DOMImplementationImpl(this)
            }
            return this.domImplementation!!
        }
    }


    override fun getLocalName(): String? {
        // Always null for document
        return null
    }

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


    fun getURL(): String? {
        return this.documentURI
    }


    private fun updateStyleRules() {
        if (classifiedRules == null) {
            val jSheets: MutableList<StyleSheet?> = ArrayList()
            jSheets.add(if (this.isXML) recommendedStyleXML else recommendedStyle)
            jSheets.add(if (this.isXML) userAgentStyleXML else userAgentStyle)
            classifiedRules = AnalyzerUtil.getClassifiedRules(jSheets, MediaSpec("screen"))
        }
    }

    val matcher: ElementMatcher
        get() = if (this.isXML) xhtmlMatcher else stdMatcher


    fun getClassifiedRules(): Analyzer.Holder? {

        if (classifiedRules == null) {
            updateStyleRules()
        }
        return classifiedRules

    }


    private inner class LocalWritableLineReader : WritableLineReader {

        constructor(reader: Reader) : super(reader)

        @Throws(IOException::class)
        override fun write(text: String?) {
            super.write(text)
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
