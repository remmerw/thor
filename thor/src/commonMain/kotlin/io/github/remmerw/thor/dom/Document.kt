package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.StateFlow
import java.io.LineNumberReader
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch


class Document(
    private var reader: LineNumberReader,
    private val documentUri: String
) : Node(null, 0, "#document", DOCUMENT_NODE) {

    @OptIn(ExperimentalAtomicApi::class)
    private val uids = AtomicLong(0L)
    private val factory: ElementFactory = ElementFactory.Companion.instance

    private val nodes: MutableMap<Long, Node> = mutableMapOf()
    private var doctype: DocumentType? = null
    private var isDocTypeXHTML = false
    private var xmlEncoding: String? = null
    private var xmlStandalone = false


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

    fun getBaseUri(): String {
        return this.documentUri
    }


    fun load() {

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


    fun isXML(): Boolean {
        return isDocTypeXHTML
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

    // todo
    fun createCDATASection(data: String): CDataSection {
        return CDataSection(this, nextUid(), data)
    }

    @Throws(DOMException::class)
    fun createProcessingInstruction(target: String, data: String): ProcessingInstruction {
        return ProcessingInstruction(
            this, nextUid(),
            target, data
        )
    }

    fun getXmlEncoding(): String? {
        return this.xmlEncoding
    }

    fun getXmlStandalone(): Boolean {
        return this.xmlStandalone
    }

    // todo
    fun setXmlStandalone(xmlStandalone: Boolean) {
        this.xmlStandalone = xmlStandalone
    }


    fun getDocumentURI(): String? {
        return this.documentUri.toString()
    }

    fun childNodes(uid: Long): List<Node> {
        return nodes[uid]?.children() ?: emptyList()
    }

    fun node(uid: Long): Node? {
        return nodes[uid]
    }

    fun data(entity: Entity): StateFlow<String> {
        return (nodes[entity.uid] as Text).data
    }

    companion object {
        private const val XHTML_STRICT_PUBLIC_ID = "-//W3C//DTD XHTML 1.0 Strict//EN"
        private const val XHTML_STRICT_SYS_ID = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    }
}
