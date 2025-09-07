package io.github.remmerw.thor.dom


abstract class Node(
    var document: Document?,
    private val uid: Long,
    val name: String,
    private val type: Short
) {
    init {
        if (document is Document) {
            (document as Document).addNode(this)
        }
    }

    private val children = mutableListOf<Node>()

    protected var parent: Node? = null


    fun getNodeName(): String {
        return this.name
    }

    fun getNodeType(): Short {
        return type
    }

    fun children(): List<Node> {
        return children
    }


    fun uid(): Long {
        return uid
    }

    @Throws(DOMException::class)
    fun appendChild(newChild: Node): Node {

        if (isInclusiveAncestorOf(newChild)) {
            val prevParent = newChild.getParentNode()
            if (prevParent is Node) {
                prevParent.removeChild(newChild)
            }
        } else if (newChild.isInclusiveAncestorOf(this)) {
            throw DOMException(
                DOMException.HIERARCHY_REQUEST_ERR,
                "Trying to append an ancestor element."
            )
        }

        this.children.add(newChild)

        return newChild
    }


    private fun appendChildrenToCollectionImpl(
        filter: NodeFilter,
        collection: MutableList<Node>
    ) {
        children.forEach { node ->
            if (filter.accept(node)) {
                collection.add(node)
            }
            node.appendChildrenToCollectionImpl(filter, collection)
        }
    }

    private fun isAncestorOf(other: Node): Boolean {
        val parent = other.getParentNode()
        return if (parent === this) {
            true
        } else if (parent == null) {
            false
        } else {
            this.isAncestorOf(parent)
        }
    }

    private fun isInclusiveAncestorOf(other: Node?): Boolean {
        return if (other === this) {
            true
        } else if (other == null) {
            false
        } else {
            this.isAncestorOf(other)
        }
    }


    fun getOwnerDocument(): Document? {
        return document
    }

    protected fun setOwnerDocument(value: Document) {
        this.document = value
    }


    protected fun visitImpl(visitor: NodeVisitor) {

        visitor.visit(this)

        val nl = this.children
        val i: MutableIterator<Node?> = nl.iterator()
        while (i.hasNext()) {
            val child = i.next() as Node
            child.visit(visitor)
        }
    }

    protected fun visit(visitor: NodeVisitor) {
        this.visitImpl(visitor)
    }


    @Throws(DOMException::class)
    fun removeChild(oldChild: Node?): Node? {

        if (!children.remove(oldChild)) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found")
        }

        return oldChild
    }

    fun getParentNode(): Node? {
        return this.parent
    }

    fun entity(): Entity {
        return Entity(uid, name)
    }

    override fun toString(): String {
        return "$name($uid)"
    }


    companion object {
        const val ELEMENT_NODE: Short = 1
        const val TEXT_NODE: Short = 3
        const val CDATA_SECTION_NODE: Short = 4
        const val PROCESSING_INSTRUCTION_NODE: Short = 7
        const val COMMENT_NODE: Short = 8
        const val DOCUMENT_NODE: Short = 9
        const val DOCUMENT_TYPE_NODE: Short = 10
    }
}

