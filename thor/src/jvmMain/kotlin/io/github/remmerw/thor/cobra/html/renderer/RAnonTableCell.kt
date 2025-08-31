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
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.renderer.TableMatrix.ColSizeInfo
import io.github.remmerw.thor.cobra.html.renderer.TableMatrix.RowSizeInfo
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle

internal class RAnonTableCell(
    private val cellNode: NodeImpl, pcontext: UserAgentContext?, rcontext: HtmlRendererContext?,
    frameContext: FrameContext?,
    tableAsContainer: RenderableContainer?
) : RAbstractCell(cellNode, 0, pcontext, rcontext, frameContext, tableAsContainer) {
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
    override var rowSpan: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override val colSpan: Int
        get() = TODO("Not yet implemented")

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

            return Dimension(this.width(), this.height())
        } finally {
            this.layoutUpTreeCanBeInvalidated = true
            this.layoutDeepCanBeInvalidated = true
        }
    }

    fun clearLayoutCache() {
        // test method
        // this.cachedLayout.clear();
    }

    // public void setCellPadding(int value) {
    // this.cellPadding = value;
    // }
    override fun getDeclaredHeight(renderState: RenderState?, availHeight: Int): Int? {
        // Overridden since height declaration is handled by table.
        return null
    }

    override fun getDeclaredWidth(renderState: RenderState?, availWidth: Int): Int? {
        // Overridden since width declaration is handled by table.
        return null
    }


    fun getColSpan(): Int {
        return 1
    }

    fun getRowSpan(): Int {
        return 1
    }

    fun setRowSpan(rowSpan: Int) {
        throw IllegalStateException()
    }

    fun getHeightText(): String? {
        return null
    }

    fun getWidthText(): String? {
        return null
    }

    fun setCellBounds(
        colSizes: Array<ColSizeInfo>, rowSizes: Array<RowSizeInfo>, hasBorder: Int,
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
        return cellNode.getRenderState()
    }


    override var originalParent: RCollection?
        get() = TODO("Not yet implemented")
        set(value) {}
    override val originalOrCurrentParent: RCollection?
        get() = TODO("Not yet implemented")
    override val visualX: Int
        get() = TODO("Not yet implemented")
    override val visualY: Int
        get() = TODO("Not yet implemented")

    override val isContainedByNode: Boolean
        get() = TODO("Not yet implemented")


    override fun vAlign(): CSSProperty.VerticalAlign? {
        TODO("Not yet implemented")
    }
}
