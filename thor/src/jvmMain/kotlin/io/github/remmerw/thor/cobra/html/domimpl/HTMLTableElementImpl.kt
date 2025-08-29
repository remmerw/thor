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

import io.github.remmerw.thor.cobra.html.js.PropertyName
import io.github.remmerw.thor.cobra.html.style.HtmlLength
import io.github.remmerw.thor.cobra.html.style.HtmlValues
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.html.style.TableRenderState
import org.w3c.dom.DOMException
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLTableCaptionElement
import org.w3c.dom.html.HTMLTableElement
import org.w3c.dom.html.HTMLTableSectionElement

class HTMLTableElementImpl : HTMLAbstractUIElement, HTMLTableElement {
    private var caption: HTMLTableCaptionElement? = null
    private var thead: HTMLTableSectionElement? = null
    private var tfoot: HTMLTableSectionElement? = null

    constructor() : super("TABLE")

    constructor(name: String?) : super(name)

    override fun getCaption(): HTMLTableCaptionElement? {
        return this.caption
    }

    @Throws(DOMException::class)
    override fun setCaption(caption: HTMLTableCaptionElement?) {
        this.caption = caption
    }

    override fun getTHead(): HTMLTableSectionElement? {
        return this.thead
    }

    @Throws(DOMException::class)
    override fun setTHead(tHead: HTMLTableSectionElement?) {
        this.thead = tHead
    }

    override fun getTFoot(): HTMLTableSectionElement? {
        return this.tfoot
    }

    @Throws(DOMException::class)
    override fun setTFoot(tFoot: HTMLTableSectionElement?) {
        this.tfoot = tFoot
    }

    override fun getRows(): HTMLCollection {
        // TODO: filter by display: table-row
        return DescendentHTMLCollection(this, NodeNameFilter("TR"), this.treeLock, false)
    }

    @PropertyName("tBodies")
    override fun getTBodies(): HTMLCollection {
        // TODO: filter by display: table-row-group
        return DescendentHTMLCollection(this, NodeNameFilter("TBODY"), this.treeLock, false)
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

    override fun getBorder(): String? {
        return this.getAttribute("border")
    }

    override fun setBorder(border: String?) {
        this.setAttribute("border", border)
    }

    override fun getCellPadding(): String? {
        return this.getAttribute("cellpadding")
    }

    override fun setCellPadding(cellPadding: String?) {
        this.setAttribute("cellpadding", cellPadding)
    }

    override fun getCellSpacing(): String? {
        return this.getAttribute("cellspacing")
    }

    override fun setCellSpacing(cellSpacing: String?) {
        this.setAttribute("cellspacing", cellSpacing)
    }

    override fun getFrame(): String? {
        return this.getAttribute("frame")
    }

    override fun setFrame(frame: String?) {
        this.setAttribute("frame", frame)
    }

    override fun getRules(): String? {
        return this.getAttribute("rules")
    }

    override fun setRules(rules: String?) {
        this.setAttribute("rules", rules)
    }

    override fun getSummary(): String? {
        return this.getAttribute("summary")
    }

    override fun setSummary(summary: String?) {
        this.setAttribute("summary", summary)
    }

    override fun getWidth(): String? {
        return this.getAttribute("width")
    }

    override fun setWidth(width: String?) {
        this.setAttribute("width", width)
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.RenderableContext#getHeightLength()
     */
    fun getHeightLength(availHeight: Int): HtmlLength? {
        try {
            val props = this.getCurrentStyle()
            val heightText = props.height
            if (heightText == null) {
                // TODO: convert attributes to CSS properties
                return HtmlLength(
                    HtmlValues.getPixelSize(
                        this.getAttribute("height")!!,
                        this.getRenderState(),
                        0,
                        availHeight
                    )
                )
            } else {
                return HtmlLength(
                    HtmlValues.getPixelSize(
                        heightText,
                        this.getRenderState(),
                        0,
                        availHeight
                    )
                )
            }
        } catch (err: NumberFormatException) {
            println("Number format exception: " + err)
            return null
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.RenderableContext#getWidthLength()
     */
    fun getWidthLength(availWidth: Int): HtmlLength? {
        try {
            val props = this.getCurrentStyle()
            val widthText = props.width
            if (widthText == null) {
                // TODO: convert attributes to CSS properties
                return HtmlLength(
                    HtmlValues.getPixelSize(
                        this.getAttribute("width")!!,
                        this.getRenderState(),
                        0,
                        availWidth
                    )
                )
            } else {
                return HtmlLength(
                    HtmlValues.getPixelSize(
                        widthText,
                        this.getRenderState(),
                        0,
                        availWidth
                    )
                )
            }
        } catch (err: NumberFormatException) {
            println("Number format exception: " + err)
            return null
        }
    }

    override fun createTHead(): HTMLElement? {
        val doc = this.document
        return if (doc == null) null else doc.createElement("thead") as HTMLElement?
    }

    override fun deleteTHead() {
        this.removeChildren(NodeNameFilter("THEAD"))
    }

    override fun createTFoot(): HTMLElement? {
        val doc = this.document
        return if (doc == null) null else doc.createElement("tfoot") as HTMLElement?
    }

    override fun deleteTFoot() {
        this.removeChildren(NodeNameFilter("TFOOT"))
    }

    override fun createCaption(): HTMLElement? {
        val doc = this.document
        return if (doc == null) null else doc.createElement("caption") as HTMLElement?
    }

    override fun deleteCaption() {
        this.removeChildren(NodeNameFilter("CAPTION"))
    }

    /**
     * Inserts a row at the index given. If `index` is `-1`,
     * the row is appended as the last row.
     */
    @Throws(DOMException::class)
    override fun insertRow(index: Int): HTMLElement? {
        val doc = this.document
        if (doc == null) {
            throw DOMException(DOMException.WRONG_DOCUMENT_ERR, "Orphan element")
        }
        val rowElement = doc.createElement("TR") as HTMLElement?
        synchronized(this.treeLock) {
            if (index == -1) {
                this.appendChild(rowElement)
                return rowElement
            }
            val nl = this.nodeList
            if (nl != null) {
                val size = nl.size
                var trcount = 0
                for (i in 0..<size) {
                    val node = nl.get(i)
                    if ("TR".equals(node.nodeName, ignoreCase = true)) {
                        if (trcount == index) {
                            this.insertAt(rowElement, i)
                            return rowElement
                        }
                        trcount++
                    }
                }
            } else {
                this.appendChild(rowElement)
                return rowElement
            }
        }
        throw DOMException(DOMException.INDEX_SIZE_ERR, "Index out of range")
    }

    @Throws(DOMException::class)
    override fun deleteRow(index: Int) {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            if (nl != null) {
                val size = nl.size
                var trcount = 0
                for (i in 0..<size) {
                    val node = nl.get(i)
                    if ("TR".equals(node.nodeName, ignoreCase = true)) {
                        if (trcount == index) {
                            this.removeChildAt(i)
                            return
                        }
                        trcount++
                    }
                }
            }
        }
        throw DOMException(DOMException.INDEX_SIZE_ERR, "Index out of range")
    }

    override fun createRenderState(prevRenderState: RenderState?): RenderState {
        return TableRenderState(prevRenderState, this)
    }
}
