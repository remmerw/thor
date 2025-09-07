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
    fun appendChild(newChild: Node?): Node {
        if (newChild != null) {

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
        } else {
            throw DOMException(DOMException.INVALID_ACCESS_ERR, "Trying to append a null child!")
        }
    }


    fun getDescendants(
        filter: NodeFilter,
        nestIntoMatchingNodes: Boolean
    ): ArrayList<Node> {
        val al = ArrayList<Node>()

        this.extractDescendantsArrayImpl(filter, al, nestIntoMatchingNodes) // todo

        return al
    }


    private fun extractDescendantsArrayImpl(
        filter: NodeFilter,
        al: ArrayList<Node>,
        nestIntoMatchingNodes: Boolean
    ) {
        this.children.forEach { node ->
            val n = node
            if (filter.accept(n)) {
                al.add(n)
                if (nestIntoMatchingNodes) {
                    n.extractDescendantsArrayImpl(filter, al, nestIntoMatchingNodes)
                }
            } else if (n.getNodeType() == ELEMENT_NODE) {
                n.extractDescendantsArrayImpl(filter, al, nestIntoMatchingNodes)
            }
        }
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


    protected fun getChildIndex(child: Node?): Int {
        val nl = this.children
        return nl.indexOf(child)
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

    // Ongoing issue : 152
    // This is a changed and better version of the above. It gives the same number of pass / failures on http://web-platform.test:8000/dom/nodes/Node-insertBefore.html
    // Pass 2: FAIL: 24
    @Throws(DOMException::class)
    fun insertBefore(newChild: Node, refChild: Node?): Node {

        if (newChild is Node) {
            if (newChild.isInclusiveAncestorOf(this)) {
                throw DOMException(
                    DOMException.HIERARCHY_REQUEST_ERR,
                    "new child is an ancestor"
                )
            }
        }
        // From what I understand from https://developer.mozilla.org/en-US/docs/Web/API/Node.insertBefore
        // a null or undefined refChild will cause the new child to be appended at the end of the list
        // otherwise, this function will throw an exception if refChild is not found in the child list
        val nl = this.children
        val idx =
            if (refChild == null) nl.size else (nl.indexOf(refChild))
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "refChild not found")
        }
        nl.add(idx, newChild)


        return newChild
    }


    @Throws(DOMException::class)
    fun replaceChild(newChild: Node?, oldChild: Node?): Node? {

        if (this.isInclusiveAncestorOf(newChild)) {
            throw DOMException(
                DOMException.HIERARCHY_REQUEST_ERR,
                "newChild is already a child of the node"
            )
        }
        if ((newChild is Node) && newChild.isInclusiveAncestorOf(this)) {
            throw DOMException(
                DOMException.HIERARCHY_REQUEST_ERR,
                "Trying to set an ancestor element as a child."
            )
        }

        val nl = this.children
        val idx = nl.indexOf(oldChild)
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found")
        }
        nl.set(idx, newChild!!)

        return newChild
    }

    @Throws(DOMException::class)
    fun removeChild(oldChild: Node?): Node? {

        if (!children.remove(oldChild)) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found")
        }

        return oldChild
    }


    fun hasChildNodes(): Boolean {
        return children.isNotEmpty()
    }


    private fun getPreviousTo(node: Node?): Node? {

        val nl = this.children
        val idx = nl.indexOf(node)
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "node not found")
        }
        return try {
            nl[idx - 1]
        } catch (throwable: Throwable) {
            debug(throwable)
            null
        }

    }

    private fun getNextTo(node: Node?): Node? {

        val nl = this.children
        val idx = nl.indexOf(node)
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "node not found")
        }
        return try {
            nl[idx + 1]
        } catch (throwable: Throwable) {
            debug(throwable)
            null
        }

    }

    fun getPreviousSibling(): Node? {
        val parent = this.getParentNode()
        return parent?.getPreviousTo(this)
    }

    fun getNextSibling(): Node? {
        val parent = this.getParentNode()
        return parent?.getNextTo(this)
    }


    fun getParentNode(): Node? {
        return this.parent
    }

    open fun equalAttributes(arg: Node?): Boolean {
        return false
    }


    fun entity(): Entity {
        return Entity(uid, name)
    }

    override fun toString(): String {
        return "$name($uid)"
    }


    companion object {
        const val ELEMENT_NODE: Short = 1
        const val ATTRIBUTE_NODE: Short = 2
        const val TEXT_NODE: Short = 3
        const val CDATA_SECTION_NODE: Short = 4
        const val ENTITY_REFERENCE_NODE: Short = 5
        const val ENTITY_NODE: Short = 6
        const val PROCESSING_INSTRUCTION_NODE: Short = 7
        const val COMMENT_NODE: Short = 8
        const val DOCUMENT_NODE: Short = 9
        const val DOCUMENT_TYPE_NODE: Short = 10
        const val DOCUMENT_FRAGMENT_NODE: Short = 11
        const val NOTATION_NODE: Short = 12
        const val DOCUMENT_POSITION_DISCONNECTED: Short = 1
        const val DOCUMENT_POSITION_PRECEDING: Short = 2
        const val DOCUMENT_POSITION_FOLLOWING: Short = 4
        const val DOCUMENT_POSITION_CONTAINS: Short = 8
        const val DOCUMENT_POSITION_CONTAINED_BY: Short = 16
        const val DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC: Short = 32
    }
}

