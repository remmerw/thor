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
 * Created on Sep 3, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.js.AbstractScriptableDelegate
import io.github.remmerw.thor.cobra.js.JavaScript
import io.github.remmerw.thor.cobra.util.Objects
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

// TODO: This needs to be live (dynamic) not a static store of nodes.
class NodeListImpl(collection: MutableCollection<Node>) : AbstractScriptableDelegate(), NodeList {
    // Note: class must be public for reflection to work.
    private val nodeList: ArrayList<Node> = ArrayList<Node>(collection)

    override fun getLength(): Int {
        return this.nodeList.size
    }

    override fun item(index: Int): Node? {
        try {
            return this.nodeList.get(index)
        } catch (iob: IndexOutOfBoundsException) {
            return null
        }
    }

    // TODO: This needs to be handled in a general fashion. GH #123
    fun hasOwnProperty(obj: Any?): Boolean {
        if (Objects.isAssignableOrBox(obj, Integer.TYPE)) {
            val i = JavaScript.instance.getJavaObject(obj, Integer.TYPE) as Int
            return i < length
        } else {
            return false
        }
    }

    /* Described here: http://www.w3.org/TR/dom/#dom-htmlcollection-nameditem. This actually needs to be in a separate class that implements HTMLCollection */
    fun namedItem(key: String): Node? {
        val length = getLength()
        for (i in 0..<length) {
            val n = item(0)
            if (n is Element) {
                if (key == n.getAttribute("id") || key == n.getAttribute("name")) {
                    return n
                }
            }
        }
        return null
    }

    override fun toString(): String {
        return nodeList.toString()
    }
}
