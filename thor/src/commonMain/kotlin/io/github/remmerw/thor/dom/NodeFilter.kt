package io.github.remmerw.thor.dom

interface NodeFilter {
    fun accept(node: Node): Boolean


    class ElementNameFilter(private val name: String) : NodeFilter {
        override fun accept(node: Node): Boolean {
            return (node is Element) && this.name == node.getAttribute("name")
        }
    }

    class ElementIdFilter(private val id: String) : NodeFilter {
        override fun accept(node: Node): Boolean {
            return (node is Element) && this.id == node.getAttribute("id")
        }
    }

    class ElementFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node is Element
        }
    }

    class TagNameFilter(private val name: String) : NodeFilter {
        override fun accept(node: Node): Boolean {
            if (node !is Element) {
                return false
            }
            val n = this.name
            return n.equals(node.getTagName(), ignoreCase = true)
        }
    }
}
