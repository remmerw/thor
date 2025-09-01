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
 * Created on Dec 4, 2005
 */
package io.github.remmerw.thor.cobra.html.dom

import org.w3c.dom.DOMException
import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLTableCellElement
import org.w3c.dom.html.HTMLTableRowElement

class HTMLTableRowElementImpl(name: String) : HTMLElementImpl(name), HTMLTableRowElement {


    override fun getRowIndex(): Int {
        val parent = this.nodeParent as NodeImpl?
        if (parent == null) {
            return -1
        }
        try {
            parent.visit(object : NodeVisitor {
                private var count = 0

                override fun visit(node: Node) {
                    if (node is HTMLTableRowElementImpl) {
                        if (this@HTMLTableRowElementImpl === node) {
                            throw StopVisitorException(this.count)
                        }
                        this.count++
                    }
                }
            })
        } catch (sve: StopVisitorException) {
            return (sve.tag as Int)
        }
        return -1
    }

    override fun getSectionRowIndex(): Int {
        // TODO Auto-generated method stub
        return 0
    }

    override fun getCells(): HTMLCollection {
        val filter: NodeFilter = object : NodeFilter {
            override fun accept(node: Node): Boolean {
                return node is HTMLTableCellElementImpl
            }
        }
        return DescendantHTMLCollection(this, filter, this.treeLock, false)
    }

    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

    override fun getBgColor(): String? {
        return this.getAttribute("bgcolor")
    }

    override fun setBgColor(bgColor: String?) {
        this.setAttribute("bgcolor", bgColor)
    }

    override fun getCh(): String? {
        return this.getAttribute("ch")
    }

    override fun setCh(ch: String?) {
        this.setAttribute("ch", ch)
    }

    override fun getChOff(): String? {
        return this.getAttribute("choff")
    }

    override fun setChOff(chOff: String?) {
        this.setAttribute("choff", chOff)
    }

    override fun getVAlign(): String? {
        return this.getAttribute("valign")
    }

    override fun setVAlign(vAlign: String?) {
        this.setAttribute("valign", vAlign)
    }

    /**
     * Inserts a TH element at the specified index.
     *
     *
     * Note: This method is non-standard.
     *
     * @param index The cell index to insert at.
     * @return The element that was inserted.
     * @throws DOMException When the index is out of range.
     */
    @Throws(DOMException::class)
    fun insertHeader(index: Int): HTMLElement? {
        return this.insertCell(index, "TH")
    }

    @Throws(DOMException::class)
    override fun insertCell(index: Int): HTMLElement? {
        return this.insertCell(index, "TD")
    }

    @Throws(DOMException::class)
    private fun insertCell(index: Int, tagName: String?): HTMLElement? {
        val doc = this.document
        if (doc == null) {
            throw DOMException(DOMException.WRONG_DOCUMENT_ERR, "Orphan element")
        }
        val cellElement = doc.createElement(tagName) as HTMLElement?
        synchronized(this.treeLock) {
            if (index == -1) {
                this.appendChild(cellElement)
                return cellElement
            }
            val nl = this.nodeList
            if (nl != null) {
                val size = nl.size
                var trcount = 0
                for (i in 0..<size) {
                    val node = nl.get(i)
                    if (node is HTMLTableCellElement) {
                        if (trcount == index) {
                            this.insertAt(cellElement, i)
                            return cellElement
                        }
                        trcount++
                    }
                }
            } else {
                this.appendChild(cellElement)
                return cellElement
            }
        }
        throw DOMException(DOMException.INDEX_SIZE_ERR, "Index out of range")
    }

    @Throws(DOMException::class)
    override fun deleteCell(index: Int) {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            if (nl != null) {
                val size = nl.size
                var trcount = 0
                for (i in 0..<size) {
                    val node = nl.get(i)
                    if (node is HTMLTableCellElement) {
                        if (trcount == index) {
                            this.removeChildAt(index)
                        }
                        trcount++
                    }
                }
            }
        }
        throw DOMException(DOMException.INDEX_SIZE_ERR, "Index out of range")
    }
}
