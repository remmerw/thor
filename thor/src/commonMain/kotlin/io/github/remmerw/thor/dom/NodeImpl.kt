package io.github.remmerw.thor.dom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.Text
import org.w3c.dom.UserDataHandler
import java.net.URL
import java.util.LinkedList
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.Volatile

abstract class NodeImpl(doc: Document?) : NodeModel {

    private var document by mutableStateOf(doc)
    private var nodeList = mutableStateListOf<NodeModel>()

    override fun nodes(): SnapshotStateList<NodeModel> {
        return nodeList
    }


    override fun cloneNode(p0: Boolean): Node? {
        TODO("Not yet implemented")
    }

    /**
     * A tree lock is less deadlock-prone than a node-level lock. This is assigned
     * in setOwnerDocument.
     */
    @Volatile
    var treeLock: Any = this

    @Volatile
    protected var nodeParent: Node? = null
    private var userData: MutableMap<String, Any> = mutableMapOf()

    private val userDataHandlers: MutableMap<String, UserDataHandler> = mutableMapOf()

    @Volatile
    private var prefix: String? = null


    @Throws(DOMException::class)
    override fun appendChild(newChild: Node?): Node {
        if (newChild != null) {
            synchronized(this.treeLock) {
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


                this.nodeList.add(newChild as NodeModel)

                if (newChild is ElementImpl) {
                    newChild.finish()
                }
            }



            return newChild
        } else {
            throw DOMException(DOMException.INVALID_ACCESS_ERR, "Trying to append a null child!")
        }
    }


    protected fun removeAllChildrenImpl() {
        synchronized(this.treeLock) {
            this.nodeList.clear()
        }
    }

    protected fun getNodeList(filter: NodeFilter): NodeList {
        val collection: MutableList<Node> = mutableListOf()
        synchronized(this.treeLock) {
            this.appendChildrenToCollectionImpl(filter, collection)
        }
        return NodeListImpl(collection.toList())
    }


    /**
     * Creates an `ArrayList` of descendent nodes that the given filter
     * condition.
     */
    fun getDescendants(
        filter: NodeFilter,
        nestIntoMatchingNodes: Boolean
    ): java.util.ArrayList<NodeImpl?> {
        val al = java.util.ArrayList<NodeImpl?>()
        synchronized(this.treeLock) {
            this.extractDescendantsArrayImpl(filter, al, nestIntoMatchingNodes)
        }
        return al
    }


    private fun extractDescendantsArrayImpl(
        filter: NodeFilter,
        al: java.util.ArrayList<NodeImpl?>,
        nestIntoMatchingNodes: Boolean
    ) {
        this.nodeList.forEach { node ->
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
        collection: MutableList<Node>
    ) {
        nodeList.forEach { nodeModel ->
            val node = nodeModel as NodeImpl
            if (filter.accept(node)) {
                collection.add(node)
            }
            node.appendChildrenToCollectionImpl(filter, collection)
        }
    }


    fun nodeIndex(): Int {
        val parent =
            this.nodeParent as NodeImpl?
        return if (parent == null) -1 else parent.getChildIndex(this)
    }

    fun getChildIndex(child: Node?): Int {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            return nl.indexOf(child)
        }
    }


    private fun isAncestorOf(other: Node): Boolean {
        val parent = other.parentNode as NodeImpl?
        if (parent === this) {
            return true
        } else if (parent == null) {
            return false
        } else {
            return this.isAncestorOf(parent)
        }
    }

    private fun isInclusiveAncestorOf(other: Node?): Boolean {
        if (other === this) {
            return true
        } else if (other == null) {
            return false
        } else {
            return this.isAncestorOf(other)
        }
    }

    @Throws(DOMException::class)
    override fun compareDocumentPosition(other: Node?): Short {
        val parent = this.nodeParent
        if (other !is NodeImpl) {
            throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Unknwon node implementation")
        }
        if ((parent != null) && (parent === other.nodeParent)) {
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

    override fun getAttributes(): NamedNodeMap? {
        return null
    }

    override fun getOwnerDocument(): Document? {
        return document
    }

    open fun setOwnerDocument(value: Document) {
        this.document = value
        this.treeLock = value
    }

    open fun setOwnerDocument(value: Document, deep: Boolean) {
        setOwnerDocument(value)
        if (deep) {
            synchronized(this.treeLock) {
                val nl = this.nodeList
                val i: MutableIterator<Node?> = nl.iterator()
                while (i.hasNext()) {
                    val child = i.next() as NodeImpl
                    child.setOwnerDocument(value, deep)
                }
            }
        }
    }

    fun visitImpl(visitor: NodeVisitor) {
        try {
            visitor.visit(this)
        } catch (_: SkipVisitorException) {
            return
        } catch (sve: StopVisitorException) {
            throw sve
        }
        val nl = this.nodeList
        val i: MutableIterator<Node?> = nl.iterator()
        while (i.hasNext()) {
            val child = i.next() as NodeImpl
            try {
                // Call with child's synchronization
                child.visit(visitor)
            } catch (sve: StopVisitorException) {
                throw sve
            }
        }
    }

    fun visit(visitor: NodeVisitor) {
        synchronized(this.treeLock) {
            this.visitImpl(visitor)
        }
    }

    // Ongoing issue : 152
    // This is a changed and better version of the above. It gives the same number of pass / failures on http://web-platform.test:8000/dom/nodes/Node-insertBefore.html
    // Pass 2: FAIL: 24
    @Throws(DOMException::class)
    override fun insertBefore(newChild: Node, refChild: Node?): Node {
        synchronized(this.treeLock) {
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
            val nl = this.nodeList
            val idx =
                if (refChild == null) nl.size else (nl.indexOf(refChild))
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "refChild not found")
            }
            nl.add(idx, newChild as NodeModel)
        }

        return newChild
    }


