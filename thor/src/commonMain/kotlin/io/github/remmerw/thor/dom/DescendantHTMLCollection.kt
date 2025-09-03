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
package io.github.remmerw.thor.dom

import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection

open class DescendantHTMLCollection @JvmOverloads constructor(
    private val rootNode: NodeImpl,
    private val nodeFilter: NodeFilter?,
    private val treeLock: Any,
    private val nestIntoMatchingNodes: Boolean = true
) : HTMLCollection {


    private var itemsByName: MutableMap<String?, ElementImpl?>? = null
    private var itemsByIndex: MutableList<NodeImpl?>? = null


    private fun ensurePopulatedImpl() {
        if (this.itemsByName == null) {
            val descendents =
                this.rootNode.getDescendants(this.nodeFilter!!, this.nestIntoMatchingNodes)
            this.itemsByIndex = if (descendents == null) mutableListOf<NodeImpl?>() else descendents
            val size = if (descendents == null) 0 else descendents.size
            val itemsByName: MutableMap<String?, ElementImpl?> = HashMap((size * 3) / 2)
            this.itemsByName = itemsByName
            for (i in 0..<size) {
                val descNode = descendents!!.get(i)
                if (descNode is ElementImpl) {
                    val id = descNode.getId()
                    if ((id != null) && (id.length != 0)) {
                        itemsByName.put(id, descNode)
                    }
                    val name = descNode.getAttribute("name")
                    if ((name != null) && (name.length != 0) && (name != id)) {
                        itemsByName.put(name, descNode)
                    }
                }
            }
        }
    }

    override fun getLength(): Int {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            return this.itemsByIndex!!.size
        }
    }

    override fun item(index: Int): Node? {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            return try {
                this.itemsByIndex!!.get(index)
            } catch (_: Throwable) {
                null
            }
        }
    }

    // TODO: This is a quick hack. Need to support WEB-IDL Semantics. GH #67
    fun item(obj: Any?): Node? {
        if (obj is Int) {
            return item(obj)
        }
        return item(0)
    }


    override fun namedItem(name: String?): Node? {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            return this.itemsByName!!.get(name)
        }
    }

    fun indexOf(node: Node?): Int {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            return this.itemsByIndex!!.indexOf(node)
        }
    }
}
