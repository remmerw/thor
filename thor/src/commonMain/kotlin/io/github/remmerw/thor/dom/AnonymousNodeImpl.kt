package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.Node
import org.w3c.dom.Node.ELEMENT_NODE

class AnonymousNodeImpl(parentNode: Node?) : NodeImpl() {
    init {
        setParentImpl(parentNode)
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


    override fun toString(): String {
        return "Anonymous child of " + nodeParent
    }
}
