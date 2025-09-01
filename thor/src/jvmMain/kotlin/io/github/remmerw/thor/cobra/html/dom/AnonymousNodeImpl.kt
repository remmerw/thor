package io.github.remmerw.thor.cobra.html.dom

import io.github.remmerw.thor.cobra.util.NotImplementedYetException
import org.w3c.dom.DOMException
import org.w3c.dom.Node
import org.w3c.dom.Node.ELEMENT_NODE

class AnonymousNodeImpl(parentNode: Node?) : NodeImpl() {
    init {
        setParentImpl(parentNode)
    }

    override fun createSimilarNode(): Node {
        throw NotImplementedYetException()
    }

    override fun getLocalName(): String {
        return ""
    }

    override fun getNodeName(): String {
        return ""
    }

    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        return null
    }

    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
        // nop
    }

    override fun getNodeType(): Short {
        return ELEMENT_NODE
    }

    /**
     * Append child without informing the child of the new parent
     */

    fun appendChildSilently(c: NodeImpl) {
        synchronized(this.treeLock) {
            var nl = this.nodeList
            if (nl == null) {
                nl = ArrayList<Node>(3)
                this.nodeList = nl
            }
            nl.add(c)
        }
    }

    override fun toString(): String {
        return "Anonymous child of " + nodeParent
    }
}
