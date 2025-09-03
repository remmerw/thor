package io.github.remmerw.thor.dom

import org.w3c.dom.Element
import org.w3c.dom.Node

interface NodeFilter {
    fun accept(node: Node): Boolean

    class ImageFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return "IMG".equals(node.nodeName, ignoreCase = true)
        }
    }

    class AppletFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return "APPLET".equals(node.nodeName, ignoreCase = true)
        }
    }

    class LinkFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node is HTMLLinkElementModel
        }
    }

    class AnchorFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node is HTMLAnchorElementModel
        }
    }

    class FormFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node is HTMLFormElementModel
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
