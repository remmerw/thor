package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.StateFlow
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch


class Model() : Node(null, 0, "#document") {

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

    internal fun createElement(name: String): Element {
        val uid = this.nextUid()
        return Element(this, uid, name.uppercase())
    }


    internal fun createTextNode(data: String): Text {
        return Text(this, nextUid(), data)
    }

    internal fun createComment(data: String): Comment {
        return Comment(this, nextUid(), data)
    }

    // todo
    internal fun createCDATASection(data: String): CDataSection {
        return CDataSection(this, nextUid(), data)
    }

    internal fun createProcessingInstruction(
        name: String,
        data: String
    ): ProcessingInstruction {
        return ProcessingInstruction(
            this, nextUid(), name, data
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
