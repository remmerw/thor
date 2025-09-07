package io.github.remmerw.thor.dom

import io.ktor.http.Url
import kotlinx.coroutines.flow.StateFlow
import java.io.LineNumberReader
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch


class Document(
    private var reader: LineNumberReader? = null,
    private var documentURI: Url,
    private val contentType: String? = null
) : Node(null, 0, "#document", DOCUMENT_NODE) {

    @OptIn(ExperimentalAtomicApi::class)
    private val uids = AtomicLong(0L)
    private val factory: ElementFactory = ElementFactory.Companion.instance
    private var documentUrl: Url? = null
    private val nodes: MutableMap<Long, Node> = mutableMapOf()
    private var doctype: DocumentType? = null
    private var isDocTypeXHTML = false
    private var inputEncoding: String? = null
    private var xmlEncoding: String? = null
    private var xmlStandalone = false
    private var xmlVersion: String? = null
    private var strictErrorChecking = true


    init {
        this.setOwnerDocument(this)
        this.nodes.put(0, this)
    }

    internal fun addNode(node: Node) {
        nodes.put(node.uid(), node)
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


    fun getElementsByName(name: String): Collection {
        synchronized(this) {
            return DescendantHTMLCollection(this, NodeFilter.ElementNameFilter(name))
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


    fun getDoctype(): DocumentType? {
        return this.doctype
    }


    fun setDoctype(doctype: DocumentType) {
        this.doctype = doctype
        isDocTypeXHTML = (doctype.name == "html")
                && (doctype.publicId == XHTML_STRICT_PUBLIC_ID)
                && (doctype.systemId == XHTML_STRICT_SYS_ID)
    }

    fun getDocumentElement(): Element? {
        this.children().forEach { node ->
            val node: Any? = node
            if (node is Element) {
                return node
            }
        }
        return null

    }

    @Throws(DOMException::class)
    fun createElement(tagName: String): Element {
        return this.factory.createElement(this, tagName)
    }


    fun createTextNode(data: String): Text {
        return Text(this, nextUid(), data)
    }

    fun createComment(data: String): Comment {
        val node = Comment(this, nextUid(), data)
        return node
    }

    fun createCDATASection(data: String): CDataSection {
        return CDataSection(this, nextUid(), data)
    }

    @Throws(DOMException::class)
    fun createProcessingInstruction(
        target: String,
        data: String?
    ): ProcessingInstruction {
        return ProcessingInstruction(
            this, nextUid(),
            target, data
        )
    }


    fun getInputEncoding(): String? {
        return this.inputEncoding
    }

    fun getXmlEncoding(): String? {
        return this.xmlEncoding
    }

    fun getXmlStandalone(): Boolean {
        return this.xmlStandalone
    }

    @Throws(DOMException::class)
    fun setXmlStandalone(xmlStandalone: Boolean) {
        this.xmlStandalone = xmlStandalone
    }

    fun getXmlVersion(): String? {
        return this.xmlVersion
    }

    @Throws(DOMException::class)
    fun setXmlVersion(xmlVersion: String?) {
        this.xmlVersion = xmlVersion
    }

    fun getStrictErrorChecking(): Boolean {
        return this.strictErrorChecking
    }

    fun setStrictErrorChecking(strictErrorChecking: Boolean) {
        this.strictErrorChecking = strictErrorChecking
    }

    fun getDocumentURI(): String? {
        return this.documentURI.toString()
    }

    fun setDocumentURI(documentURI: String) {
        TODO()
    }

    @Throws(DOMException::class)
    fun adoptNode(source: Node?): Node {
        if (source is Node) {
            source.setOwnerDocument(this, true)
            return source
        } else {
            throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Invalid Node implementation")
        }
    }


    fun childNodes(uid: Long): List<Node> {
        return nodes[uid]?.children() ?: emptyList()
    }

    fun node(uid: Long): Node? {
        return nodes[uid]
    }

    fun wurst(entity: Entity): StateFlow<String> {
        return (nodes[entity.uid] as Text).wurst
    }

    companion object {
        private const val XHTML_STRICT_PUBLIC_ID = "-//W3C//DTD XHTML 1.0 Strict//EN"
        private const val XHTML_STRICT_SYS_ID = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    }
}
