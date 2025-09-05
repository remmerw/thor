package io.github.remmerw.thor.dom

import io.github.remmerw.thor.dom.NodeFilter.AnchorFilter
import io.github.remmerw.thor.dom.NodeFilter.AppletFilter
import io.github.remmerw.thor.dom.NodeFilter.FormFilter
import io.github.remmerw.thor.dom.NodeFilter.LinkFilter
import io.github.remmerw.thor.dom.NodeFilter.TagNameFilter
import io.ktor.http.Url
import org.w3c.dom.Attr
import org.w3c.dom.CDATASection
import org.w3c.dom.Comment
import org.w3c.dom.DOMConfiguration
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.DocumentFragment
import org.w3c.dom.DocumentType
import org.w3c.dom.Element
import org.w3c.dom.EntityReference
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.ProcessingInstruction
import org.w3c.dom.Text
import java.io.LineNumberReader
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch


class DocumentImpl(
    private var reader: LineNumberReader? = null,
    private var documentURI: Url,
    private val contentType: String? = null
) : NodeImpl(null, 0, "#document", DOCUMENT_NODE), Document {

    @OptIn(ExperimentalAtomicApi::class)
    private val uids = AtomicLong(0L)
    private val factory: ElementFactory = ElementFactory.Companion.instance
    private var documentUrl: Url? = null
    private val allNodes: MutableMap<Long, NodeImpl> = mutableMapOf()
    private var doctype: DocumentType? = null
    private var isDocTypeXHTML = false
    private var inputEncoding: String? = null
    private var xmlEncoding: String? = null
    private var xmlStandalone = false
    private var xmlVersion: String? = null
    private var strictErrorChecking = true
    private var body: Element? = null


    init {
        this.setOwnerDocument(this)
    }

    internal fun addNode(node: NodeImpl) {
        allNodes.put(node.uid(), node)

        if (node is Element) {
            if (node.nodeName == "BODY") {
                setBody(node)
            }
        }
    }

    internal fun removeNode(node: NodeImpl) {
        allNodes.remove(node.uid())
    }

    @OptIn(ExperimentalAtomicApi::class)
    fun nextUid(): Long {
        return uids.incrementAndFetch()
    }

    fun getDocumentUrl(): Url? {
        return documentUrl
    }

    fun getDocumentHost(): String? {
        val docUrl = this.getDocumentUrl()
        return docUrl?.host
    }


    override fun getBaseURI(): String? {
        return this.documentURI.toString()
    }

    @Throws(DOMException::class)
    override fun getTextContent(): String? {
        return null
    }

    @Throws(DOMException::class)
    override fun setTextContent(textContent: String) {
        // NOP, per spec
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
            return DescendantHTMLCollection(this, NodeFilter.ImageFilter())
        }
    }

    fun getApplets(): Collection {
        synchronized(this) {
            return DescendantHTMLCollection(this, AppletFilter())

        }
    }

    fun getLinks(): Collection {
        synchronized(this) {
            return DescendantHTMLCollection(this, LinkFilter())
        }
    }

    fun getForms(): Collection {
        synchronized(this) {
            return DescendantHTMLCollection(this, FormFilter())
        }
    }

    fun getElementsByName(name: String): Collection {
        synchronized(this) {
            return DescendantHTMLCollection(this, NodeFilter.ElementNameFilter(name))
        }
    }

    fun getAnchors(): Collection {
        synchronized(this) {
            return DescendantHTMLCollection(this, AnchorFilter())
        }
    }


    fun load() {


        val reader = this.reader

        if (reader != null) {
            try {
                val parser = HtmlParser(
                    this, this.isXML(), true
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


    fun isXML(): Boolean {
        return isDocTypeXHTML || "application/xhtml+xml" == contentType
    }


    override fun getDoctype(): DocumentType? {
        return this.doctype
    }

    override fun getImplementation(): DOMImplementation? {
        TODO("Not yet implemented")
    }

    fun setDoctype(doctype: DocumentType) {
        this.doctype = doctype
        isDocTypeXHTML = (doctype.name == "html")
                && (doctype.publicId == XHTML_STRICT_PUBLIC_ID)
                && (doctype.systemId == XHTML_STRICT_SYS_ID)
    }

    override fun getDocumentElement(): Element? {
        this.nodes().forEach { node ->
            val node: Any? = node
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

    override fun createDocumentFragment(): DocumentFragment {
        TODO()
    }

    override fun createTextNode(data: String): Text {
        val node = TextImpl(this, nextUid(), data)
        this.addNode(node)
        return node
    }

    override fun createComment(data: String): Comment {
        val node = CommentImpl(this, nextUid(), data)
        return node
    }

    @Throws(DOMException::class)
    override fun createCDATASection(data: String): CDATASection {
        return CDataSectionImpl(this, nextUid(), data)
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


    override fun createAttribute(name: String): Attr {
        TODO()
    }


    override fun createEntityReference(name: String?): EntityReference? {
        TODO()
    }


    override fun getElementsByTagName(classNames: String): NodeList {
        if ("*" == classNames) {
            return this.getNodeList(NodeFilter.ElementFilter())
        } else {
            return this.getNodeList(TagNameFilter(classNames))
        }
    }

    override fun importNode(importedNode: Node?, deep: Boolean): Node? {
        TODO()
    }


    override fun createElementNS(namespaceURI: String?, qualifiedName: String): Element? {
        TODO()
    }

    override fun createAttributeNS(namespaceURI: String?, qualifiedName: String?): Attr? {
        TODO()
    }

    override fun getElementsByTagNameNS(namespaceURI: String?, localName: String?): NodeList? {
        TODO()
    }

    override fun getElementById(id: String): Element? {
        synchronized(this) {
            val list = DescendantHTMLCollection(this, NodeFilter.ElementIdFilter(id))
            if (list.getLength() > 0) {
                return list.item(0) as Element?
            }
            return null
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
        return this.documentURI.toString()
    }

    override fun setDocumentURI(documentURI: String) {
        TODO()
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
        TODO()
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
        TODO()
    }

    override fun getLocalName(): String? {
        // Always null for document
        return null
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

    fun childNodes(uid: Long): List<Node> {
        return allNodes[uid]?.nodes() ?: emptyList()
    }

    fun node(uid: Long): Node? {
        return allNodes[uid]
    }

    companion object {
        private const val XHTML_STRICT_PUBLIC_ID = "-//W3C//DTD XHTML 1.0 Strict//EN"
        private const val XHTML_STRICT_SYS_ID = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    }
}
