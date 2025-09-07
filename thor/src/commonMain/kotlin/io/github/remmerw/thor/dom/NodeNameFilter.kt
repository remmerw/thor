package io.github.remmerw.thor.dom


class NodeNameFilter(private val elementName: String) : NodeFilter {
    override fun accept(node: Node): Boolean {
        return this.elementName.equals(node.getNodeName(), ignoreCase = true)
    }
}
