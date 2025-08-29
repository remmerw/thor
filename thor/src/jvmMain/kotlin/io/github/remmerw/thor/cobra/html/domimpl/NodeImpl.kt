/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Sep 3, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import cz.vutbr.web.css.CSSException
import cz.vutbr.web.css.CSSFactory
import cz.vutbr.web.css.CombinedSelector
import cz.vutbr.web.css.RuleSet
import cz.vutbr.web.css.Selector
import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.js.Event
import io.github.remmerw.thor.cobra.html.parser.HtmlParser
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.html.style.StyleSheetRenderState
import io.github.remmerw.thor.cobra.js.AbstractScriptableDelegate
import io.github.remmerw.thor.cobra.js.HideFromJS
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.util.Strings
import io.github.remmerw.thor.cobra.util.Urls
import org.mozilla.javascript.Function
import org.w3c.dom.Attr
import org.w3c.dom.Comment
import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.ProcessingInstruction
import org.w3c.dom.Text
import org.w3c.dom.UserDataHandler
import org.w3c.dom.html.HTMLCollection
import java.io.IOException
import java.lang.Boolean
import java.net.URL
import java.util.Arrays
import java.util.LinkedList
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors
import kotlin.Any
import kotlin.Array
import kotlin.Exception
import kotlin.IllegalStateException
import kotlin.IndexOutOfBoundsException
import kotlin.Int
import kotlin.Short
import kotlin.String
import kotlin.Throwable
import kotlin.Throws
import kotlin.also
import kotlin.arrayOfNulls
import kotlin.concurrent.Volatile
import kotlin.plus
import kotlin.run
import kotlin.synchronized

// TODO: Implement org.w3c.dom.events.EventTarget ?
abstract class NodeImpl : AbstractScriptableDelegate(), Node, ModelNode {
    // Called in GUI thread always.
    // Called in GUI thread always.

    var uINode: UINode? = null
    protected var nodeList: ArrayList<Node>? = null

    @Volatile
    protected var document: Document? = null

    /**
     * A tree lock is less deadlock-prone than a node-level lock. This is assigned
     * in setOwnerDocument.
     */
    @Volatile
    var treeLock: Any = this

    @Volatile
    protected var notificationsSuspended: kotlin.Boolean = false

    @Volatile
    protected var parentNode: Node? = null
    private var userData: MutableMap<String?, Any?>? = null

    // TODO: Inform handlers on cloning, etc.
    private val userDataHandlers: MutableMap<String, UserDataHandler> = mutableMapOf()

    @Volatile
    private var prefix: String? = null

    /**
     * @return the attachment with the document. true if the element is attached
     * to the document, false otherwise. Document nodes are considered
     * attached by default.
     */

    var isAttachedToDocument: kotlin.Boolean = this is Document


