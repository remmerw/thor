package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.StateFlow
import java.io.LineNumberReader
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch


class Document(
    private var reader: LineNumberReader,
    private val documentUri: String
) : Node(null, null, 0, "#document") {

    @OptIn(ExperimentalAtomicApi::class)
    private val uids = AtomicLong(0L)

    private val nodes: MutableMap<Long, Node> = mutableMapOf()
    private var doctype: DocumentType? = null
    private var isDocTypeXHTML = false
    private var xmlEncoding: String? = null


    init {
        this.setOwnerDocument(this)
        this.nodes.put(0, this)
    }

    internal fun addNode(node: Node) {
        nodes.put(node.uid, node)
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

    fun createElement(parent: Node, name: String): Element {
        val uid = this.nextUid()
        return Element(this, parent, uid, name.uppercase())
    }


    fun createTextNode(parent: Node, data: String): Text {
        return Text(this, parent, nextUid(), data)
    }

    fun createComment(parent: Node, data: String): Comment {
        return Comment(this, parent, nextUid(), data)
    }

    // todo
    fun createCDATASection(parent: Node, data: String): CDataSection {
        return CDataSection(this, parent, nextUid(), data)
    }


    fun createProcessingInstruction(
        parent: Node,
        name: String,
        data: String
    ): ProcessingInstruction {
        return ProcessingInstruction(
            this, parent, nextUid(), name, data
        )
    }

    fun getXmlEncoding(): String? {
        return this.xmlEncoding
    }

    fun getDocumentURI(): String? {
        return this.documentUri
    }

    fun children(uid: Long): List<Node> {
        return nodes[uid]?.children() ?: emptyList()
    }

    fun node(uid: Long): Node? {
        return nodes[uid]
    }

    fun data(entity: Entity): StateFlow<String> {
        return (nodes[entity.uid] as Text).data
    }

}
