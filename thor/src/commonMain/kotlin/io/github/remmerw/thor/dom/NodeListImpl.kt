package io.github.remmerw.thor.dom

import org.w3c.dom.Node
import org.w3c.dom.NodeList

class NodeListImpl(val list: List<Node>) : NodeList {

    override fun getLength(): Int {
        return this.list.size
    }

    override fun item(index: Int): Node? {
        return this.list[index]
    }

    override fun toString(): String {
        return list.toString()
    }
}
