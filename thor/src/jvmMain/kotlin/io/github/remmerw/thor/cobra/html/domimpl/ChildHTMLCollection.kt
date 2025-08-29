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
 * Created on Dec 3, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.js.ScriptableDelegate
import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection

class ChildHTMLCollection
/**
 * @param rootNode
 */(private val rootNode: NodeImpl) : ScriptableDelegate(), HTMLCollection {
    override fun getLength(): Int {
        return this.rootNode.childCount
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

    fun indexOf(node: Node?): Int {
        return this.rootNode.getChildIndex(node)
    }
}
