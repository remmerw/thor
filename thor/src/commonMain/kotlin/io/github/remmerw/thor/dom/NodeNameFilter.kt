package io.github.remmerw.thor.dom

import org.w3c.dom.Node

class NodeNameFilter(private val elementName: String) : NodeFilter {
    override fun accept(node: Node): Boolean {
        return this.elementName.equals(node.nodeName, ignoreCase = true)
    }
}
