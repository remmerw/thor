package io.github.remmerw.thor.dom

import org.w3c.dom.Element
import org.w3c.dom.Node

interface NodeFilter {
    fun accept(node: Node): Boolean

    class ImageFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node.nodeName == ElementType.IMG.name
        }
    }

    class AppletFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node.nodeName == ElementType.APPLET.name
        }
    }

    class LinkFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node.nodeName == ElementType.LINK.name
        }
    }

    class AnchorFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node.nodeName == ElementType.ANCHOR.name || node.nodeName == ElementType.A.name
        }
    }

    class FormFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node.nodeName == ElementType.FORM.name
        }
    }


    class ElementNameFilter(private val name: String) : NodeFilter {
        override fun accept(node: Node): Boolean {
            return (node is Element) && this.name == node.getAttribute("name")
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
            return n.equals(node.tagName, ignoreCase = true)
        }
    }
}
