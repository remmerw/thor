package io.github.remmerw.thor.dom

import io.github.remmerw.thor.model.Type
import org.w3c.dom.Element
import org.w3c.dom.Node

interface NodeFilter {
    fun accept(node: Node): Boolean

    class ImageFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node.nodeName == Type.IMG.name
        }
    }

    class AppletFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node.nodeName == Type.APPLET.name
        }
    }

    class LinkFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node.nodeName == Type.LINK.name
        }
    }

    class AnchorFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node.nodeName == Type.ANCHOR.name || node.nodeName == Type.A.name
        }
    }

    class FormFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node.nodeName == Type.FORM.name
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
