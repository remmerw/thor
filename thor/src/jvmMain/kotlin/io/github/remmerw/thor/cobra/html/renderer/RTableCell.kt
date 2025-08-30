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
package io.github.remmerw.thor.cobra.html.renderer

import cz.vutbr.web.css.CSSProperty
import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.renderer.TableMatrix.ColSizeInfo
import io.github.remmerw.thor.cobra.html.renderer.TableMatrix.RowSizeInfo
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle

internal open class RTableCell(
    element: HTMLElementImpl, pcontext: UserAgentContext?, rcontext: HtmlRendererContext?,
    frameContext: FrameContext?,
    tableAsContainer: RenderableContainer?
) : RAbstractCell(element, 0, pcontext, rcontext, frameContext, tableAsContainer) {
    private val cellElement: HTMLElementImpl
    override var colSpan = -1
    override fun setCellBounds(
        colSizes: List<ColSizeInfo>,
        rowSizes: List<RowSizeInfo>,
        hasBorder: Int,
        cellSpacingX: Int,
        cellSpacingY: Int
    ) {
        TODO("Not yet implemented")
    }

    override val widthText: String?
        get() = TODO("Not yet implemented")
    override val heightText: String?
        get() = TODO("Not yet implemented")
    override var rowSpan = -1

    /**
     * @param element
     */
    init {
        this.cellElement = element
    }

    override fun doCellLayout(
        width: Int, height: Int, expandWidth: Boolean, expandHeight: Boolean,
        sizeOnly: Boolean
    ): Dimension {
        return this.doCellLayout(width, height, expandWidth, expandHeight, sizeOnly, true)
    }

    override val renderState: RenderState
        get() = TODO("Not yet implemented")

    /**
     * @param width    The width available, including insets.
     * @param height   The height available, including insets.
     * @param useCache Testing parameter. Should always be true.
     */
    protected fun doCellLayout(
        width: Int, height: Int, expandWidth: Boolean, expandHeight: Boolean,
        sizeOnly: Boolean, useCache: Boolean
    ): Dimension {
        try {
            /* TODO: This was being called along with the layout call. Investigate if the repeat calls serve some purpose.
      this.doLayout(width, height, expandWidth, expandHeight, null, RenderState.OVERFLOW_NONE, RenderState.OVERFLOW_NONE, sizeOnly, useCache);
      */
            this.layout(width, height, expandWidth, expandHeight, null, sizeOnly)
            return Dimension(this.width, this.height)
        } finally {
            this.layoutUpTreeCanBeInvalidated = true
            this.layoutDeepCanBeInvalidated = true
        }
    }

    fun clearLayoutCache() {
        // test method
        // this.cachedLayout.clear();
    }

    override fun getDeclaredHeight(renderState: RenderState?, availHeight: Int): Int? {
        // Overridden since height declaration is handled by table.
        return null
    }

    override fun getDeclaredWidth(renderState: RenderState?, availWidth: Int): Int? {
        // Overridden since width declaration is handled by table.
        return null
    }

    fun getColSpan(): Int {
        var cs = this.colSpan
        if (cs == -1) {
            cs = getColSpan(this.cellElement)
            if (cs < 1) {
                cs = 1
            }
            this.colSpan = cs
        }
        return cs
    }

    fun getRowSpan(): Int {
        var rs = this.rowSpan
        if (rs == -1) {
            rs = getRowSpan(this.cellElement)
            if (rs < 1) {
                rs = 1
            }
            this.rowSpan = rs
        }
        return rs
    }

    fun setRowSpan(rowSpan: Int) {
        this.rowSpan = rowSpan
    }

    fun getHeightText(): String? {
        return this.cellElement.getCurrentStyle().height
        // return this.cellElement.getHeight();
    }

    fun getWidthText(): String? {
        return this.cellElement.getCurrentStyle().width
        // return this.cellElement.getWidth();
    }

    // public Dimension layoutMinWidth() {
    //
    // return this.panel.layoutMinWidth();
    //
    // }
    //
    //
    fun setCellBounds(
        colSizes: Array<ColSizeInfo>,
        rowSizes: Array<RowSizeInfo>,
        hasBorder: Int,
        cellSpacingX: Int,
        cellSpacingY: Int
    ) {
        val vcol = this.virtualColumn
        val vrow = this.virtualRow
        val colSize = colSizes[vcol]
        val rowSize = rowSizes[vrow]
        val x = colSize.offsetX + rowSize.offsetX
        val y = rowSize.offsetY
        var width: Int
        var height: Int
        val colSpan = this.getColSpan()
        if (colSpan > 1) {
            width = 0
            for (i in 0..<colSpan) {
                val vc = vcol + i
                width += colSizes[vc].actualSize
                if ((i + 1) < colSpan) {
                    width += cellSpacingX + (hasBorder * 2)
                }
            }
        } else {
            width = colSizes[vcol].actualSize
        }
        val rowSpan = this.getRowSpan()
        if (rowSpan > 1) {
            height = 0
            for (i in 0..<rowSpan) {
                val vr = vrow + i
                height += rowSizes[vr].actualSize
                if ((i + 1) < rowSpan) {
                    height += cellSpacingY + (hasBorder * 2)
                }
            }
        } else {
            height = rowSizes[vrow].actualSize
        }
        this.setBounds(x, y, width, height)
    }

    fun isMarginBoundary(): Boolean {
        return true
    }

    fun getRenderState(): RenderState {
        return cellElement.getRenderState()
    }


    override val visualBounds: Rectangle?
        get() = TODO("Not yet implemented")

    override var parent: RCollection?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var originalParent: RCollection?
        get() = TODO("Not yet implemented")
        set(value) {}
    override val originalOrCurrentParent: RCollection?
        get() = TODO("Not yet implemented")
    override val visualX: Int
        get() = TODO("Not yet implemented")
    override val visualY: Int
        get() = TODO("Not yet implemented")
    override val visualHeight: Int
        get() = TODO("Not yet implemented")
    override val visualWidth: Int
        get() = TODO("Not yet implemented")
    override val isContainedByNode: Boolean
        get() = TODO("Not yet implemented")



    override val parentContainer: RenderableContainer?
        get() = TODO("Not yet implemented")


    override fun vAlign(): CSSProperty.VerticalAlign? {
        TODO("Not yet implemented")
    }

    companion object {
        private fun getColSpan(elem: HTMLElementImpl): Int {
            val colSpanText = elem.getAttribute("colspan")
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

        // public void setCellPadding(int value) {
        // this.cellPadding = value;
        // }
        private fun getRowSpan(elem: HTMLElementImpl): Int {
            val rowSpanText = elem.getAttribute("rowspan")
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
    }
}