    /**
     * Tries to get a UINode associated with the current node. Failing that, it
     * tries ancestors recursively. This method will return the closest
     * *block-level* renderer node, if any.
     */
    @HideFromJS
    fun findUINode(): UINode? {
        // Called in GUI thread always.
        val uiNode: UINode? = this.uINode
        if (uiNode != null) {
            return uiNode
        }
        val parentNode = this.parentNode as NodeImpl?
        return parentNode?.findUINode()
    }

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
                var nl = this.nodeList
                if (nl == null) {
                    nl = java.util.ArrayList<Node>(3)
                    this.nodeList = nl
                }
                nl.add(newChild)
                if (newChild is NodeImpl) {
                    newChild.handleAddedToParent(this)
                }
            }

            this.postChildListChanged()

            return newChild
        } else {
            throw DOMException(DOMException.INVALID_ACCESS_ERR, "Trying to append a null child!")
        }
    }

    // TODO not used by anyone
    protected fun removeAllChildren() {
        this.removeAllChildrenImpl()
    }

    protected fun removeAllChildrenImpl() {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            if (nl != null) {
                for (node in nl) {
                    if (node is NodeImpl) {
                        node.handleDeletedFromParent()
                    }
                }
                this.nodeList = null
            }
        }

        this.postChildListChanged()
    }

    protected fun getNodeList(filter: NodeFilter): NodeList {
        val collection: MutableCollection<Node> = ArrayList<Node>()
        synchronized(this.treeLock) {
            this.appendChildrenToCollectionImpl(filter, collection)
        }
        return NodeListImpl(collection)
    }

    open val childrenArray: Array<NodeImpl?>?
        /*
              * TODO: If this is not a w3c DOM method, we can return an Iterator instead of
              * creating a new array But, it changes the semantics slightly (when
              * modifications are needed during iteration). For those cases, we can retain
              * this method.
              */
        get() {
            val nl = this.nodeList
            synchronized(this.treeLock) {
                return if (nl == null) null else nl.toArray<NodeImpl?>(
                    EMPTY_ARRAY
                )
            }
        }

    val childCount: Int
        get() {
            synchronized(this.treeLock) {
                val nl = this.nodeList
                return if (nl == null) 0 else nl.size
            }
        }

    val children: HTMLCollection
        // TODO: This is needed to be implemented only by Element, Document and DocumentFragment as per https://developer.mozilla.org/en-US/docs/Web/API/ParentNode
        get() = DescendentHTMLCollection(
            this,
            NodeFilter.ElementFilter(),
            this.treeLock
        )

    /**
     * Creates an `ArrayList` of descendent nodes that the given filter
     * condition.
     */
    fun getDescendents(
        filter: NodeFilter,
        nestIntoMatchingNodes: kotlin.Boolean
    ): java.util.ArrayList<NodeImpl?> {
        val al = java.util.ArrayList<NodeImpl?>()
        synchronized(this.treeLock) {
            this.extractDescendentsArrayImpl(filter, al, nestIntoMatchingNodes)
        }
        return al
    }

    /**
     * Extracts all descendents that match the filter, except those descendents of
     * nodes that match the filter.
     *
     * @param filter
     * @param al
     */
    private fun extractDescendentsArrayImpl(
        filter: NodeFilter,
        al: java.util.ArrayList<NodeImpl?>,
        nestIntoMatchingNodes: kotlin.Boolean
    ) {
        val nl = this.nodeList
        if (nl != null) {
            val i: MutableIterator<Node?> = nl.iterator()
            while (i.hasNext()) {
                val n = i.next() as NodeImpl
                if (filter.accept(n)) {
                    al.add(n)
                    if (nestIntoMatchingNodes) {
                        n.extractDescendentsArrayImpl(filter, al, nestIntoMatchingNodes)
                    }
                } else if (n.getNodeType() == Node.ELEMENT_NODE) {
                    n.extractDescendentsArrayImpl(filter, al, nestIntoMatchingNodes)
                }
            }
        }
    }

    private fun appendChildrenToCollectionImpl(
        filter: NodeFilter,
        collection: MutableCollection<Node>
    ) {
        val nl = this.nodeList
        if (nl != null) {
            val i: MutableIterator<Node?> = nl.iterator()
            while (i.hasNext()) {
                val node = i.next() as NodeImpl
                if (filter.accept(node)) {
                    collection.add(node)
                }
                node.appendChildrenToCollectionImpl(filter, collection)
            }
        }
    }

    /**
     * Should create a node with some cloned properties, like the node name, but
     * not attributes or children.
     */
    protected abstract fun createSimilarNode(): Node?

    override fun cloneNode(deep: kotlin.Boolean): Node {
        // TODO: Synchronize with treeLock?
        try {
            val newNode = this.createSimilarNode()
            val children = this.childNodes
            val length = children.length
            for (i in 0..<length) {
                val child = children.item(i)
                val newChild = if (deep) child.cloneNode(deep) else child
                newNode.appendChild(newChild)
            }
            if (newNode is Element) {
                val nnmap = this.attributes
                if (nnmap != null) {
                    val nnlength = nnmap.length
                    for (i in 0..<nnlength) {
                        val attr = nnmap.item(i) as Attr
                        newNode.setAttributeNode(attr.cloneNode(true) as Attr?)
                    }
                }
            }

            synchronized(this) {
                if ((userDataHandlers != null) && (userData != null)) {

                    userDataHandlers.toMap().forEach { (k: String, handler: UserDataHandler) ->

                        handler.handle(

                            UserDataHandler.NODE_CLONED, k, userData!!.get(k), this, newNode
                        )
                    }
                }
            }

            return newNode
        } catch (err: Exception) {
            throw IllegalStateException(err.message)
        }
    }

    private val nodeIndex: Int
        get() {
            val parent =
                this.parentNode as NodeImpl?
            return if (parent == null) -1 else parent.getChildIndex(this)
        }

    fun getChildIndex(child: Node?): Int {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            return if (nl == null) -1 else nl.indexOf(child)
        }
    }

    fun getChildAtIndex(index: Int): Node? {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            try {
                return if (nl == null) null else nl.get(index)
            } catch (iob: IndexOutOfBoundsException) {
                this.warn("getChildAtIndex(): Bad index=" + index + " for node=" + this + ".")
                return null
            }
        }
    }

    /*
  public Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
    synchronized (this.treeLock) {
      final ArrayList<Node> nl = getNonEmptyNodeList();
      // int idx = nl == null ? -1 : nl.indexOf(refChild);
      int idx = nl.indexOf(refChild);
      if (idx == -1) {
        // The exception was misleading. -1 could have resulted from an empty node list too. (but that is no more the case)
        // throw new DOMException(DOMException.NOT_FOUND_ERR, "refChild not found");

        // From what I understand from https://developer.mozilla.org/en-US/docs/Web/API/Node.insertBefore
        // an invalid refChild will add the new child at the end of the list

        idx = nl.size();
      }
      nl.add(idx, newChild);
      if (newChild instanceof NodeImpl) {
        ((NodeImpl) newChild).handleAddedToParent(this);
      }
    }

    this.postChildListChanged();

    return newChild;
  }*/
    private fun isAncestorOf(other: Node): kotlin.Boolean {
        val parent = other.parentNode as NodeImpl?
        if (parent === this) {
            return true
        } else if (parent == null) {
            return false
        } else {
            return this.isAncestorOf(parent)
        }
    }

    private fun isInclusiveAncestorOf(other: Node?): kotlin.Boolean {
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
        val parent = this.parentNode
        if (other !is NodeImpl) {
            throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Unknwon node implementation")
        }
        if ((parent != null) && (parent === other.parentNode)) {
            val thisIndex = this.nodeIndex
            val otherIndex =
                other.nodeIndex
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
        return this.document
    }

    open fun setOwnerDocument(value: Document?) {
        this.document = value
        this.treeLock = if (value == null) this else value
    }

    open fun setOwnerDocument(value: Document?, deep: kotlin.Boolean) {
        this.document = value
        this.treeLock = if (value == null) this else value
        if (deep) {
            synchronized(this.treeLock) {
                val nl = this.nodeList
                if (nl != null) {
                    val i: MutableIterator<Node?> = nl.iterator()
                    while (i.hasNext()) {
                        val child = i.next() as NodeImpl
                        child.setOwnerDocument(value, deep)
                    }
                }
            }
        }
    }

    fun visitImpl(visitor: NodeVisitor) {
        try {
            visitor.visit(this)
        } catch (sve: SkipVisitorException) {
            return
        } catch (sve: StopVisitorException) {
            throw sve
        }
        val nl = this.nodeList
        if (nl != null) {
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
        if (newChild == null) {
            throw DOMException(DOMException.TYPE_MISMATCH_ERR, "child is null")
        }
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
            val nl = if (refChild == null) this.nonEmptyNodeList else this.nodeList
            val idx =
                if (refChild == null) nl!!.size else (if (nl == null) -1 else nl.indexOf(refChild))
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "refChild not found")
            }
            nl!!.add(idx, newChild)
            if (newChild is NodeImpl) {
                newChild.handleAddedToParent(this)
            }
        }

        this.postChildListChanged()

        return newChild
    }

    private val nonEmptyNodeList: ArrayList<Node>
        // TODO: Use this wherever nodeList needs to be non empty
        get() {
            var nl = this.nodeList
            if (nl == null) {
                nl = java.util.ArrayList<Node>()
                this.nodeList = nl
            }
            return nl
        }

    @Throws(DOMException::class)
    protected fun insertAt(newChild: Node?, idx: Int): Node? {
        synchronized(this.treeLock) {
            val nl = this.nonEmptyNodeList
            nl.add(idx, newChild!!)
            if (newChild is NodeImpl) {
                newChild.handleAddedToParent(this)
            }
        }

        this.postChildListChanged()

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
            val idx = if (nl == null) -1 else nl.indexOf(oldChild)
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found")
            }
            nl!!.set(idx, newChild!!)

            if (newChild is NodeImpl) {
                newChild.handleAddedToParent(this)
            }
            if (oldChild is NodeImpl) {
                oldChild.handleDeletedFromParent()
            }
        }

        this.postChildListChanged()

        return newChild
    }

    @Throws(DOMException::class)
    override fun removeChild(oldChild: Node?): Node? {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            if ((nl == null) || !nl.remove(oldChild)) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found")
            }
            if (oldChild is NodeImpl) {
                oldChild.handleDeletedFromParent()
            }
        }

        this.postChildListChanged()

        return oldChild
    }

    @HideFromJS
    @Throws(DOMException::class)
    fun removeChildAt(index: Int): Node {
        try {
            synchronized(this.treeLock) {
                val nl = this.nodeList
                if (nl == null) {
                    throw DOMException(DOMException.INDEX_SIZE_ERR, "Empty list of children")
                }
                val n = nl.removeAt(index)
                if (n == null) {
                    throw DOMException(DOMException.INDEX_SIZE_ERR, "No node with that index")
                }
                if (n is NodeImpl) {
                    n.handleDeletedFromParent()
                }
                return n
            }
        } finally {
            this.postChildListChanged()
        }
    }

    override fun hasChildNodes(): kotlin.Boolean {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            return (nl != null) && !nl.isEmpty()
        }
    }

    override fun getBaseURI(): String? {
        val document = this.document
        return if (document == null) null else document.baseURI
    }

    override fun getChildNodes(): NodeList {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            return NodeListImpl(if (nl == null) mutableListOf<Node>() else nl)
        }
    }

    override fun getFirstChild(): Node? {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            try {
                return if (nl == null) null else nl.get(0)
            } catch (iob: IndexOutOfBoundsException) {
                return null
            }
        }
    }

    override fun getLastChild(): Node? {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            try {
                return if (nl == null) null else nl.get(nl.size - 1)
            } catch (iob: IndexOutOfBoundsException) {
                return null
            }
        }
    }

    private fun getPreviousTo(node: Node?): Node? {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            val idx = if (nl == null) -1 else nl.indexOf(node)
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "node not found")
            }
            try {
                return nl!!.get(idx - 1)
            } catch (iob: IndexOutOfBoundsException) {
                return null
            }
        }
    }

    private fun getNextTo(node: Node?): Node? {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            val idx = if (nl == null) -1 else nl.indexOf(node)
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "node not found")
            }
            try {
                return nl!!.get(idx + 1)
            } catch (iob: IndexOutOfBoundsException) {
                return null
            }
        }
    }

    override fun getPreviousSibling(): Node? {
        val parent = this.parentNode as NodeImpl?
        return if (parent == null) null else parent.getPreviousTo(this)
    }

    override fun getNextSibling(): Node? {
        val parent = this.parentNode as NodeImpl?
        return if (parent == null) null else parent.getNextTo(this)
    }

    val previousElementSibling: Element?
        get() {
            val parent =
                this.parentNode as NodeImpl?
            if (parent != null) {
                var previous: Node? = this
                do {
                    previous = parent.getPreviousTo(previous)
                    if ((previous != null) && (previous is Element)) {
                        return previous
                    }
                } while (previous != null)
                return null
            } else {
                return null
            }
        }

    val nextElementSibling: Element?
        get() {
            val parent =
                this.parentNode as NodeImpl?
            if (parent != null) {
                var next: Node? = this
                do {
                    next = parent.getNextTo(next)
                    if ((next != null) && (next is Element)) {
                        return next
                    }
                } while (next != null)
                return null
            } else {
                return null
            }
        }

    override fun getFeature(feature: String?, version: String?): Any? {
        // TODO What should this do?
        return null
    }

    override fun setUserData(key: String, data: Any?, handler: UserDataHandler?): Any? {
        if (HtmlParser.MODIFYING_KEY == key) {
            val ns = (Boolean.TRUE === data)
            this.notificationsSuspended = ns
            if (!ns) {
                this.informNodeLoaded()
            }
        }
        // here we spent some effort preventing our maps from growing too much
        synchronized(this) {
            if (handler != null) {
                this.userDataHandlers.remove(key)
                this.userDataHandlers.put(key, handler)
            }
            var userData = this.userData
            if (data != null) {
                if (userData == null) {
                    userData = HashMap<String?, Any?>()
                    this.userData = userData
                }
                return userData.put(key, data)
            } else if (userData != null) {
                return userData.remove(key)
            } else {
                return null
            }
        }
    }

    override fun getUserData(key: String?): Any? {
        synchronized(this) {
            val ud = this.userData
            return if (ud == null) null else ud.get(key)
        }
    }

    abstract override fun getLocalName(): String?

    override fun hasAttributes(): kotlin.Boolean {
        return false
    }

    override fun getNamespaceURI(): String? {
        return null
    }

    abstract override fun getNodeName(): String

    @Throws(DOMException::class)
    abstract override fun getNodeValue(): String?

    @Throws(DOMException::class)
    abstract override fun setNodeValue(nodeValue: String?)

    override fun getPrefix(): String? {
        return this.prefix
    }

    @Throws(DOMException::class)
    override fun setPrefix(prefix: String?) {
        this.prefix = prefix
    }

    abstract override fun getNodeType(): Short

    /**
     * Gets the text content of this node and its descendents.
     */
    @Throws(DOMException::class)
    override fun getTextContent(): String {
        val sb = StringBuffer()
        synchronized(this.treeLock) {
            val nl = this.nodeList
            if (nl != null) {
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
        }
        return sb.toString()
    }

    @Throws(DOMException::class)
    override fun setTextContent(textContent: String?) {
        synchronized(this.treeLock) {
            this.removeChildrenImpl(TextFilter())
            if ((textContent != null) && "" != textContent) {
                val t = TextImpl(textContent)
                t.setOwnerDocument(this.document)
                t.setParentImpl(this)
                var nl = this.nodeList
                if (nl == null) {
                    nl = java.util.ArrayList<Node>()
                    this.nodeList = nl
                }
                nl.add(t)
            }
        }

        this.postChildListChanged()
    }

    protected fun removeChildren(filter: NodeFilter) {
        synchronized(this.treeLock) {
            this.removeChildrenImpl(filter)
        }

        this.postChildListChanged()
    }

    protected fun removeChildrenImpl(filter: NodeFilter) {
        val nl = this.nodeList
        if (nl != null) {
            val len = nl.size
            var i = len
            while (--i >= 0) {
                val node: Node = nl.get(i)
                if (filter.accept(node)) {
                    val n: Node? = nl.removeAt(i)
                    if (n is NodeImpl) {
                        n.handleDeletedFromParent()
                    }
                }
            }
        }
    }

    @HideFromJS
    fun insertAfter(newChild: Node?, refChild: Node?): Node? {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            val idx = if (nl == null) -1 else nl.indexOf(refChild)
            if (idx == -1) {
                throw DOMException(DOMException.NOT_FOUND_ERR, "refChild not found")
            }
            nl!!.add(idx + 1, newChild!!)
            if (newChild is NodeImpl) {
                newChild.handleAddedToParent(this)
            }
        }

        this.postChildListChanged()

        return newChild
    }

    @HideFromJS
    fun replaceAdjacentTextNodes(node: Text, textContent: String?): Text {
        try {
            synchronized(this.treeLock) {
                val nl = this.nodeList
                if (nl == null) {
                    throw DOMException(DOMException.NOT_FOUND_ERR, "Node not a child")
                }
                val idx = nl.indexOf(node)
                if (idx == -1) {
                    throw DOMException(DOMException.NOT_FOUND_ERR, "Node not a child")
                }
                var firstIdx = idx
                val toDelete: MutableList<Any?> = LinkedList<Any?>()
                run {
                    var adjIdx = idx
                    while (--adjIdx >= 0) {
                        val child: Any? = this.nodeList!![adjIdx]
                        if (child is Text) {
                            firstIdx = adjIdx
                            toDelete.add(child)
                        }
                    }
                }
                val length = this.nodeList!!.size
                var adjIdx = idx
                while (++adjIdx < length) {
                    val child: Any? = this.nodeList!!.get(adjIdx)
                    if (child is Text) {
                        toDelete.add(child)
                    }
                }
                this.nodeList!!.removeAll(toDelete)
                val textNode = TextImpl(textContent!!)
                textNode.setOwnerDocument(this.document)
                textNode.setParentImpl(this)
                this.nodeList!!.add(firstIdx, textNode)
                return textNode
            }
        } finally {
            this.postChildListChanged()
        }
    }

    @HideFromJS
    fun replaceAdjacentTextNodes(node: Text): Text {
        try {
            synchronized(this.treeLock) {
                val nl = this.nodeList
                if (nl == null) {
                    throw DOMException(DOMException.NOT_FOUND_ERR, "Node not a child")
                }
                val idx = nl.indexOf(node)
                if (idx == -1) {
                    throw DOMException(DOMException.NOT_FOUND_ERR, "Node not a child")
                }
                val textBuffer = StringBuffer()
                var firstIdx = idx
                val toDelete: MutableList<Any?> = LinkedList<Any?>()
                run {
                    var adjIdx = idx
                    while (--adjIdx >= 0) {
                        val child: Any? = this.nodeList!![adjIdx]
                        if (child is Text) {
                            firstIdx = adjIdx
                            toDelete.add(child)
                            textBuffer.append(child.nodeValue)
                        }
                    }
                }
                val length = this.nodeList!!.size
                var adjIdx = idx
                while (++adjIdx < length) {
                    val child: Any? = this.nodeList!!.get(adjIdx)
                    if (child is Text) {
                        toDelete.add(child)
                        textBuffer.append(child.nodeValue)
                    }
                }
                this.nodeList!!.removeAll(toDelete)
                val textNode = TextImpl(textBuffer.toString())
                textNode.setOwnerDocument(this.document)
                textNode.setParentImpl(this)
                this.nodeList!!.add(firstIdx, textNode)
                return textNode
            }
        } finally {
            this.postChildListChanged()
        }
    }

    override fun getParentNode(): Node? {
        // Should it be synchronized? Could have side-effects.
        return this.parentNode
    }

    override fun isSameNode(other: Node?): kotlin.Boolean {
        return this === other
    }

    override fun isSupported(feature: String?, version: String): kotlin.Boolean {
        return ("HTML" == feature && (version.compareTo("4.01") <= 0))
    }

    override fun lookupNamespaceURI(prefix: String?): String? {
        return null
    }

    open fun equalAttributes(arg: Node?): kotlin.Boolean {
        return false
    }

    override fun isEqualNode(arg: Node?): kotlin.Boolean {
        return (arg is NodeImpl) && (this.getNodeType() == arg.getNodeType()) && this.getNodeName() == arg.getNodeName()
                && this.getNodeValue() == arg.getNodeValue() && this.getLocalName() == arg.getLocalName()
                && this.nodeList == arg.nodeList && this.equalAttributes(arg)
    }

    override fun isDefaultNamespace(namespaceURI: String?): kotlin.Boolean {
        return namespaceURI == null
    }

    override fun lookupPrefix(namespaceURI: String?): String? {
        return null
    }

    override fun normalize() {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            if (nl != null) {
                var i = nl.iterator()
                val textNodes: MutableList<Node> = LinkedList<Node>()
                var prevText = false
                while (i.hasNext()) {
                    val child = i.next()
                    if (child.nodeType == Node.TEXT_NODE) {
                        if (!prevText) {
                            prevText = true
                            textNodes.add(child)
                        }
                    } else {
                        prevText = false
                    }
                }
                i = textNodes.iterator()
                while (i.hasNext()) {
                    val text = i.next() as Text
                    this.replaceAdjacentTextNodes(text)
                }
            }
        }
        this.postChildListChanged()
    }

    // ----- ModelNode implementation
    override fun toString(): String {
        return this.getNodeName()
    }

    open val userAgentContext: UserAgentContext?
        get() {
            val doc: Any? = this.document
            if (doc is HTMLDocumentImpl) {
                return doc.getUserAgentContext()
            } else {
                return null
            }
        }

    open val htmlRendererContext: HtmlRendererContext?
        get() {
            val doc: Any? = this.document
            if (doc is HTMLDocumentImpl) {
                return doc.getHtmlRendererContext()
            } else {
                return null
            }
        }

    fun setParentImpl(parent: Node?) {
        // Call holding treeLock.
        this.parentNode = parent
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.RenderableContext#getFullURL(java.lang.String)
     */

    override fun getFullURL(spec: String): URL {
        val doc: Any? = this.document
        val cleanSpec = Urls.encodeIllegalCharacters(spec)
        if (doc is HTMLDocumentImpl) {
            return doc.getFullURL(cleanSpec)
        } else {
            return URL(cleanSpec)
        }
    }

    open val documentURL: URL?
        get() {
            val doc: Any? = this.document
            if (doc is HTMLDocumentImpl) {
                return doc.getDocumentURL()
            } else {
                return null
            }
        }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.RenderableContext#getDocumentItem(java.lang.String
     * )
     */
    override fun getDocumentItem(name: String?): Any? {
        val document = this.document
        return if (document == null) null else document.getUserData(name)
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.RenderableContext#setDocumentItem(java.lang.String
     * , java.lang.Object)
     */
    override fun setDocumentItem(name: String?, value: Any?) {
        val document = this.document
        if (document == null) {
            return
        }
        document.setUserData(name, value, null)
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.RenderableContext#isEqualOrDescendentOf(org.xamjwg
     * .html.renderer.RenderableContext)
     */
    override fun isEqualOrDescendentOf(otherContext: ModelNode?): kotlin.Boolean {
        if (otherContext === this) {
            return true
        }
        val parent: Any? = this.parentNode
        if (parent is HTMLElementImpl) {
            return parent.isEqualOrDescendentOf(otherContext)
        } else {
            return false
        }
    }

    fun getParentModelNode(): ModelNode? {
        return this.parentNode as ModelNode?
    }

    override fun warn(message: String?, err: Throwable?) {
        logger.log(Level.WARNING, message, err)
    }

    open fun warn(message: String?) {
        logger.log(Level.WARNING, message)
    }

    fun informSizeInvalid() {
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            doc.sizeInvalidated(this)
        }
    }

    fun informLookInvalid() {
        this.forgetRenderState()
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            doc.lookInvalidated(this)
        }
    }

    fun informPositionInvalid() {
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            doc.positionInParentInvalidated(this)
        }
    }

    open fun informInvalid() {
        // This is called when an attribute or child changes.
        this.forgetRenderState()
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            doc.invalidated(this)
        }
    }

    fun informStructureInvalid() {
        // This is called when an attribute or child changes.
        this.forgetRenderState()
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            doc.structureInvalidated(this)
        }
    }

    protected fun informNodeLoaded() {
        // This is called when an attribute or child changes.
        this.forgetRenderState()
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            doc.nodeLoaded(this)
        }
    }

    protected fun informExternalScriptLoading() {
        // This is called when an attribute or child changes.
        this.forgetRenderState()
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            doc.externalScriptLoading(this)
        }
    }

    fun informLayoutInvalid() {
        // This is called by the style properties object.
        this.forgetRenderState()
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            doc.invalidated(this)
        }
    }

    fun informDocumentInvalid() {
        // This is called when an attribute or child changes.
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            doc.allInvalidated(true)
        }
    }

    fun getRenderState(): RenderState {
        // Generally called from the GUI thread, except for
        // offset properties.
        synchronized(this.treeLock) {
            var rs: RenderState? = this.renderState
            rs = this.renderState
            if (rs != null) {
                return rs
            }
            val parent: Any? = this.parentNode
            if ((parent != null) || (this is Document)) {
                val prs: RenderState? = getParentRenderState(parent)
                rs = this.createRenderState(prs)
                this.renderState = rs
                return rs
            } else {
                // Scenario is possible due to Javascript.
                return BLANK_RENDER_STATE
            }
        }
    }

    // abstract protected RenderState createRenderState(final RenderState prevRenderState);
    protected open fun createRenderState(prevRenderState: RenderState?): RenderState {
        if (prevRenderState == null) {
            return BLANK_RENDER_STATE
        } else {
            return prevRenderState
        }
    }

    protected fun forgetRenderState() {
        synchronized(this.treeLock) {
            if (this.renderState != null) {
                this.renderState = null
                // Note that getRenderState() "validates"
                // ancestor states as well.
                val nl = this.nodeList
                if (nl != null) {
                    val i: MutableIterator<Node?> = nl.iterator()
                    while (i.hasNext()) {
                        (i.next() as NodeImpl).forgetRenderState()
                    }
                }
            }
        }
    }

    val innerHTML: String
        get() {
            val buffer = StringBuffer()
            synchronized(this) {
                this.appendInnerHTMLImpl(buffer)
            }
            return buffer.toString()
        }

    protected fun appendInnerHTMLImpl(buffer: StringBuffer) {
        val nl = this.nodeList
        var size: Int = 0
        if ((nl != null) && ((nl.size.also { size = it }) > 0)) {
            for (i in 0..<size) {
                val child: Node? = nl[i]
                if (child is HTMLElementImpl) {
                    child.appendOuterHTMLImpl(buffer)
                } else if (child is Comment) {
                    buffer.append("<!--" + child.textContent + "-->")
                } else if (child is Text) {
                    val text = child.textContent
                    val encText = this.htmlEncodeChildText(text)
                    buffer.append(encText)
                } else if (child is ProcessingInstruction) {
                    buffer.append(child)
                }
            }
        }
    }

    protected open fun htmlEncodeChildText(text: String): String? {
        return Strings.strictHtmlEncode(text, false)
    }

    /*
  protected void dispatchEventToHandlers(final Event event, final List<Function> handlers) {
    if (handlers != null) {
      // We clone the collection and check if original collection still contains
      // the handler before dispatching
      // This is to avoid ConcurrentModificationException during dispatch
      // TODO: Event Bubbling
      final ArrayList<Function> handlersCopy = new ArrayList<>(handlers);
      for (final Function h : handlersCopy) {
        if (handlers.contains(h)) {
          Executor.executeFunction(this, h, event);
        }
      }
    }
  }

  private final Map<String, List<Function>> onEventHandlers = new HashMap<>();

  public void addEventListener(final String type, final Function listener) {
    addEventListener(type, listener, false);
  }

  public void addEventListener(final String type, final Function listener, final boolean useCapture) {
    // TODO
    System.out.println("node by name: " + getNodeName() + " adding Event listener of type: " + type);

    List<Function> handlerList = null;
    if (onEventHandlers.containsKey(type)) {
      handlerList = onEventHandlers.get(type);
    } else {
      handlerList = new ArrayList<>();
      onEventHandlers.put(type, handlerList);
    }
    handlerList.add(listener);
  }

  public void removeEventListener(final String type, final Function listener, final boolean useCapture) {
    // TODO
    System.out.println("node remove Event listener: " + type);
    if (onEventHandlers.containsKey(type)) {
      onEventHandlers.get(type).remove(listener);
    }
  }

  public boolean dispatchEvent(final Event evt) {
    System.out.println("Dispatching event: " + evt);
    dispatchEventToHandlers(evt, onEventHandlers.get(evt.getType()));
    return false;
  }*/
    val innerText: String
        /**
         * Attempts to convert the subtree starting at this point to a close text
         * representation. BR elements are converted to line breaks, and so forth.
         */
        get() {
            val buffer = StringBuffer()
            synchronized(this.treeLock) {
                this.appendInnerTextImpl(buffer)
            }
            return buffer.toString()
        }

    protected open fun appendInnerTextImpl(buffer: StringBuffer) {
        val nl = this.nodeList
        if (nl == null) {
            return
        }
        val size = nl.size
        if (size == 0) {
            return
        }
        for (i in 0..<size) {
            val child: Node? = nl.get(i)
            if (child is ElementImpl) {
                child.appendInnerTextImpl(buffer)
            }
            if (child is Comment) {
                // skip
            } else if (child is Text) {
                buffer.append(child.textContent)
            }
        }
    }

    /**
     * This method is intended to be overriden by subclasses that are interested
     * in processing their child-list whenever it is updated.
     */
    protected open fun handleChildListChanged() {
    }

    /**
     * This method is intended to be overriden by subclasses that are interested
     * in performing some operation when they are attached/detached from the
     * document.
     */
    protected open fun handleDocumentAttachmentChanged() {
    }

    /**
     * This method will be called on a node whenever it is being appended to a
     * parent node.
     *
     *
     * NOTE: changeDocumentAttachment will call updateIds() which needs to be tree
     * locked, and hence these methods are also being tree locked
     */
    private fun handleAddedToParent(parent: NodeImpl) {
        this.setParentImpl(parent)
        changeDocumentAttachment(parent.isAttachedToDocument)
    }

    /**
     * This method will be called on a node whenever it is being deleted from a
     * parent node.
     *
     *
     * NOTE: changeDocumentAttachment will call updateIds() which needs to be tree
     * locked, and hence these methods are also being tree locked
     */
    private fun handleDeletedFromParent() {
        this.setParentImpl(null)
        changeDocumentAttachment(false)
    }

    /**
     * This method will change the attachment of a node with the document. It will
     * also change the attachment of all its descendant nodes.
     *
     * @param attached the attachment with the document. true when attached, false
     * otherwise.
     */
    private fun changeDocumentAttachment(attached: kotlin.Boolean) {
        if (this.isAttachedToDocument != attached) {
            this.isAttachedToDocument = attached
            handleDocumentAttachmentChanged()
            if (this is ElementImpl) {
                this.updateIdMap(attached)
            }
        }
        if (nodeList != null) {
            for (node in this.nodeList) {
                if (node is NodeImpl) {
                    node.changeDocumentAttachment(attached)
                }
            }
        }
    }

    /*
  public void addEventListener(final String type, final EventListener listener) {
    addEventListener(type, listener, false);
  }

  public void addEventListener(final String type, final EventListener listener, final boolean useCapture) {
    if (useCapture) {
      throw new UnSupportedOperationException();
    }
  }

  public void removeEventListener(final String type, final EventListener listener, final boolean useCapture) {
    // TODO Auto-generated method stub

  }

  public boolean dispatchEvent(final org.w3c.dom.events.Event evt) throws EventException {
    // TODO Auto-generated method stub
    return false;
  }*/
    /**
     * Common tasks to be performed when the NodeList of an element is changed.
     */
    private fun postChildListChanged() {
        this.handleChildListChanged()

        if (!this.notificationsSuspended) {
            this.informStructureInvalid()
        }
    }

    @JvmOverloads
    fun addEventListener(type: String?, listener: Function?, useCapture: kotlin.Boolean = false) {
        // TODO
        println("node by name: " + getNodeName() + " adding Event listener of type: " + type)
        // System.out.println("  txt content: " + getInnerText());
        (ownerDocument as HTMLDocumentImpl).eventTargetManager?.addEventListener(
            this,
            type,
            listener
        )
    }

    fun removeEventListener(type: String?, listener: Function?, useCapture: kotlin.Boolean) {
        // TODO
        println("node remove Event listener: " + type)
        (ownerDocument as HTMLDocumentImpl).eventTargetManager?.removeEventListener(
            this,
            type,
            listener,
            useCapture
        )
    }

    /*
  public void addEventListener(final String type, final EventListener listener) {
    addEventListener(type, listener, false);
  }

  public void addEventListener(final String type, final EventListener listener, final boolean useCapture) {
    if (useCapture) {
      throw new UnSupportedOperationException();
    }
  }

  public void removeEventListener(final String type, final EventListener listener, final boolean useCapture) {
    // TODO Auto-generated method stub

  }

  public boolean dispatchEvent(final org.w3c.dom.events.Event evt) throws EventException {
    // TODO Auto-generated method stub
    return false;
  }*/
    fun dispatchEvent(evt: Event): kotlin.Boolean {
        println("Dispatching event: " + evt)
        // dispatchEventToHandlers(evt, onEventHandlers.get(evt.getType()));
        (ownerDocument as HTMLDocumentImpl).eventTargetManager?.dispatchEvent(this, evt)
        return false
    }

    fun querySelector(query: String?): Element? {
        // TODO: Optimize: Avoid getting all matches. Only first match is sufficient.
        val matchingElements = querySelectorAll(query)
        if (matchingElements.length > 0) {
            return matchingElements.item(0) as Element?
        } else {
            return null
        }
    }

    /*
  protected Collection<Node> getMatchingChildren(CombinedSelector selectors) {
    final Collection<Node> matchingElements = new LinkedList<>();
    final NodeImpl[] childrenArray = getChildrenArray();
    if (childrenArray != null) {
      for (final NodeImpl n : childrenArray) {
        if (n instanceof ElementImpl) {
          final ElementImpl element = (ElementImpl) n;
          if (selectors.stream().anyMatch(selector -> selector.matches(element))) {
            System.out.println("Found match: " + element + " of class: " + element.getClass());
            matchingElements.add(element);
          }
          matchingElements.addAll(element.getMatchingChildren(selectors));
        }
      }
    }
    return matchingElements;
  }*/
    protected fun getMatchingChildren(selectors: MutableList<Selector>): MutableCollection<Node> {
        val matchingElements: MutableCollection<Node> = LinkedList<Node>()
        val numSelectors = selectors.size
        if (numSelectors > 0) {
            val firstSelector = selectors.get(0)
            val childrenArray =
                this.childrenArray
            if (childrenArray != null) {
                for (n in childrenArray) {
                    if (n is ElementImpl) {
                        if (firstSelector.matches(n)) {
                            if (numSelectors > 1) {
                                val tailSelectors = selectors.subList(1, numSelectors)
                                matchingElements.addAll(n.getMatchingChildren(tailSelectors))
                            } else {
                                matchingElements.add(n)
                            }
                        }
                        matchingElements.addAll(n.getMatchingChildren(selectors))
                    }
                }
            }
        }
        return matchingElements
    }

    fun querySelectorAll(query: String?): NodeList {
        try {
            val selectors: Array<CombinedSelector> = makeSelectors(query)
            val matches = LinkedList<Node>()
            for (selector in selectors) {
                matches.addAll(getMatchingChildren(selector))
            }
            return NodeListImpl(matches)
        } catch (e: IOException) {
            e.printStackTrace()
            throw DOMException(DOMException.SYNTAX_ERR, "Couldn't parse selector: " + query)
        } catch (e: CSSException) {
            e.printStackTrace()
            throw DOMException(DOMException.SYNTAX_ERR, "Couldn't parse selector: " + query)
        }
    }

    fun getElementsByClassName(classNames: String): NodeList {
        val classNamesArray: Array<String?> =
            classNames.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        // TODO: escape commas in class-names
        val query = Arrays.stream<String?>(classNamesArray)
            .filter { cn: String? -> cn!!.length > 0 }
            .map<String?> { cn: String? -> "." + cn }
            .collect(Collectors.joining(","))
        return querySelectorAll(query)
    }

    open fun getElementsByTagName(classNames: String): NodeList {
        val classNamesArray: Array<String?> =
            classNames.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        // TODO: escape commas in class-names
        val query = Arrays.stream<String?>(classNamesArray).collect(Collectors.joining(","))
        return querySelectorAll(query)
    }

    val nameSpaceURI: String?
        // TODO: This is a plug
        get() {
            val nodeType = getNodeType()
            if (nodeType == Node.ELEMENT_NODE || nodeType == Node.ATTRIBUTE_NODE) {
                return "http://www.w3.org/1999/xhtml"
            } else {
                return null
            }
        }

    companion object {
        @JvmStatic
        protected val logger: Logger = Logger.getLogger(NodeImpl::class.java.name)
        private val EMPTY_ARRAY = arrayOfNulls<NodeImpl>(0)
        private val BLANK_RENDER_STATE: RenderState = StyleSheetRenderState(null)
        private fun getParentRenderState(parent: Any?): RenderState? {
            if (parent is NodeImpl) {
                return parent.getRenderState()
            } else {
                return null
            }
        }

        @Throws(IOException::class, CSSException::class)
        private fun makeSelectors(query: String?): Array<CombinedSelector> {
            // this is quick way to parse the selectors. TODO: check if jStyleParser supports a better option.
            val tempBlock = query + " { display: none}"
            val styleSheet = CSSFactory.parseString(tempBlock, null)
            val firstRuleBlock = styleSheet.get(0) as RuleSet
            val selectors = firstRuleBlock.selectors
            return selectors
        }
    }
}
