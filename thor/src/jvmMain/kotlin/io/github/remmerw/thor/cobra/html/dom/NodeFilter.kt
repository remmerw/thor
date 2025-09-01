/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Oct 8, 2005
 */
package io.github.remmerw.thor.cobra.html.dom

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.html.HTMLFrameElement
import org.w3c.dom.html.HTMLIFrameElement
import org.w3c.dom.html.HTMLLinkElement

interface NodeFilter {
    fun accept(node: Node): Boolean

    class ImageFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return "IMG".equals(node.nodeName, ignoreCase = true)
        }
    }

    class AppletFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            // TODO: "OBJECT" elements that are applets too.
            return "APPLET".equals(node.nodeName, ignoreCase = true)
        }
    }

    class LinkFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return node is HTMLLinkElement
        }
    }

    class AnchorFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            val nodeName = node.nodeName
            return "A".equals(nodeName, ignoreCase = true) || "ANCHOR".equals(
                nodeName,
                ignoreCase = true
            )
        }
    }

    class FormFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            val nodeName = node.nodeName
            return "FORM".equals(nodeName, ignoreCase = true)
        }
    }

    class FrameFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return (node is HTMLFrameElement) || (node is HTMLIFrameElement)
        }
    }

    // private class BodyFilter implements NodeFilter {
    // public boolean accept(Node node) {
    // return node instanceof org.w3c.dom.html2.HTMLBodyElement;
    // }
    // }
    class ElementNameFilter(private val name: String) : NodeFilter {
        override fun accept(node: Node): Boolean {
            // TODO: Case sensitive?
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
