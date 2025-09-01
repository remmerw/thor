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

import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.html.style.TableCellRenderState
import org.w3c.dom.html.HTMLTableCellElement

open class HTMLTableCellElementImpl(name: String) : HTMLAbstractUIElement(name),
    HTMLTableCellElement {
    override fun getCellIndex(): Int {
        // TODO Cell index in row
        return 0
    }

    override fun getAbbr(): String? {
        return this.getAttribute("abbr")
    }

    override fun setAbbr(abbr: String?) {
        this.setAttribute("abbr", abbr)
    }

    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

    override fun getAxis(): String? {
        return this.getAttribute("axis")
    }

    override fun setAxis(axis: String?) {
        this.setAttribute("axis", axis)
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

    override fun getColSpan(): Int {
        val colSpanText = this.getAttribute("colspan")
        if (colSpanText == null) {
            return 1
        } else {
            try {
                return colSpanText.toInt()
            } catch (nfe: NumberFormatException) {
                return 1
            }
        }
    }

    override fun setColSpan(colSpan: Int) {
        this.setAttribute("colspan", colSpan.toString())
    }

    override fun getHeaders(): String? {
        return this.getAttribute("headers")
    }

    override fun setHeaders(headers: String?) {
        this.setAttribute("headers", headers)
    }

    override fun getHeight(): String? {
        return this.getAttribute("height")
    }

    override fun setHeight(height: String?) {
        this.setAttribute("height", height)
    }

    override fun getNoWrap(): Boolean {
        return "nowrap".equals(this.getAttribute("nowrap"), ignoreCase = true)
    }

    override fun setNoWrap(noWrap: Boolean) {
        this.setAttribute("nowrap", if (noWrap) "nowrap" else null)
    }

    override fun getRowSpan(): Int {
        val rowSpanText = this.getAttribute("rowspan")
        if (rowSpanText == null) {
            return 1
        } else {
            try {
                return rowSpanText.toInt()
            } catch (nfe: NumberFormatException) {
                return 1
            }
        }
    }

    override fun setRowSpan(rowSpan: Int) {
        this.setAttribute("rowspan", rowSpan.toString())
    }

    override fun getScope(): String? {
        return this.getAttribute("scope")
    }

    override fun setScope(scope: String?) {
        this.setAttribute("scope", scope)
    }

    override fun getVAlign(): String? {
        return this.getAttribute("valign")
    }

    override fun setVAlign(vAlign: String?) {
        this.setAttribute("valign", vAlign)
    }

    override fun getWidth(): String? {
        return this.getAttribute("width")
    }

    override fun setWidth(width: String?) {
        this.setAttribute("width", width)
    }

    override fun createRenderState(prevRenderState: RenderState?): RenderState {
        return TableCellRenderState(prevRenderState, this)
    }
}