    @Throws(DOMException::class)
    protected fun insertAt(newChild: Node?, idx: Int): Node? {
        synchronized(this.treeLock) {
            nodes().add(idx, newChild!! as NodeModel)
        }
        return newChild
    }

    @Throws(DOMException::class)
    override fun replaceChild(newChild: Node?, oldChild: Node?): Node? {
        synchronized(this.treeLock) {
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

            val nl = this.nodeList
            val idx = nl.indexOf(oldChild)
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found")
            }
            nl.set(idx, newChild!! as NodeModel)


        }


        return newChild
    }

    @Throws(DOMException::class)
    override fun removeChild(oldChild: Node?): Node? {
        synchronized(this.treeLock) {
            if (!nodeList.remove(oldChild)) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found")
            }
        }
        return oldChild
    }


    @Throws(DOMException::class)
    fun removeChildAt(index: Int): Node {
        synchronized(this.treeLock) {
            return nodeList.removeAt(index)
        }
    }

    override fun hasChildNodes(): Boolean {
        synchronized(this.treeLock) {
            return nodeList.isNotEmpty()
        }
    }

    override fun getBaseURI(): String? {
        val document = this.document
        return if (document == null) null else document.baseURI
    }

    override fun getChildNodes(): NodeList {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            return NodeListImpl(nl.toList())
        }
    }

    override fun getFirstChild(): Node? {
        synchronized(this.treeLock) {
            return nodes().first()
        }
    }

    override fun getLastChild(): Node? {
        synchronized(this.treeLock) {
            return this.nodeList.last()
        }
    }

    private fun getPreviousTo(node: Node?): Node? {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            val idx = nl.indexOf(node)
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "node not found")
            }
            try {
                return nl[idx - 1]
            } catch (_: Throwable) {
                return null
            }
        }
    }

    private fun getNextTo(node: Node?): Node? {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            val idx = nl.indexOf(node)
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "node not found")
            }
            try {
                return nl[idx + 1]
            } catch (_: IndexOutOfBoundsException) {
                return null
            }
        }
    }

    override fun getPreviousSibling(): Node? {
        val parent = this.nodeParent as NodeImpl?
        return if (parent == null) null else parent.getPreviousTo(this)
    }

    override fun getNextSibling(): Node? {
        val parent = this.nodeParent as NodeImpl?
        return if (parent == null) null else parent.getNextTo(this)
    }

    override fun getFeature(feature: String?, version: String?): Any? {
        // TODO What should this do?
        return null
    }

    override fun setUserData(key: String, data: Any?, handler: UserDataHandler?): Any? {
        // here we spent some effort preventing our maps from growing too much
        synchronized(this) {
            if (handler != null) {
                this.userDataHandlers.remove(key)
                this.userDataHandlers.put(key, handler)
            }

            return if (data != null) {
                userData.put(key, data)
            } else {
                userData.remove(key)
            }
        }
    }

    override fun getUserData(key: String?): Any? {
        synchronized(this) {
            return userData[key]
        }
    }

    override fun hasAttributes(): Boolean {
        return false
    }

    override fun getNamespaceURI(): String? {
        return null
    }

    override fun getPrefix(): String? {
        return this.prefix
    }

    @Throws(DOMException::class)
    override fun setPrefix(prefix: String?) {
        this.prefix = prefix
    }

    //abstract override fun getNodeType(): Short

    /**
     * Gets the text content of this node and its descendents.
     */
    @Throws(DOMException::class)
    override fun getTextContent(): String? {
        val sb = StringBuffer()
        synchronized(this.treeLock) {
            val nl = this.nodeList
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
        }
        return sb.toString()
    }


    override fun setTextContent(textContent: String) {
        synchronized(this.treeLock) {
            this.removeChildrenImpl(TextFilter())
            if ("" != textContent) {
                val t = TextImpl(ownerDocument!!, textContent)
                t.setParentImpl(this)

                this.nodeList.add(t)
            }
        }


    }

    protected fun removeChildren(filter: NodeFilter) {
        synchronized(this.treeLock) {
            this.removeChildrenImpl(filter)
        }
    }

    protected fun removeChildrenImpl(filter: NodeFilter) {
        val nl = this.nodeList
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
        synchronized(this.treeLock) {
            val nl = this.nodeList
            val idx = nl.indexOf(refChild)
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "refChild not found")
            }
            nl.add(idx + 1, newChild!! as NodeModel)

        }

        return newChild
    }

    fun replaceAdjacentTextNodes(node: Text, textContent: String?): Text {

        synchronized(this.treeLock) {
            val nl = this.nodeList
            val idx = nl.indexOf(node as NodeModel)
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "Node not a child")
            }
            var firstIdx = idx
            val toDelete: MutableList<Any?> = LinkedList<Any?>()
            run {
                var adjIdx = idx
                while (--adjIdx >= 0) {
                    val child: Any? = this.nodeList[adjIdx]
                    if (child is Text) {
                        firstIdx = adjIdx
                        toDelete.add(child)
                    }
                }
            }
            val length = this.nodeList.size
            var adjIdx = idx
            while (++adjIdx < length) {
                val child: Any? = this.nodeList.get(adjIdx)
                if (child is Text) {
                    toDelete.add(child)
                }
            }
            this.nodeList.removeAll(toDelete)
            val textNode = TextImpl(ownerDocument!!, textContent!!)
            textNode.setParentImpl(this)
            this.nodeList.add(firstIdx, textNode)
            return textNode
        }

    }

    fun replaceAdjacentTextNodes(node: Text): Text {

        synchronized(this.treeLock) {
            val nl = this.nodeList
            val idx = nl.indexOf(node as NodeModel)
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "Node not a child")
            }
            val textBuffer = StringBuffer()
            var firstIdx = idx
            val toDelete: MutableList<Any?> = LinkedList<Any?>()
            run {
                var adjIdx = idx
                while (--adjIdx >= 0) {
                    val child: Any? = this.nodeList[adjIdx]
                    if (child is Text) {
                        firstIdx = adjIdx
                        toDelete.add(child)
                        textBuffer.append(child.nodeValue)
                    }
                }
            }
            val length = this.nodeList.size
            var adjIdx = idx
            while (++adjIdx < length) {
                val child: Any? = this.nodeList.get(adjIdx)
                if (child is Text) {
                    toDelete.add(child)
                    textBuffer.append(child.nodeValue)
                }
            }
            this.nodeList.removeAll(toDelete)
            val textNode = TextImpl(ownerDocument!!, textBuffer.toString())

            textNode.setParentImpl(this)
            this.nodeList.add(firstIdx, textNode)
            return textNode
        }

    }

    override fun getParentNode(): Node? {
        // Should it be synchronized? Could have side-effects.
        return this.nodeParent
    }

    override fun isSameNode(other: Node?): Boolean {
        return this === other
    }

    override fun isSupported(feature: String?, version: String): Boolean {
        return ("HTML" == feature && (version.compareTo("4.01") <= 0))
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
                && this.nodeList == arg.nodeList && this.equalAttributes(arg)
    }

    override fun isDefaultNamespace(namespaceURI: String?): Boolean {
        return namespaceURI == null
    }

    override fun lookupPrefix(namespaceURI: String?): String? {
        return null
    }

    override fun normalize() {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            nl.iterator()
            val textNodes: MutableList<Node> = LinkedList<Node>()
            var prevText = false
            nodes().forEach { nodeModel ->
                val child = nodeModel
                if (child.nodeType == Node.TEXT_NODE) {
                    if (!prevText) {
                        prevText = true
                        textNodes.add(child)
                    }
                } else {
                    prevText = false
                }
            }
            nodes().forEach { nodeModel ->
                val text = nodeModel as Text
                this.replaceAdjacentTextNodes(text)
            }
        }
    }


    override fun toString(): String {
        return this.nodeName
    }


    fun setParentImpl(parent: Node?) {
        // Call holding treeLock.
        this.nodeParent = parent
    }

    open fun getDocumentURL(): URL? {
        val doc: Any? = this.document
        return if (doc is DocumentImpl) {
            doc.getDocumentURL()
        } else {
            null
        }
    }

    open fun warn(message: String?, err: Throwable?) {
        logger.log(Level.WARNING, message, err)
    }

    open fun warn(message: String?) {
        logger.log(Level.WARNING, message)
    }


    companion object {
        @JvmStatic
        protected val logger: Logger = Logger.getLogger(NodeImpl::class.java.name)
    }
}
