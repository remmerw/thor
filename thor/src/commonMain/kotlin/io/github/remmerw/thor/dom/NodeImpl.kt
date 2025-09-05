package io.github.remmerw.thor.dom

import org.w3c.dom.Document
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.Text
import org.w3c.dom.UserDataHandler

abstract class NodeImpl(
    var document: Document?,
    private val uid: Long,
    private val name: String
) : Node {

    private val nodes = mutableListOf<Node>()

    protected var parent: Node? = null

    override fun getAttributes(): NamedNodeMap? {
        return null  // todo
    }

    override fun getNodeName(): String {
        return this.name
    }

    fun nodes(): List<Node> {
        return nodes
    }

    override fun cloneNode(p0: Boolean): Node? {
        TODO("Not yet implemented")
    }

    fun uid(): Long {
        return uid
    }

    @Throws(DOMException::class)
    override fun appendChild(newChild: Node?): Node {
        if (newChild != null) {

            if (isInclusiveAncestorOf(newChild)) {
                val prevParent = newChild.parentNode
                if (prevParent is NodeImpl) {
                    prevParent.removeChild(newChild)
                }
            } else if ((newChild is NodeImpl) && newChild.isInclusiveAncestorOf(this)) {
                throw DOMException(
                    DOMException.HIERARCHY_REQUEST_ERR,
                    "Trying to append an ancestor element."
                )
            }

            this.nodes.add(newChild as NodeImpl)

            return newChild
        } else {
            throw DOMException(DOMException.INVALID_ACCESS_ERR, "Trying to append a null child!")
        }
    }


    protected fun getNodeList(filter: NodeFilter): NodeList {
        val collection: MutableList<NodeImpl> = mutableListOf()

        this.appendChildrenToCollectionImpl(filter, collection)

        return NodeListImpl(collection.toList())
    }


    fun getDescendants(
        filter: NodeFilter,
        nestIntoMatchingNodes: Boolean
    ): ArrayList<NodeImpl> {
        val al = ArrayList<NodeImpl>()

        this.extractDescendantsArrayImpl(filter, al, nestIntoMatchingNodes) // todo

        return al
    }


    private fun extractDescendantsArrayImpl(
        filter: NodeFilter,
        al: ArrayList<NodeImpl>,
        nestIntoMatchingNodes: Boolean
    ) {
        this.nodes.forEach { node ->
            val n = node as NodeImpl
            if (filter.accept(n)) {
                al.add(n)
                if (nestIntoMatchingNodes) {
                    n.extractDescendantsArrayImpl(filter, al, nestIntoMatchingNodes)
                }
            } else if (n.nodeType == Node.ELEMENT_NODE) {
                n.extractDescendantsArrayImpl(filter, al, nestIntoMatchingNodes)
            }
        }
    }

    private fun appendChildrenToCollectionImpl(
        filter: NodeFilter,
        collection: MutableList<NodeImpl>
    ) {
        nodes.forEach { node ->
            if (filter.accept(node as NodeImpl)) {
                collection.add(node)
            }
            node.appendChildrenToCollectionImpl(filter, collection)
        }
    }


    protected fun nodeIndex(): Int {
        val parent = this.parentNode as NodeImpl?
        return parent?.getChildIndex(this) ?: -1
    }

    protected fun getChildIndex(child: Node?): Int {

        val nl = this.nodes
        return nl.indexOf(child)

    }


    private fun isAncestorOf(other: Node): Boolean {
        val parent = other.parentNode as NodeImpl?
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

    @Throws(DOMException::class)
    override fun compareDocumentPosition(other: Node?): Short {
        val parent = this.parentNode
        if (other !is NodeImpl) {
            throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Unknwon node implementation")
        }
        if ((parent != null) && (parent === other.parentNode)) {
            val thisIndex = this.nodeIndex()
            val otherIndex =
                other.nodeIndex()
            if ((thisIndex == -1) || (otherIndex == -1)) {
                return Node.DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC
            }
            if (thisIndex < otherIndex) {
                return Node.DOCUMENT_POSITION_FOLLOWING
            } else {
                return Node.DOCUMENT_POSITION_PRECEDING
            }
        } else if (this.isAncestorOf(other)) {
            return Node.DOCUMENT_POSITION_CONTAINED_BY
        } else if (other.isAncestorOf(this)) {
            return Node.DOCUMENT_POSITION_CONTAINS
        } else {
            return Node.DOCUMENT_POSITION_DISCONNECTED
        }
    }


    override fun getOwnerDocument(): Document? {
        return document
    }

    protected fun setOwnerDocument(value: Document) {
        this.document = value

    }

    fun setOwnerDocument(value: Document, deep: Boolean) {
        setOwnerDocument(value)
        if (deep) {

            val nl = this.nodes
            val i: MutableIterator<Node?> = nl.iterator()
            while (i.hasNext()) {
                val child = i.next() as NodeImpl
                child.setOwnerDocument(value, deep)
            }

        }
    }

    protected fun visitImpl(visitor: NodeVisitor) {

        visitor.visit(this)

        val nl = this.nodes
        val i: MutableIterator<Node?> = nl.iterator()
        while (i.hasNext()) {
            val child = i.next() as NodeImpl
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
    override fun insertBefore(newChild: Node, refChild: Node?): Node {

        if (newChild is NodeImpl) {
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
        val nl = this.nodes
        val idx =
            if (refChild == null) nl.size else (nl.indexOf(refChild))
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "refChild not found")
        }
        nl.add(idx, newChild as NodeImpl)


        return newChild
    }


    @Throws(DOMException::class)
    override fun replaceChild(newChild: Node?, oldChild: Node?): Node? {

        if (this.isInclusiveAncestorOf(newChild)) {
            throw DOMException(
                DOMException.HIERARCHY_REQUEST_ERR,
                "newChild is already a child of the node"
            )
        }
        if ((newChild is NodeImpl) && newChild.isInclusiveAncestorOf(this)) {
            throw DOMException(
                DOMException.HIERARCHY_REQUEST_ERR,
                "Trying to set an ancestor element as a child."
            )
        }

        val nl = this.nodes
        val idx = nl.indexOf(oldChild)
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found")
        }
        nl.set(idx, newChild!! as NodeImpl)

        return newChild
    }

    @Throws(DOMException::class)
    override fun removeChild(oldChild: Node?): Node? {

        if (!nodes.remove(oldChild)) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found")
        }

        return oldChild
    }


    override fun hasChildNodes(): Boolean {

        return nodes.isNotEmpty()

    }

    override fun getBaseURI(): String? {
        val document = this.document
        return if (document == null) null else document.baseURI
    }

    override fun getChildNodes(): NodeList {

        val nl = this.nodes
        return NodeListImpl(nl.toList())

    }

    override fun getFirstChild(): Node? {

        return nodes().first()

    }

    override fun getLastChild(): Node? {

        return this.nodes.last()

    }

    private fun getPreviousTo(node: Node?): Node? {

        val nl = this.nodes
        val idx = nl.indexOf(node)
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "node not found")
        }
        return try {
            nl[idx - 1]
        } catch (_: Throwable) {
            null
        }

    }

    private fun getNextTo(node: Node?): Node? {

        val nl = this.nodes
        val idx = nl.indexOf(node)
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "node not found")
        }
        return try {
            nl[idx + 1]
        } catch (_: Throwable) {
            null
        }

    }

    override fun getPreviousSibling(): Node? {
        val parent = this.parentNode as NodeImpl?
        return parent?.getPreviousTo(this)
    }

    override fun getNextSibling(): Node? {
        val parent = this.parentNode as NodeImpl?
        return parent?.getNextTo(this)
    }

    override fun getFeature(feature: String?, version: String?): Any? {
        // TODO What should this do?
        return null
    }

    override fun setUserData(key: String, data: Any?, handler: UserDataHandler?): Any? {
        TODO("not implemented yet")
    }

    override fun getUserData(key: String?): Any? {
        TODO("not implemented yet")
    }

    override fun hasAttributes(): Boolean {
        return false
    }

    override fun getNamespaceURI(): String? {
        return null
    }

    override fun getPrefix(): String? {
        TODO("Not supported yet")
    }

    override fun setPrefix(prefix: String?) {
        TODO("Not supported yet")
    }


    @Throws(DOMException::class)
    override fun getTextContent(): String? {
        val sb = StringBuffer()

        val nl = this.nodes
        val i = nl.iterator()
        while (i.hasNext()) {
            val node = i.next()
            val type = node.nodeType
            when (type) {
                Node.CDATA_SECTION_NODE, Node.TEXT_NODE, Node.ELEMENT_NODE -> {
                    val textContent = node.textContent
                    if (textContent != null) {
                        sb.append(textContent)
                    }
                }

                else -> {}
            }
        }

        return sb.toString()
    }


    override fun setTextContent(textContent: String) {

        this.removeChildrenImpl(TextFilter())
        if ("" != textContent) {
            val impl = ownerDocument!! as DocumentImpl
            val t = TextImpl(
                ownerDocument!!,
                impl.nextUid(), textContent
            )
            t.parent = (this)

            this.nodes.add(t)
        }


    }

    protected fun removeChildrenImpl(filter: NodeFilter) {
        val nl = this.nodes
        val len = nl.size
        var i = len
        while (--i >= 0) {
            val node: Node = nl[i]
            if (filter.accept(node)) {
                nl.removeAt(i)
            }
        }
    }

    fun insertAfter(newChild: Node?, refChild: Node?): Node? {

        val nl = this.nodes
        val idx = nl.indexOf(refChild)
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "refChild not found")
        }
        nl.add(idx + 1, newChild!! as NodeImpl)



        return newChild
    }

    fun replaceAdjacentTextNodes(node: Text, textContent: String?): Text {


        val nl = this.nodes
        val idx = nl.indexOf(node as NodeImpl)
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "Node not a child")
        }
        var firstIdx = idx
        val toDelete: MutableList<Text> = mutableListOf()
        run {
            var adjIdx = idx
            while (--adjIdx >= 0) {
                val child: Any? = this.nodes[adjIdx]
                if (child is Text) {
                    firstIdx = adjIdx
                    toDelete.add(child)
                }
            }
        }
        val length = this.nodes.size
        var adjIdx = idx
        while (++adjIdx < length) {
            val child: Any? = this.nodes[adjIdx]
            if (child is Text) {
                toDelete.add(child)
            }
        }
        this.nodes.removeAll(toDelete)
        val impl = ownerDocument!! as DocumentImpl
        val textNode = TextImpl(ownerDocument!!, impl.nextUid(), textContent!!)
        textNode.parent = (this)
        this.nodes.add(firstIdx, textNode)
        return textNode


    }

    protected fun replaceAdjacentTextNodes(node: Text): Text {


        val nl = this.nodes
        val idx = nl.indexOf(node as NodeImpl)
        if (idx == -1) {
            throw DOMException(DOMException.NOT_FOUND_ERR, "Node not a child")
        }
        val textBuffer = StringBuffer()
        var firstIdx = idx
        val toDelete: MutableList<Text> = mutableListOf()
        run {
            var adjIdx = idx
            while (--adjIdx >= 0) {
                val child: Any? = this.nodes[adjIdx]
                if (child is Text) {
                    firstIdx = adjIdx
                    toDelete.add(child)
                    textBuffer.append(child.nodeValue)
                }
            }
        }
        val length = this.nodes.size
        var adjIdx = idx
        while (++adjIdx < length) {
            val child: Any? = this.nodes.get(adjIdx)
            if (child is Text) {
                toDelete.add(child)
                textBuffer.append(child.nodeValue)
            }
        }
        this.nodes.removeAll(toDelete)
        val impl = ownerDocument!! as DocumentImpl
        val textNode = TextImpl(
            ownerDocument!!, impl.nextUid(),
            textBuffer.toString()
        )

        textNode.parent = (this)
        this.nodes.add(firstIdx, textNode)
        return textNode


    }

    override fun getParentNode(): Node? {
        return this.parent
    }

    override fun isSameNode(other: Node?): Boolean {
        return this === other
    }

    override fun isSupported(feature: String?, version: String): Boolean {
        return ("HTML" == feature && (version <= "4.01"))
    }

    override fun lookupNamespaceURI(prefix: String?): String? {
        return null
    }

    open fun equalAttributes(arg: Node?): Boolean {
        return false
    }

    override fun isEqualNode(arg: Node?): Boolean {
        return (arg is NodeImpl) && (this.nodeType == arg.nodeType) && this.nodeName == arg.nodeName
                && this.nodeValue == arg.nodeValue && this.localName == arg.localName
                && this.nodes == arg.nodes && this.equalAttributes(arg)
    }

    override fun isDefaultNamespace(namespaceURI: String?): Boolean {
        return namespaceURI == null
    }

    override fun lookupPrefix(namespaceURI: String?): String? {
        return null
    }

    override fun toString(): String {
        return "$nodeName($uid)"
    }

    override fun normalize() {

        val nl = this.nodes
        nl.iterator()
        val textNodes: MutableList<Node> = mutableListOf()
        var prevText = false
        nodes().forEach { node ->
            val child = node
            if (child.nodeType == Node.TEXT_NODE) {
                if (!prevText) {
                    prevText = true
                    textNodes.add(child)
                }
            } else {
                prevText = false
            }
        }
        nodes().forEach { node ->
            val text = node as Text
            this.replaceAdjacentTextNodes(text)
        }

    }
}

