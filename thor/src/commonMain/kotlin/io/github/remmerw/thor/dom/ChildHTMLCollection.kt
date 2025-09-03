package io.github.remmerw.thor.dom

import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection

class ChildHTMLCollection(private val rootNode: NodeImpl) : HTMLCollection {


    override fun getLength(): Int {
        return this.rootNode.getChildCount()
    }

    override fun item(index: Int): Node? {
        return this.rootNode.getChildAtIndex(index)
    }

    override fun namedItem(name: String?): Node? {
        val doc = this.rootNode.ownerDocument
        if (doc == null) {
            return null
        }
        // TODO: This might get elements that are not descendents.
        val node: Node? = doc.getElementById(name)
        if ((node != null) && (node.parentNode === this.rootNode)) {
            return node
        }
        return null
    }

}
