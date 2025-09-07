package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.StateFlow
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch


class Model() : Node(null, null, 0, "#document") {

    @OptIn(ExperimentalAtomicApi::class)
    private val uids = AtomicLong(0L)
    private val nodes: MutableMap<Long, Node> = mutableMapOf()
    private var doctype: DocumentType? = null


    init {
        this.setModel(this)
        this.nodes.put(0, this)
    }

    internal fun addNode(node: Node) {
        nodes.put(node.uid, node)
    }

    @OptIn(ExperimentalAtomicApi::class)
    fun nextUid(): Long {
        return uids.incrementAndFetch()
    }


    internal fun node(entity: Entity): Node {
        return nodes[entity.uid]!!
    }


    fun getDoctype(): DocumentType? {
        return this.doctype
    }


    fun setDoctype(doctype: DocumentType) {
        this.doctype = doctype

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

    fun children(entity: Entity): StateFlow<List<Entity>> {
        return nodes[entity.uid]!!.children
    }

    fun attributes(entity: Entity): StateFlow<Map<String, String>> {
        return (nodes[entity.uid] as Element).attributes
    }

    fun data(entity: Entity): StateFlow<String> {
        return (nodes[entity.uid] as Text).data
    }

}
