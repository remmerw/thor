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
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.AnonymousNodeImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.domimpl.TextImpl
import io.github.remmerw.thor.cobra.html.style.HtmlInsets
import io.github.remmerw.thor.cobra.html.style.HtmlLength
import io.github.remmerw.thor.cobra.html.style.HtmlValues
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.html.style.RenderThreadState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent
import kotlin.math.max

class TableMatrix(
    element: HTMLElementImpl, uaContext: UserAgentContext?, rcontext: HtmlRendererContext?,
    frameContext: FrameContext?,
    tableAsContainer: RenderableContainer?, relement: RElement?
) {
    private val ROWS = ArrayList<Row>()
    private val ROW_GROUPS: ArrayList<RowGroup> = ArrayList<RowGroup>()
    private val ALL_CELLS = ArrayList<RAbstractCell>()
    private val tableElement: HTMLElementImpl
    private val uaContext: UserAgentContext?
    private val rendererContext: HtmlRendererContext?
    private val frameContext: FrameContext?
    private val relement: RElement?
    private val container: RenderableContainer?

    private val columnSizes: MutableList<ColSizeInfo> = mutableListOf()
    private val rowSizes: MutableList<RowSizeInfo> = mutableListOf()

    /**
     * @return Returns the tableWidth.
     */
    var tableWidth: Int = 0
        private set

    /**
     * @return Returns the tableHeight.
     */
    var tableHeight: Int = 0
        private set

    /*
    * This is so that we can draw the lines inside the table that appear when a
    * border attribute is used.
    */
    private var hasOldStyleBorder = 0

    // private int border;
    private var cellSpacingY = 0
    private var cellSpacingX = 0
    private var widthsOfExtras = 0
    private var heightsOfExtras = 0
    private var tableWidthLength: HtmlLength? = null
    private var rowGroupSizes: ArrayList<RowGroupSizeInfo>? = null
    private var armedRenderable: BoundableRenderable? = null
    private var maxRowGroupLeft = 0
    private var maxRowGroupRight = 0

    /**
     * @param element
     */
    init {
        this.tableElement = element
        this.uaContext = uaContext
        this.rendererContext = rcontext
        this.frameContext = frameContext
        this.relement = relement
        this.container = tableAsContainer
    }

    val numRows: Int
        get() = this.ROWS.size

    val numColumns: Int
        get() = this.columnSizes.size

    /**
     * Called on every relayout. Element children might have changed.
     */
    fun reset(insets: Insets, availWidth: Int, availHeight: Int) {
        // TODO: Incorporate into build() and calculate
        // sizes properly based on parameters.
        ROW_GROUPS.clear()
        ROWS.clear()
        ALL_CELLS.clear()
        rowGroupSizes = null
        // TODO: Does it need this old-style border?
        val border = this.borderAttribute
        val cellSpacing = this.cellSpacingAttribute

        this.cellSpacingX = cellSpacing
        this.cellSpacingY = cellSpacing

        this.tableWidthLength = getWidthLength(this.tableElement, availWidth)

        this.populateRows()
        this.adjustForCellSpans()
        this.createSizeArrays()

        // Calculate widths of extras
        val columnSizes = this.columnSizes
        val numCols = columnSizes.size
        var widthsOfExtras = insets.left + insets.right + ((numCols + 1) * cellSpacing)
        if (border > 0) {
            widthsOfExtras += (numCols * 2)
        }
        this.widthsOfExtras = widthsOfExtras

        // Calculate heights of extras
        val rowSizes = this.rowSizes
        val numRows = rowSizes.size
        var heightsOfExtras = insets.top + insets.bottom + ((numRows + 1) * cellSpacing)
        if (border > 0) {
            heightsOfExtras += (numRows * 2)
        }
        this.heightsOfExtras = heightsOfExtras
        this.hasOldStyleBorder = if (border > 0) 1 else 0
    }

    private val cellSpacingAttribute: Int
        get() {
            var cellSpacing = 0
            val cellSpacingText = this.tableElement.getAttribute("cellspacing")
            if (cellSpacingText != null) {
                try {
                    // TODO: cellSpacing can be a percentage as well
                    cellSpacing = cellSpacingText.toInt()
                    if (cellSpacing < 0) {
                        cellSpacing = 0
                    }
                } catch (nfe: NumberFormatException) {
                    println("Exception while parsing cellSpacing: " + nfe)
                    // ignore
                }
            }
            return cellSpacing
        }

    private val borderAttribute: Int
        get() {
            var border = 0
            val borderText = this.tableElement.getAttribute("border")
            if (borderText != null) {
                if (borderText.length == 0) {
                    border = 1
                } else {
                    try {
                        border = borderText.toInt()
                        if (border < 0) {
                            border = 0
                        }
                    } catch (nfe: NumberFormatException) {
                        println("Exception while parsing border: " + nfe)
                        // ignore
                    }
                }
            }
            return border
        }

    fun build(availWidth: Int, availHeight: Int, sizeOnly: Boolean) {
        val hasBorder = this.hasOldStyleBorder
        this.determineColumnSizes(hasBorder, this.cellSpacingX, this.cellSpacingY, availWidth)
        this.determineRowSizes(hasBorder, this.cellSpacingY, availHeight, sizeOnly)
    }

    /**
     * Populates the ROWS and ALL_CELLS collections.
     */
    private fun populateRows(): ArrayList<HTMLElementImpl?> {
        val te = this.tableElement
        val rowElements = ArrayList<HTMLElementImpl?>()
        val tChildren = te.getChildrenArray()
        val rowRelation = TableRelation(this.ROWS, this.ROW_GROUPS)

        if (tChildren != null) {
            for (cn in tChildren) {
                if (cn is HTMLElementImpl) {
                    val display = cn.getRenderState().display
                    if (display == RenderState.DISPLAY_TABLE_ROW_GROUP || display == RenderState.DISPLAY_TABLE_HEADER_GROUP || display == RenderState.DISPLAY_TABLE_FOOTER_GROUP) {
                        processRowGroup(cn, rowRelation)
                    } else if (display == RenderState.DISPLAY_TABLE_ROW) {
                        processRow(cn, null, rowRelation)
                    } else if (display == RenderState.DISPLAY_TABLE_CELL) {
                        processCell(cn, null, null, rowRelation)
                    } else if (display != RenderState.DISPLAY_TABLE_COLUMN && display != RenderState.DISPLAY_TABLE_COLUMN_GROUP) {
                        addAnonCell(rowRelation, null, null, cn)
                    }
                } else if (cn is TextImpl) {
                    addAnonTextCell(rowRelation, null, null, cn)
                }
            }
        }

        rowRelation.finish()

        run {
            // Find the max insets among row group elements
            maxRowGroupLeft = 0
            maxRowGroupRight = 0
            for (rowGroup in this.ROW_GROUPS) {
                val groupInsets = rowGroup.groupBorderInsets
                if (groupInsets != null) {
                    if (groupInsets.left > maxRowGroupLeft) {
                        maxRowGroupLeft = groupInsets.left
                    }
                    if (groupInsets.right > maxRowGroupRight) {
                        maxRowGroupRight = groupInsets.right
                    }
                }
            }
        }

        return rowElements
    }

    private fun processCell(
        ce: HTMLElementImpl,
        rowGroupElem: HTMLElementImpl?,
        rowElem: HTMLElementImpl?,
        rowRelation: TableRelation
    ) {
        val ac =
            RTableCell(ce, this.uaContext, this.rendererContext, this.frameContext, this.container)
        ac.setParent(this.relement)
        ce.uINode = ac
        val vc = VirtualCell(ac, true)
        ac.topLeftVirtualCell = (vc)
        rowRelation.associate(rowGroupElem, rowElem, vc)
        this.ALL_CELLS.add(ac)
    }

    private fun processRow(
        rowE: HTMLElementImpl,
        rowGroupElem: HTMLElementImpl?,
        rowRelation: TableRelation
    ) {
        val rChildren = rowE.getChildrenArray()
        if (rChildren != null) {
            for (cn in rChildren) {
                if (cn is HTMLElementImpl) {
                    val display = cn.getRenderState().display
                    if (display == RenderState.DISPLAY_TABLE_CELL) {
                        processCell(cn, rowGroupElem, rowE, rowRelation)
                    } else {
                        addAnonCell(rowRelation, rowGroupElem, rowE, cn)
                    }
                } else if (cn is TextImpl) {
                    addAnonTextCell(rowRelation, rowGroupElem, rowE, cn)
                }
            }
        }
    }

    private fun processRowGroup(rowGroupElem: HTMLElementImpl, rowRelation: TableRelation) {
        val rChildren = rowGroupElem.getChildrenArray()
        if (rChildren != null) {
            for (cn in rChildren) {
                if (cn is HTMLElementImpl) {
                    val display = cn.getRenderState().display
                    if (display == RenderState.DISPLAY_TABLE_ROW) {
                        processRow(cn, rowGroupElem, rowRelation)
                    } else {
                        addAnonCell(rowRelation, rowGroupElem, null, cn)
                    }
                } else if (cn is TextImpl) {
                    addAnonTextCell(rowRelation, rowGroupElem, null, cn)
                }
            }
        }
    }

    private fun addAnonTextCell(
        rowRelation: TableRelation,
        rowGroupElem: HTMLElementImpl?,
        rowElem: HTMLElementImpl?,
        tn: TextImpl
    ) {
        if (!tn.isElementContentWhitespace()) {
            addAnonCell(rowRelation, rowGroupElem, rowElem, tn)
        }
    }

    private fun addAnonCell(
        rowRelation: TableRelation,
        rowGroupElem: HTMLElementImpl?,
        rowElem: HTMLElementImpl?,
        node: NodeImpl
    ) {
        val acn = AnonymousNodeImpl(node.getParentNode())
        acn.appendChildSilently(node)
        val ac = RAnonTableCell(
            acn,
            this.uaContext,
            this.rendererContext,
            this.frameContext,
            this.container
        )
        ac.setParent(this.relement)
        acn.uINode = ac
        val vc = VirtualCell(ac, true)
        ac.topLeftVirtualCell = (vc)
        rowRelation.associate(rowGroupElem, rowElem, vc)
        this.ALL_CELLS.add(ac)
    }

    /**
     * Based on colspans and rowspans, creates additional virtual cells from
     * actual table cells.
     */
    private fun adjustForCellSpans() {
        val rows = this.ROWS
        var numRows = rows.size
        run {
            var r = 0
            while (r < numRows) {
                val row = rows.get(r)
                var numCols = row.size()
                var c = 0
                while (c < numCols) {
                    val vc = row.get(c)
                    if ((vc != null) && vc.isTopLeft) {
                        val ac = vc.actualCell
                        var colspan = ac.colSpan
                        if (colspan < 1) {
                            colspan = 1
                        }
                        var rowspan = ac.rowSpan
                        if (rowspan < 1) {
                            rowspan = 1
                        }

                        // Can't go beyond last row (Fix bug #2022584)
                        val targetRows = r + rowspan
                        if (numRows < targetRows) {
                            rowspan = numRows - r
                            ac.rowSpan = (rowspan)
                        }

                        numRows = rows.size
                        for (y in 0..<rowspan) {
                            if ((colspan > 1) || (y > 0)) {
                                // Get row
                                val nr = r + y
                                val newRow = rows.get(nr)

                                // Insert missing cells in row
                                val xstart = if (y == 0) 1 else 0

                                // Insert virtual cells, potentially
                                // shifting others to the right.
                                for (cc in xstart..<colspan) {
                                    val nc = c + cc
                                    while (newRow.size() < nc) {
                                        newRow.add(null)
                                    }
                                    newRow.add(nc, VirtualCell(ac, false))
                                }
                                if (row == newRow) {
                                    numCols = row.size()
                                }
                            }
                        }
                    }
                    c++
                }
                r++
            }
        }

        // Adjust row and column of virtual cells
        for (r in 0..<numRows) {
            val row = rows.get(r)
            val numCols = row.size()
            for (c in 0..<numCols) {
                val vc = row.get(c)
                if (vc != null) {
                    vc.column = (c)
                    vc.row = (r)
                }
            }
        }
    }

    /**
     * Populates the columnSizes and rowSizes arrays, setting htmlLength in each
     * element.
     */
    private fun createSizeArrays() {
        var numCols = 0
        val rows = this.ROWS
        val numRows = rows.size

        run {

            for (i in 0..<numRows) {
                val row = rows.get(i)
                val numColsInThisRow = row.size()
                if (numColsInThisRow > numCols) {
                    numCols = numColsInThisRow
                }
                val rowSizeInfo = RowSizeInfo()
                rowSizes[i] = rowSizeInfo

                var bestHeightLength: HtmlLength? = null
                for (x in 0..<numColsInThisRow) {
                    val vc = row.get(x)
                    if (vc != null) {
                        val vcHeightLength = vc.heightLength
                        if ((vcHeightLength != null) && vcHeightLength.isPreferredOver(
                                bestHeightLength
                            )
                        ) {
                            bestHeightLength = vcHeightLength
                        }
                        rowSizeInfo.offsetX = maxRowGroupLeft
                    }
                }
                rowSizeInfo.htmlLength = bestHeightLength

                val rowGroupInsets = row.rowGroup!!.groupBorderInsets
                if (row.firstInGroup && rowGroupInsets != null) {
                    rowSizeInfo.marginTop = max(0, rowGroupInsets.top)
                }
                if (row.lastInGroup && rowGroupInsets != null) {
                    rowSizeInfo.marginBottom =
                        max(0, rowGroupInsets.bottom - row.maxCellBorderBottom)
                }
            }
        }


        for (i in 0..<numCols) {
            var bestWidthLength: HtmlLength? = null

            // Cells with colspan==1 first.
            for (y in 0..<numRows) {
                val row = rows.get(y)
                var vc: VirtualCell?
                try {
                    vc = row.get(i)
                } catch (iob: IndexOutOfBoundsException) {
                    vc = null
                }
                if (vc != null) {
                    val ac = vc.actualCell
                    if (ac.colSpan == 1) {
                        val vcWidthLength = vc.widthLength
                        if ((vcWidthLength != null) && vcWidthLength.isPreferredOver(bestWidthLength)) {
                            bestWidthLength = vcWidthLength
                        }
                    }
                }
            }
            // Now cells with colspan>1.
            if (bestWidthLength == null) {
                for (y in 0..<numRows) {
                    val row = rows.get(y)
                    var vc: VirtualCell?
                    try {
                        vc = row.get(i)
                    } catch (iob: IndexOutOfBoundsException) {
                        vc = null
                    }
                    if (vc != null) {
                        val ac = vc.actualCell
                        if (ac.colSpan > 1) {
                            val vcWidthLength = vc.widthLength
                            if ((vcWidthLength != null) && vcWidthLength.isPreferredOver(
                                    bestWidthLength
                                )
                            ) {
                                bestWidthLength = vcWidthLength
                            }
                        }
                    }
                }
            }
            val colSizeInfo = ColSizeInfo()
            colSizeInfo.htmlLength = bestWidthLength
            columnSizes[i] = colSizeInfo
        }
    }

    /**
     * Determines the size of each column, and the table width. Does the
     * following:
     *
     *  1. Determine tentative widths. This is done by looking at declared column
     * widths, any table width, and filling in the blanks. No rendering is done.
     * The tentative width of columns with no declared width is zero.
     *
     *  1. Render all cell blocks. It uses the tentative widths from the previous
     * step as a desired width. The resulting width is considered a sort of
     * minimum. If the column width is not defined, use a NOWRAP override flag to
     * render.
     *
     *  1. Check if cell widths are too narrow for the rendered width. In the case
     * of columns without a declared width, check if they are too wide.
     *
     *  1. Finally, adjust widths considering the expected max table size. Columns
     * are layed out again if necessary to determine if they can really be shrunk.
     *
     *
     * @param cellSpacingX
     * @param cellSpacingY
     * @param availWidth
     */
    private fun determineColumnSizes(
        hasBorder: Int,
        cellSpacingX: Int,
        cellSpacingY: Int,
        availWidth: Int
    ) {
        val tableWidthLength = this.tableWidthLength
        var tableWidth: Int
        val widthKnown: Boolean
        if (tableWidthLength != null) {
            tableWidth = tableWidthLength.getLength(availWidth)
            widthKnown = true
        } else {
            tableWidth = availWidth
            widthKnown = false
        }
        tableWidth -= (this.maxRowGroupLeft + this.maxRowGroupRight) / 2

        val columnSizes = this.columnSizes
        val widthsOfExtras = this.widthsOfExtras
        var cellAvailWidth = tableWidth - widthsOfExtras
        if (cellAvailWidth < 0) {
            tableWidth += (-cellAvailWidth)
            cellAvailWidth = 0
        }

        // Determine tentative column widths based on specified cell widths
        determineTentativeSizes(columnSizes, widthsOfExtras, cellAvailWidth, widthKnown)

        // Pre-layout cells. This will give the minimum width of each cell,
        // in addition to the minimum height.
        this.preLayout(hasBorder, cellSpacingX, cellSpacingY, widthKnown)

        // Increases column widths if they are less than minimums of each cell.
        adjustForLayoutWidths(columnSizes, hasBorder, cellSpacingX, widthKnown)

        // Adjust for expected total width
        this.adjustWidthsForExpectedMax(columnSizes, cellAvailWidth, widthKnown)
    }

    private fun layoutColumn(
        columnSizes: List<ColSizeInfo>,
        colSize: ColSizeInfo,
        col: Int,
        cellSpacingX: Int,
        hasBorder: Int
    ) {
        val rowSizes = this.rowSizes
        val rows = this.ROWS
        val numRows = rows.size
        val actualSize = colSize.actualSize
        colSize.layoutSize = 0
        var rowIndx = 0
        while (rowIndx < numRows) {
            // SizeInfo rowSize = rowSizes[row];
            val row = rows.get(rowIndx)
            var vc: VirtualCell? = null
            try {
                vc = row.get(col)
            } catch (iob: IndexOutOfBoundsException) {
                vc = null
            }
            val ac = if (vc == null) null else vc.actualCell
            if (ac != null) {
                if (ac.virtualRow == rowIndx) {
                    // Only process actual cells with a row
                    // beginning at the current row being processed.
                    val colSpan = ac.colSpan
                    if (colSpan > 1) {
                        val firstCol = ac.virtualColumn
                        val cellExtras = (colSpan - 1) * (cellSpacingX + (2 * hasBorder))
                        var vcActualWidth = cellExtras
                        for (x in 0..<colSpan) {
                            vcActualWidth += columnSizes[firstCol + x].actualSize
                        }
                        // TODO: better height possible
                        val size = ac.doCellLayout(vcActualWidth, 0, true, true, true)
                        val vcRenderWidth = size!!.width

                        val denominator = (vcActualWidth - cellExtras)
                        val newTentativeCellWidth: Int
                        if (denominator > 0) {
                            newTentativeCellWidth =
                                (actualSize * (vcRenderWidth - cellExtras)) / denominator
                        } else {
                            newTentativeCellWidth = (vcRenderWidth - cellExtras) / colSpan
                        }
                        if (newTentativeCellWidth > colSize.layoutSize) {
                            colSize.layoutSize = newTentativeCellWidth
                        }
                        val rowSpan = ac.rowSpan
                        val vch =
                            (size.height - ((rowSpan - 1) * (this.cellSpacingY + (2 * hasBorder)))) / rowSpan
                        for (y in 0..<rowSpan) {
                            if (rowSizes[rowIndx + y].minSize < vch) {
                                rowSizes[rowIndx + y].minSize = vch
                            }
                        }
                    } else {
                        // TODO: better height possible
                        val size = ac.doCellLayout(
                            actualSize, 0,
                            true, true, true
                        )!!
                        if (size.width > colSize.layoutSize) {
                            colSize.layoutSize = size.width
                        }

                        val cbi = ac.getBorderInsets()
                        val cellFullLayoutWidth = size.width + cbi.left + cbi.right
                        if (cellFullLayoutWidth > colSize.fullLayoutSize) {
                            colSize.fullLayoutSize = cellFullLayoutWidth
                        }

                        val rowSpan = ac.rowSpan
                        val vch =
                            (size.height - ((rowSpan - 1) * (this.cellSpacingY + (2 * hasBorder)))) / rowSpan
                        for (y in 0..<rowSpan) {
                            if (rowSizes[rowIndx + y].minSize < vch) {
                                rowSizes[rowIndx + y].minSize = vch
                            }
                        }
                    }
                }
            }
            // row = (ac == null ? row + 1 : ac.getVirtualRow() + ac.getRowSpan());
            rowIndx++
        }
    }

    private fun adjustWidthsForExpectedMax(
        columnSizes: List<ColSizeInfo>,
        cellAvailWidth: Int,
        expand: Boolean
    ): Int {
        val hasBorder = this.hasOldStyleBorder
        val cellSpacingX = this.cellSpacingX
        var currentTotal = 0
        val numCols = columnSizes.size
        for (i in 0..<numCols) {
            currentTotal += columnSizes[i].fullActualSize
        }
        var difference = currentTotal - (this.widthsOfExtras + cellAvailWidth)
        // int difference = currentTotal - (cellAvailWidth);
        if ((difference > 0) || ((difference < 0) && expand)) {
            // First, try to contract/expand columns with no width
            var noWidthTotal = 0
            var numNoWidth = 0
            for (i in 0..<numCols) {
                if (columnSizes[i].htmlLength == null) {
                    numNoWidth++
                    noWidthTotal += columnSizes[i].fullActualSize
                }
            }
            if (numNoWidth > 0) {
                // TODO: This is not shrinking correctly.
                var expectedNoWidthTotal = noWidthTotal - difference - this.widthsOfExtras
                if (expectedNoWidthTotal < 0) {
                    expectedNoWidthTotal = 0
                }
                val ratio = (expectedNoWidthTotal.toDouble()) / noWidthTotal
                var noWidthCount = 0
                for (i in 0..<numCols) {
                    val sizeInfo = columnSizes[i]
                    if (sizeInfo.htmlLength == null) {
                        val oldActualSize = sizeInfo.fullActualSize
                        var newActualSize: Int
                        if (++noWidthCount == numNoWidth) {
                            // Last column without a width.
                            val currentDiff = currentTotal - cellAvailWidth
                            newActualSize = oldActualSize - currentDiff
                            if (newActualSize < 0) {
                                newActualSize = 0
                            }
                        } else {
                            newActualSize = Math.round(oldActualSize * ratio).toInt()
                        }
                        sizeInfo.actualSize = newActualSize
                        if (newActualSize < sizeInfo.fullLayoutSize) {
                            // See if it actually fits.
                            this.layoutColumn(columnSizes, sizeInfo, i, cellSpacingX, hasBorder)
                            if (newActualSize < sizeInfo.layoutSize) {
                                // Didn't fit.
                                newActualSize = sizeInfo.layoutSize
                                sizeInfo.actualSize = newActualSize
                            }
                        }
                        currentTotal += (newActualSize - oldActualSize)
                    }
                }
                difference = currentTotal - cellAvailWidth
            }

            // See if absolutes need to be contracted
            if ((difference > 0) || ((difference < 0) && expand)) {
                var absoluteWidthTotal = 0
                for (i in 0..<numCols) {
                    val widthLength = columnSizes[i].htmlLength
                    if ((widthLength != null) && (widthLength.lengthType != HtmlLength.LENGTH)) {
                        absoluteWidthTotal += columnSizes[i].fullActualSize
                    }
                }
                if (absoluteWidthTotal > 0) {
                    var expectedAbsoluteWidthTotal =
                        absoluteWidthTotal - difference - this.widthsOfExtras
                    if (expectedAbsoluteWidthTotal < 0) {
                        expectedAbsoluteWidthTotal = 0
                    }
                    val ratio = (expectedAbsoluteWidthTotal.toDouble()) / absoluteWidthTotal
                    for (i in 0..<numCols) {
                        val sizeInfo = columnSizes[i]
                        val widthLength = columnSizes[i].htmlLength
                        if ((widthLength != null) && (widthLength.lengthType != HtmlLength.LENGTH)) {
                            val oldActualSize = sizeInfo.fullActualSize
                            var newActualSize = Math.round(oldActualSize * ratio).toInt()
                            sizeInfo.actualSize = newActualSize
                            if (newActualSize < sizeInfo.fullLayoutSize) {
                                // See if it actually fits.
                                this.layoutColumn(columnSizes, sizeInfo, i, cellSpacingX, hasBorder)
                                if (newActualSize < sizeInfo.layoutSize) {
                                    // Didn't fit.
                                    newActualSize = sizeInfo.layoutSize
                                    sizeInfo.actualSize = newActualSize
                                }
                            }
                            currentTotal += (newActualSize - oldActualSize)
                        }
                    }
                    difference = currentTotal - cellAvailWidth
                }

                // See if percentages need to be contracted
                if ((difference > 0) || ((difference < 0) && expand)) {
                    var percentWidthTotal = 0
                    for (i in 0..<numCols) {
                        val widthLength = columnSizes[i].htmlLength
                        if ((widthLength != null) && (widthLength.lengthType == HtmlLength.LENGTH)) {
                            percentWidthTotal += columnSizes[i].actualSize
                        }
                    }
                    if (percentWidthTotal > 0) {
                        var expectedPercentWidthTotal = percentWidthTotal - difference
                        if (expectedPercentWidthTotal < 0) {
                            expectedPercentWidthTotal = 0
                        }
                        val ratio = expectedPercentWidthTotal.toDouble() / percentWidthTotal
                        for (i in 0..<numCols) {
                            val sizeInfo = columnSizes[i]
                            val widthLength = columnSizes[i].htmlLength
                            if ((widthLength != null) && (widthLength.lengthType == HtmlLength.LENGTH)) {
                                val oldActualSize = sizeInfo.actualSize
                                var newActualSize = Math.round(oldActualSize * ratio).toInt()
                                sizeInfo.actualSize = newActualSize
                                if (newActualSize < sizeInfo.layoutSize) {
                                    // See if it actually fits.
                                    this.layoutColumn(
                                        columnSizes,
                                        sizeInfo,
                                        i,
                                        cellSpacingX,
                                        hasBorder
                                    )
                                    if (newActualSize < sizeInfo.layoutSize) {
                                        // Didn't fit.
                                        newActualSize = sizeInfo.layoutSize
                                        sizeInfo.actualSize = newActualSize
                                    }
                                }
                                currentTotal += (newActualSize - oldActualSize)
                            }
                        }
                    }
                }
            }
        } else {
            if (expand) {
                for (i in 0..<numCols) {
                    val sizeInfo = columnSizes[i]
                    sizeInfo.actualSize = sizeInfo.fullActualSize
                }
            }
        }
        return currentTotal
    }

    /**
     * This method renders each cell using already set actual column widths. It
     * sets minimum row heights based on this.
     */
    private fun preLayout(
        hasBorder: Int,
        cellSpacingX: Int,
        cellSpacingY: Int,
        tableWidthKnown: Boolean
    ) {
        // TODO: Fix for table without width that has a subtable with width=100%.
        // TODO: Maybe it can be addressed when NOWRAP is implemented.
        // TODO: Maybe it's possible to eliminate this pre-layout altogether.

        val colSizes = this.columnSizes
        val rowSizes = this.rowSizes

        // Initialize minSize in rows
        val numRows = rowSizes.size
        for (i in 0..<numRows) {
            rowSizes[i].minSize = 0
        }

        // Initialize layoutSize in columns
        val numCols = colSizes.size
        for (i in 0..<numCols) {
            colSizes[i].layoutSize = 0
            colSizes[i].fullLayoutSize = 0
        }

        for (cell in this.ALL_CELLS) {
            val col = cell.virtualColumn
            val colSpan = cell.colSpan
            val cellsTotalWidth: Int
            var cellsUsedWidth: Int
            var widthDeclared = false
            if (colSpan > 1) {
                cellsUsedWidth = 0
                for (x in 0..<colSpan) {
                    val colSize = colSizes[col + x]
                    if (colSize.htmlLength != null) {
                        widthDeclared = true
                    }
                    cellsUsedWidth += colSize.actualSize
                }
                cellsTotalWidth =
                    cellsUsedWidth + ((colSpan - 1) * (cellSpacingX + (2 * hasBorder)))
            } else {
                val colSize = colSizes[col]
                if (colSize.htmlLength != null) {
                    widthDeclared = true
                }
                cellsTotalWidth = colSize.actualSize
                cellsUsedWidth = cellsTotalWidth
            }

            // TODO: A tentative height could be used here: Height of
            // table divided by number of rows.
            var size: Dimension
            val state = RenderThreadState.state
            val prevOverrideNoWrap = state.overrideNoWrap
            try {
                if (!prevOverrideNoWrap) {
                    state.overrideNoWrap = !widthDeclared
                }
                size = cell.doCellLayout(cellsTotalWidth, 0, true, true, true)!!
            } finally {
                state.overrideNoWrap = prevOverrideNoWrap
            }
            // Set render widths
            val cellLayoutWidth = size.width
            val cbi = cell.getBorderInsets()
            val cellFullLayoutWidth = size.width + cbi.left + cbi.right
            if (colSpan > 1) {
                // TODO: set fullLayoutSize
                if (cellsUsedWidth > 0) {
                    val ratio = cellLayoutWidth.toDouble() / cellsUsedWidth
                    for (x in 0..<colSpan) {
                        val si = colSizes[col + x]
                        val newLayoutSize = Math.round(si.actualSize * ratio).toInt()
                        if (si.layoutSize < newLayoutSize) {
                            si.layoutSize = newLayoutSize
                        }
                    }
                } else {
                    val newLayoutSize = cellLayoutWidth / colSpan
                    for (x in 0..<colSpan) {
                        val si = colSizes[col + x]
                        if (si.layoutSize < newLayoutSize) {
                            si.layoutSize = newLayoutSize
                        }
                    }
                }
            } else {
                val colSizeInfo = colSizes[col]
                if (colSizeInfo.layoutSize < cellLayoutWidth) {
                    colSizeInfo.layoutSize = cellLayoutWidth
                }
                if (colSizeInfo.fullLayoutSize < cellFullLayoutWidth) {
                    colSizeInfo.fullLayoutSize = cellFullLayoutWidth
                }
            }

            // Set minimum heights
            val actualCellHeight = size.height
            val row = cell.virtualRow
            val rowSpan = cell.rowSpan
            if (rowSpan > 1) {
                val vch =
                    (actualCellHeight - ((rowSpan - 1) * (cellSpacingY + (2 * hasBorder)))) / rowSpan
                for (y in 0..<rowSpan) {
                    if (rowSizes[row + y].minSize < vch) {
                        rowSizes[row + y].minSize = vch
                    }
                }
            } else {
                if (rowSizes[row].minSize < actualCellHeight) {
                    rowSizes[row].minSize = actualCellHeight
                }
            }
        }
    }

    private fun determineRowSizes(
        hasBorder: Int,
        cellSpacing: Int,
        availHeight: Int,
        sizeOnly: Boolean
    ) {
        val tableHeightLength: HtmlLength? = getHeightLength(this.tableElement, availHeight)
        var tableHeight: Int
        val rowSizes = this.rowSizes
        val numRows = rowSizes.size
        val heightsOfExtras = this.heightsOfExtras
        if (tableHeightLength != null) {
            tableHeight = tableHeightLength.getLength(availHeight)
            this.determineRowSizesFixedTH(
                hasBorder,
                cellSpacing,
                availHeight,
                tableHeight,
                sizeOnly
            )
        } else {
            tableHeight = heightsOfExtras
            for (row in 0..<numRows) {
                tableHeight += rowSizes[row].minSize
            }
            this.determineRowSizesFlexibleTH(hasBorder, cellSpacing, availHeight, sizeOnly)
        }
    }

    private fun determineRowSizesFixedTH(
        hasBorder: Int, cellSpacing: Int, availHeight: Int, tableHeight: Int,
        sizeOnly: Boolean
    ) {
        val rowSizes = this.rowSizes
        val numRows = rowSizes.size
        val heightsOfExtras = this.heightsOfExtras
        var cellAvailHeight = tableHeight - heightsOfExtras
        if (cellAvailHeight < 0) {
            cellAvailHeight = 0
        }

        // Look at percentages first
        var heightUsedbyPercent = 0
        var otherMinSize = 0
        for (i in 0..<numRows) {
            val rowSizeInfo = rowSizes[i]
            val heightLength = rowSizeInfo.htmlLength
            if ((heightLength != null) && (heightLength.lengthType == HtmlLength.LENGTH)) {
                var actualSizeInt = heightLength.getLength(cellAvailHeight)
                if (actualSizeInt < rowSizeInfo.minSize) {
                    actualSizeInt = rowSizeInfo.minSize
                }
                heightUsedbyPercent += actualSizeInt
                rowSizeInfo.actualSize = actualSizeInt
            } else {
                otherMinSize += rowSizeInfo.minSize
            }
        }

        // Check if rows with percent are bigger than they should be
        if ((heightUsedbyPercent + otherMinSize) > cellAvailHeight) {
            val ratio = (cellAvailHeight - otherMinSize).toDouble() / heightUsedbyPercent
            for (i in 0..<numRows) {
                val rowSizeInfo = rowSizes[i]
                val heightLength = rowSizeInfo.htmlLength
                if ((heightLength != null) && (heightLength.lengthType == HtmlLength.LENGTH)) {
                    val actualSize = rowSizeInfo.actualSize
                    val prevActualSize = actualSize
                    var newActualSize = Math.round(prevActualSize * ratio).toInt()
                    if (newActualSize < rowSizeInfo.minSize) {
                        newActualSize = rowSizeInfo.minSize
                    }
                    heightUsedbyPercent += (newActualSize - prevActualSize)
                    rowSizeInfo.actualSize = newActualSize
                }
            }
        }

        // Look at rows with absolute sizes
        var heightUsedByAbsolute = 0
        var noHeightMinSize = 0
        var numNoHeightColumns = 0
        for (i in 0..<numRows) {
            val rowSizeInfo = rowSizes[i]
            val heightLength = rowSizeInfo.htmlLength
            if ((heightLength != null) && (heightLength.lengthType != HtmlLength.LENGTH)) {
                // TODO: MULTI-LENGTH not supported
                var actualSizeInt = heightLength.rawValue
                if (actualSizeInt < rowSizeInfo.minSize) {
                    actualSizeInt = rowSizeInfo.minSize
                }
                heightUsedByAbsolute += actualSizeInt
                rowSizeInfo.actualSize = actualSizeInt
            } else if (heightLength == null) {
                numNoHeightColumns++
                noHeightMinSize += rowSizeInfo.minSize
            }
        }

        // Check if absolute sizing is too much
        if ((heightUsedByAbsolute + heightUsedbyPercent + noHeightMinSize) > cellAvailHeight) {
            val ratio =
                (cellAvailHeight - noHeightMinSize - heightUsedbyPercent).toDouble() / heightUsedByAbsolute
            for (i in 0..<numRows) {
                val rowSizeInfo = rowSizes[i]
                val heightLength = rowSizeInfo.htmlLength
                if ((heightLength != null) && (heightLength.lengthType != HtmlLength.LENGTH)) {
                    val actualSize = rowSizeInfo.actualSize
                    val prevActualSize = actualSize
                    var newActualSize = Math.round(prevActualSize * ratio).toInt()
                    if (newActualSize < rowSizeInfo.minSize) {
                        newActualSize = rowSizeInfo.minSize
                    }
                    heightUsedByAbsolute += (newActualSize - prevActualSize)
                    rowSizeInfo.actualSize = newActualSize
                }
            }
        }

        // Assign all rows without heights now
        val remainingHeight = cellAvailHeight - heightUsedByAbsolute - heightUsedbyPercent
        var heightUsedByRemaining = 0
        for (i in 0..<numRows) {
            val rowSizeInfo = rowSizes[i]
            val heightLength = rowSizeInfo.htmlLength
            if (heightLength == null) {
                var actualSizeInt = remainingHeight / numNoHeightColumns
                if (actualSizeInt < rowSizeInfo.minSize) {
                    actualSizeInt = rowSizeInfo.minSize
                }
                heightUsedByRemaining += actualSizeInt
                rowSizeInfo.actualSize = actualSizeInt
            }
        }

        // Calculate actual table width
        val totalUsed = heightUsedByAbsolute + heightUsedbyPercent + heightUsedByRemaining
        if (totalUsed >= cellAvailHeight) {
            this.tableHeight = totalUsed + heightsOfExtras
        } else {
            // Rows too short; expand them
            val ratio = cellAvailHeight.toDouble() / totalUsed
            for (i in 0..<numRows) {
                val rowSizeInfo = rowSizes[i]
                val actualSize = rowSizeInfo.actualSize
                rowSizeInfo.actualSize = Math.round(actualSize * ratio).toInt()
            }
            this.tableHeight = tableHeight
        }

        // TODO:
        // This final render is probably unnecessary. Avoid exponential rendering
        // by setting a single height of subcell. Verify that IE only sets height
        // of subcells when height of row or table are specified.
        this.finalLayout(hasBorder, cellSpacing, sizeOnly)
    }

    private fun determineRowSizesFlexibleTH(
        hasBorder: Int,
        cellSpacing: Int,
        availHeight: Int,
        sizeOnly: Boolean
    ) {
        val rowSizes = this.rowSizes
        val numRows = rowSizes.size
        val heightsOfExtras = this.heightsOfExtras

        // Look at rows with absolute sizes
        var heightUsedByAbsolute = 0
        var percentSum = 0
        for (i in 0..<numRows) {
            val rowSizeInfo = rowSizes[i]
            val heightLength = rowSizeInfo.htmlLength
            if ((heightLength != null) && (heightLength.lengthType == HtmlLength.PIXELS)) {
                // TODO: MULTI-LENGTH not supported
                var actualSizeInt = heightLength.rawValue
                if (actualSizeInt < rowSizeInfo.minSize) {
                    actualSizeInt = rowSizeInfo.minSize
                }
                heightUsedByAbsolute += actualSizeInt
                rowSizeInfo.actualSize = actualSizeInt
            } else if ((heightLength != null) && (heightLength.lengthType == HtmlLength.LENGTH)) {
                percentSum += heightLength.rawValue
            }
        }

        // Look at rows with no specified heights
        var heightUsedByNoSize = 0

        // Set sizes to in row height
        for (i in 0..<numRows) {
            val rowSizeInfo = rowSizes[i]
            val widthLength = rowSizeInfo.htmlLength
            if (widthLength == null) {
                val actualSizeInt = rowSizeInfo.minSize
                heightUsedByNoSize += actualSizeInt
                rowSizeInfo.actualSize = actualSizeInt
            }
        }

        // Calculate actual total cell width
        val expectedTotalCellHeight =
            Math.round((heightUsedByAbsolute + heightUsedByNoSize) / (1 - (percentSum / 100.0)))
                .toInt()

        // Set widths of columns with percentages
        var heightUsedByPercent = 0
        for (i in 0..<numRows) {
            val rowSizeInfo = rowSizes[i]
            val heightLength = rowSizeInfo.htmlLength
            if ((heightLength != null) && (heightLength.lengthType == HtmlLength.LENGTH)) {
                var actualSizeInt = heightLength.getLength(expectedTotalCellHeight)
                if (actualSizeInt < rowSizeInfo.minSize) {
                    actualSizeInt = rowSizeInfo.minSize
                }
                heightUsedByPercent += actualSizeInt
                rowSizeInfo.actualSize = actualSizeInt
            }
        }

        // Set width of table
        this.tableHeight =
            heightUsedByAbsolute + heightUsedByNoSize + heightUsedByPercent + heightsOfExtras

        // Do a final layouts to set actual cell sizes
        this.finalLayout(hasBorder, cellSpacing, sizeOnly)
    }

    /**
     * This method layouts each cell using already set actual column widths. It
     * sets minimum row heights based on this.
     */
    private fun finalLayout(hasBorder: Int, cellSpacing: Int, sizeOnly: Boolean) {
        // finalLayout needs to adjust actualSize of columns and rows
        // given that things might change as we layout one last time.
        val colSizes = this.columnSizes
        val rowSizes = this.rowSizes
        for (cell in this.ALL_CELLS) {
            val col = cell.virtualColumn
            val colSpan = cell.colSpan
            var totalCellWidth: Int
            if (colSpan > 1) {
                totalCellWidth = (colSpan - 1) * (cellSpacing + (2 * hasBorder))
                for (x in 0..<colSpan) {
                    totalCellWidth += colSizes[col + x].actualSize
                }
            } else {
                totalCellWidth = colSizes[col].actualSize
            }
            val row = cell.virtualRow
            val rowSpan = cell.rowSpan
            var totalCellHeight: Int
            if (rowSpan > 1) {
                totalCellHeight = (rowSpan - 1) * (cellSpacing + (2 * hasBorder))
                for (y in 0..<rowSpan) {
                    totalCellHeight += rowSizes[row + y].actualSize
                }
            } else {
                totalCellHeight = rowSizes[row].actualSize
            }
            val size = cell.doCellLayout(totalCellWidth, totalCellHeight, true, true, sizeOnly)!!
            if (size.width > totalCellWidth) {
                if (colSpan == 1) {
                    colSizes[col].actualSize = size.width
                } else {
                    colSizes[col].actualSize += (size.width - totalCellWidth)
                }
            }
            if (size.height > totalCellHeight) {
                if (rowSpan == 1) {
                    rowSizes[row].actualSize = size.height
                } else {
                    rowSizes[row].actualSize += (size.height - totalCellHeight)
                }
            }
        }
    }

    // public final void adjust() {
    // // finalRender needs to adjust actualSize of columns and rows
    // // given that things might change as we render one last time.
    // int hasBorder = this.hasOldStyleBorder;
    // int cellSpacingX = this.cellSpacingX;
    // int cellSpacingY = this.cellSpacingY;
    // ArrayList allCells = this.ALL_CELLS;
    // SizeInfo[] colSizes = this.columnSizes;
    // SizeInfo[] rowSizes = this.rowSizes;
    // int numCells = allCells.size();
    // for(int i = 0; i < numCells; i++) {
    // RTableCell cell = (RTableCell) allCells.get(i);
    // int col = cell.getVirtualColumn();
    // int colSpan = cell.getColSpan();
    // int totalCellWidth;
    // if(colSpan > 1) {
    // totalCellWidth = (colSpan - 1) * (cellSpacingX + 2 * hasBorder);
    // for(int x = 0; x < colSpan; x++) {
    // totalCellWidth += colSizes[col + x].actualSize;
    // }
    // }
    // else {
    // totalCellWidth = colSizes[col].actualSize;
    // }
    // int row = cell.getVirtualRow();
    // int rowSpan = cell.getRowSpan();
    // int totalCellHeight;
    // if(rowSpan > 1) {
    // totalCellHeight = (rowSpan - 1) * (cellSpacingY + 2 * hasBorder);
    // for(int y = 0; y < rowSpan; y++) {
    // totalCellHeight += rowSizes[row + y].actualSize;
    // }
    // }
    // else {
    // totalCellHeight = rowSizes[row].actualSize;
    // }
    // cell.adjust();
    // Dimension size = cell.getSize();
    // if(size.width > totalCellWidth) {
    // if(colSpan == 1) {
    // colSizes[col].actualSize = size.width;
    // }
    // else {
    // colSizes[col].actualSize += (size.width - totalCellWidth);
    // }
    // }
    // if(size.height > totalCellHeight) {
    // if(rowSpan == 1) {
    // rowSizes[row].actualSize = size.height;
    // }
    // else {
    // rowSizes[row].actualSize += (size.height - totalCellHeight);
    // }
    // }
    // }
    // }
    /**
     * Sets bounds of each cell's component, and sums up table width and height.
     */
    fun doLayout(insets: Insets) {
        // Set row offsets

        val rowSizes = this.rowSizes
        val numRows = rowSizes.size
        var yoffset = insets.top
        val cellSpacingY = this.cellSpacingY
        val hasBorder = this.hasOldStyleBorder
        for (i in 0..<numRows) {
            yoffset += cellSpacingY
            yoffset += hasBorder
            val rowSizeInfo = rowSizes[i]
            yoffset += rowSizeInfo.marginTop
            rowSizeInfo.offsetY = yoffset
            rowSizeInfo.insetLeft = insets.left
            rowSizeInfo.insetRight = insets.right
            yoffset += rowSizeInfo.actualSize
            yoffset += hasBorder
            yoffset += rowSizeInfo.marginBottom
        }
        this.tableHeight = yoffset + cellSpacingY + insets.bottom

        // Set column offsets
        val colSizes = this.columnSizes
        val numColumns = colSizes.size
        var xoffset = insets.left
        val cellSpacingX = this.cellSpacingX
        for (i in 0..<numColumns) {
            xoffset += cellSpacingX
            xoffset += hasBorder
            val colSizeInfo = colSizes[i]
            colSizeInfo.offsetX = xoffset
            xoffset += colSizeInfo.actualSize
            xoffset += hasBorder
        }
        this.tableWidth = xoffset + cellSpacingX + insets.right + (maxRowGroupRight / 2)

        // Set offsets of each cell
        for (cell in this.ALL_CELLS) {
            cell.setCellBounds(colSizes, rowSizes, hasBorder, cellSpacingX, cellSpacingY)
        }
        this.rowGroupSizes = prepareRowGroupSizes()
    }

    fun paint(g: Graphics, size: Dimension?) {
        // Paint row group backgrounds
        for (rgsi in rowGroupSizes!!) {
            rgsi.prePaintBackground(g)
        }

        for (cell in this.ALL_CELLS) {
            // Should clip table cells, just in case.
            val newG = g.create(cell.x, cell.y, cell.width, cell.height)
            try {
                cell.paint(newG)
            } finally {
                newG.dispose()
            }
        }

        if (this.hasOldStyleBorder > 0) {
            // // Paint table border
            //
            // int tableWidth = this.tableWidth;
            // int tableHeight = this.tableHeight;
            // g.setColor(Color.BLACK); //TODO: Actual border color
            // int x = insets.left;
            // int y = insets.top;
            // for(int i = 0; i < border; i++) {
            // g.drawRect(x + i, y + i, tableWidth - i * 2 - 1, tableHeight - i * 2 -
            // 1);
            // }

            // Paint cell borders

            g.color = Color.GRAY
            for (cell in this.ALL_CELLS) {
                val cx = cell.getX() - 1
                val cy = cell.getY() - 1
                val cwidth = cell.getWidth() + 1
                val cheight = cell.getHeight() + 1
                g.drawRect(cx, cy, cwidth, cheight)
            }
        }

        // Paint row group borders
        for (rgsi in rowGroupSizes!!) {
            rgsi.prePaintBorder(g)
        }
    }

    // Called during paint
    private fun prepareRowGroupSizes(): ArrayList<RowGroupSizeInfo> {
        val rowGroupSizes: ArrayList<RowGroupSizeInfo> = ArrayList<RowGroupSizeInfo>()
        run {
            val rowSizesLocal = this.rowSizes
            for (rowGroup in this.ROW_GROUPS) {
                if (rowGroup.rowGroupElem != null) {
                    val firstRow = rowGroup.rows.get(0)
                    val lastRow = rowGroup.rows.get(rowGroup.rows.size - 1)
                    val firstRowSize = rowSizesLocal[firstRow.rowIndex]
                    val lastRowSize = rowSizesLocal[lastRow.rowIndex]

                    val groupHeight =
                        lastRowSize.actualSize + lastRowSize.offsetY - (firstRowSize.offsetY)
                    val groupWidth =
                        this.tableWidth - (firstRowSize.insetRight + firstRowSize.insetLeft)
                    val rRowGroup = RTableRowGroup(
                        this.container, firstRow.rowGroupElem, this.uaContext,
                        rowGroup.borderOverrider
                    )
                    val x = firstRowSize.offsetX + firstRowSize.insetLeft
                    val y = firstRowSize.offsetY
                    rRowGroup.setX(x)
                    rRowGroup.setY(y)
                    rRowGroup.setWidth(groupWidth)
                    rRowGroup.setHeight(groupHeight)
                    rRowGroup.applyStyle(groupWidth, groupHeight, true)
                    val rgsi = RowGroupSizeInfo(groupWidth, groupHeight, rRowGroup, x, y)
                    rowGroupSizes.add(rgsi)
                }
            }
        }
        return rowGroupSizes
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.BoundableRenderable#getRenderablePoint(int,
     * int)
     */
    fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot? {
        for (cell in this.ALL_CELLS) {
            val bounds = cell.getVisualBounds()
            if (bounds.contains(x, y)) {
                val rp = cell.getLowestRenderableSpot(x - bounds.x, y - bounds.y)
                if (rp != null) {
                    return rp
                }
            }
        }
        return null
    }

    // public boolean paintSelection(Graphics g, boolean inSelection,
    // RenderableSpot startPoint, RenderableSpot endPoint) {
    // ArrayList allCells = this.ALL_CELLS;
    // int numCells = allCells.size();
    // for(int i = 0; i < numCells; i++) {
    // RTableCell cell = (RTableCell) allCells.get(i);
    // Rectangle bounds = cell.getBounds();
    // int offsetX = bounds.x;
    // int offsetY = bounds.y;
    // g.translate(offsetX, offsetY);
    // try {
    // boolean newInSelection = cell.paintSelection(g, inSelection, startPoint,
    // endPoint);
    // if(inSelection && !newInSelection) {
    // return false;
    // }
    // inSelection = newInSelection;
    // } finally {
    // g.translate(-offsetX, -offsetY);
    // }
    // }
    // return inSelection;
    // }
    //
    // public boolean extractSelectionText(StringBuffer buffer, boolean
    // inSelection, RenderableSpot startPoint, RenderableSpot endPoint) {
    // ArrayList allCells = this.ALL_CELLS;
    // int numCells = allCells.size();
    // for(int i = 0; i < numCells; i++) {
    // RTableCell cell = (RTableCell) allCells.get(i);
    // boolean newInSelection = cell.extractSelectionText(buffer, inSelection,
    // startPoint, endPoint);
    // if(inSelection && !newInSelection) {
    // return false;
    // }
    // inSelection = newInSelection;
    // }
    // return inSelection;
    // }
    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMouseClick(java.awt.event
     * .MouseEvent, int, int)
     */
    fun onMouseClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        for (cell in this.ALL_CELLS) {
            val bounds = cell.getVisualBounds()
            if (bounds.contains(x, y)) {
                if (!cell.onMouseClick(event, x - bounds.x, y - bounds.y)) {
                    return false
                }
                break
            }
        }
        return true
    }

    fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        for (cell in this.ALL_CELLS) {
            val bounds = cell.getVisualBounds()
            if (bounds.contains(x, y)) {
                if (!cell.onDoubleClick(event, x - bounds.x, y - bounds.y)) {
                    return false
                }
                break
            }
        }
        return true
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMouseDisarmed(java.awt.event
     * .MouseEvent)
     */
    fun onMouseDisarmed(event: MouseEvent?): Boolean {
        val ar = this.armedRenderable
        if (ar != null) {
            this.armedRenderable = null
            return ar.onMouseDisarmed(event)
        } else {
            return true
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMousePressed(java.awt.event
     * .MouseEvent, int, int)
     */
    fun onMousePressed(event: MouseEvent?, x: Int, y: Int): Boolean {
        val allCells = this.ALL_CELLS
        val numCells = allCells.size
        for (i in 0..<numCells) {
            val cell = allCells.get(i)
            val bounds = cell.getVisualBounds()
            if (bounds.contains(x, y)) {
                if (!cell.onMousePressed(event, x - bounds.x, y - bounds.y)) {
                    this.armedRenderable = cell
                    return false
                }
                break
            }
        }
        return true
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMouseReleased(java.awt.event
     * .MouseEvent, int, int)
     */
    fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean {
        val allCells = this.ALL_CELLS
        val numCells = allCells.size
        var found = false
        for (i in 0..<numCells) {
            val cell = allCells.get(i)
            val bounds = cell.getVisualBounds()
            if (bounds.contains(x, y)) {
                found = true
                val oldArmedRenderable = this.armedRenderable
                if ((oldArmedRenderable != null) && (cell !== oldArmedRenderable)) {
                    oldArmedRenderable.onMouseDisarmed(event)
                    this.armedRenderable = null
                }
                if (!cell.onMouseReleased(event, x - bounds.x, y - bounds.y)) {
                    return false
                }
                break
            }
        }
        if (!found) {
            val oldArmedRenderable = this.armedRenderable
            if (oldArmedRenderable != null) {
                oldArmedRenderable.onMouseDisarmed(event)
                this.armedRenderable = null
            }
        }
        return true
    }

    val cells: MutableIterator<RAbstractCell>
        get() = this.ALL_CELLS.iterator()

    val rowGroups: MutableIterator<RTableRowGroup>
        get() = this.rowGroupSizes!!.stream()
            .map<RTableRowGroup> { rgs: RowGroupSizeInfo? -> rgs!!.r }
            .iterator()

    private class RowGroup(rowGroupElem: HTMLElementImpl?) {
        val rows: ArrayList<Row> = ArrayList<Row>()
        val borderOverrider: BorderOverrider = BorderOverrider()
        val rowGroupElem: HTMLElementImpl?

        init {
            this.rowGroupElem = rowGroupElem
        }

        fun add(row: Row) {
            rows.add(row)
            row.rowGroup = this
        }

        fun finish() {
            val numRows = rows.size
            var minCellBorderLeft = -1
            var minCellBorderRight = -1
            for (i in 0..<numRows) {
                val r = rows.get(i)
                val cellBorderLeftMost = r.cellBorderLeftMost
                if ((minCellBorderLeft == -1) || (cellBorderLeftMost < minCellBorderLeft)) {
                    minCellBorderLeft = cellBorderLeftMost
                }
                val cellBorderRightMost = r.cellBorderRightMost
                if ((minCellBorderRight == -1) || (cellBorderRightMost < minCellBorderRight)) {
                    minCellBorderRight = cellBorderRightMost
                }
            }
            val minCellBorderTop = rows.get(0).minCellBorderTop
            val minCellBorderBottom = rows.get(0).minCellBorderBottom

            val groupBorderInsets: Insets? =
                if (rowGroupElem == null) null else getCSSInsets(rowGroupElem.getRenderState())

            if (groupBorderInsets != null) {
                if (groupBorderInsets.top <= minCellBorderTop) {
                    borderOverrider.topOverridden = true
                } else {
                    val firstRow = rows.get(0)
                    for (cell in firstRow.cells) {
                        // TODO: Only override if cells border is less than minCellBorderTop (?)
                        cell.actualCell.borderOverrider.topOverridden = true
                    }
                }

                if (groupBorderInsets.bottom <= minCellBorderBottom) {
                    borderOverrider.bottomOverridden = true
                } else {
                    val lastRow = rows.get(rows.size - 1)
                    for (cell in lastRow.cells) {
                        // TODO: Only override if cells border is less than minCellBorderBottom (?)
                        cell.actualCell.borderOverrider.bottomOverridden = true
                    }
                }

                if (groupBorderInsets.left <= minCellBorderLeft) {
                    borderOverrider.leftOverridden = true
                } else {
                    for (row in rows) {
                        row.leftMostCell!!.actualCell.borderOverrider.leftOverridden = true
                    }
                }
                if (groupBorderInsets.right <= minCellBorderRight) {
                    borderOverrider.rightOverridden = true
                } else {
                    for (row in rows) {
                        row.rightMostCell!!.actualCell.borderOverrider.rightOverridden = true
                    }
                }
            }
        }

        val groupBorderInsets: HtmlInsets?
            get() {
                val borderInfo =
                    if (rowGroupElem == null) null else rowGroupElem.getRenderState()
                        .borderInfo
                return if (borderInfo == null) null else borderOverrider.get(borderInfo.insets)
            }
    }

    private class Row(rowGroup: HTMLElementImpl?) {
        val cells: ArrayList<VirtualCell> = ArrayList<VirtualCell>()
        val rowGroupElem: HTMLElementImpl?

        // TODO: Add getters and make private for the following four
        var firstInGroup: Boolean = false
        var lastInGroup: Boolean = false
        var maxCellBorderTop: Int = 0
        var maxCellBorderBottom: Int = 0
        var rowGroup: RowGroup? = null
        var minCellBorderBottom: Int = -1
        var minCellBorderTop: Int = -1
        var rowIndex: Int = 0

        init {
            this.rowGroupElem = rowGroup
        }

        val leftMostCell: VirtualCell?
            get() = cells.get(0)

        val rightMostCell: VirtualCell?
            get() = cells.get(cells.size - 1)

        val cellBorderRightMost: Int
            get() = getCSSInsets(
                this.leftMostCell!!.actualCell.renderState
            )!!.right

        val cellBorderLeftMost: Int
            get() = getCSSInsets(
                this.leftMostCell!!.actualCell.renderState
            )!!.left

        fun add(cell: VirtualCell?) {
            if (cell != null) {
                val ac = cell.actualCell
                val rs = ac.renderState
                val binfo = rs.borderInfo
                if (binfo != null) {
                    val bi = binfo.insets
                    if (bi != null) {
                        if (bi.top > maxCellBorderTop) {
                            maxCellBorderTop = bi.top
                        }
                        if ((bi.top < minCellBorderTop) || (minCellBorderTop == -1)) {
                            minCellBorderTop = bi.top
                        }
                        if (bi.bottom > maxCellBorderBottom) {
                            maxCellBorderBottom = bi.bottom
                        }
                        if ((bi.bottom < minCellBorderBottom) || (minCellBorderBottom == -1)) {
                            minCellBorderBottom = bi.bottom
                        }
                    }
                }
            }
            cells.add(cell!!)
        }

        fun add(nc: Int, virtualCell: VirtualCell?) {
            cells.add(nc, virtualCell!!)
        }

        fun size(): Int {
            return cells.size
        }

        fun get(c: Int): VirtualCell? {
            return cells.get(c)
        }
    }

    /**
     * A class that helps map elements to children (or their delegates). It automatically takes care of
     * non-existing parents by creating a place holder.
     * For example, helps map table rows to virtual cells (which are delegates for table columns).
     */
    private class TableRelation(listOfRows: ArrayList<Row>, listOfRowGroups: ArrayList<RowGroup>) {
        private val elementToRow: MutableMap<HTMLElementImpl?, Row?> =
            HashMap<HTMLElementImpl?, Row?>(2)
        private val listOfRows: ArrayList<Row>
        private val listOfRowGroups: ArrayList<RowGroup>
        private var currentFallbackRow: Row? = null

        init {
            this.listOfRows = listOfRows
            this.listOfRowGroups = listOfRowGroups
        }

        fun associate(
            rowGroupElem: HTMLElementImpl?,
            rowElem: HTMLElementImpl?,
            cell: VirtualCell?
        ) {
            var row: Row?
            if (rowElem != null) {
                currentFallbackRow = null
                row = elementToRow.get(rowElem)
                if (row == null) {
                    row = createRow(rowGroupElem)
                    elementToRow.put(rowElem, row)
                }
            } else {
                // Doesn't have a parent. Let's add a list just for itself.
                if (currentFallbackRow != null) {
                    row = currentFallbackRow
                } else {
                    row = createRow(rowGroupElem)
                    currentFallbackRow = row
                }
            }
            row!!.add(cell)
        }

        fun createRow(rowGroupElem: HTMLElementImpl?): Row {
            val row = Row(rowGroupElem)
            row.rowIndex = this.listOfRows.size
            this.listOfRows.add(row)
            return row
        }

        fun finish() {
            var prevRowGroupElem: HTMLElementImpl? = null
            var currentRowGroup: RowGroup? = null
            val numRows = listOfRows.size
            for (i in 0..<numRows) {
                val row = listOfRows.get(i)
                row.firstInGroup = (i == 0) || (row.rowGroupElem !== prevRowGroupElem)
                row.lastInGroup =
                    (i == numRows - 1) || (listOfRows.get(i + 1).rowGroupElem !== row.rowGroupElem)
                if (row.firstInGroup) {
                    currentRowGroup = RowGroup(row.rowGroupElem)
                    this.listOfRowGroups.add(currentRowGroup)
                }
                checkNotNull(currentRowGroup)
                currentRowGroup.add(row)
                if (row.lastInGroup) {
                    currentRowGroup.finish()
                }
                prevRowGroupElem = row.rowGroupElem
            }
        }
    }

    class RTableRowGroup(
        container: RenderableContainer?,
        modelNode: ModelNode?,
        ucontext: UserAgentContext?,
        borderOverrider: BorderOverrider
    ) : BaseElementRenderable(container, modelNode, ucontext) {
        init {
            this.borderOverrider.copyFrom(borderOverrider)
        }

        override fun getRenderables(topFirst: Boolean): MutableIterator<out Renderable>? {
            return null
        }

        override val clipBounds: Rectangle?
            get() = TODO("Not yet implemented")
        override val clipBoundsWithoutInsets: Rectangle?
            get() = TODO("Not yet implemented")
        override val bounds: Rectangle?
            get() = TODO("Not yet implemented")
        override val visualBounds: Rectangle?
            get() = TODO("Not yet implemented")
        override val size: Dimension?
            get() = TODO("Not yet implemented")
        override val origin: Point?
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

        override fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot? {
            return null
        }

        override fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean {
            return false
        }

        override fun onMouseDisarmed(event: MouseEvent?): Boolean {
            return false
        }

        override fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
            return false
        }

        override val isContainedByNode: Boolean
            get() = TODO("Not yet implemented")
        override val isDelegated: Boolean
            get() = TODO("Not yet implemented")

        override fun repaint() {
            container?.repaint(x, y, width, height)
        }

        override fun repaint(modelNode: ModelNode?) {
            // TODO Auto-generated method stub
        }

        fun getPaintedBackgroundColor(): Color? {
            // TODO Auto-generated method stub
            return null
        }

        override fun paintShifted(g: Graphics) {
            TODO("Not yet implemented")
        }

        override fun doLayout(availWidth: Int, availHeight: Int, sizeOnly: Boolean) {
            // TODO Auto-generated method stub
        }

        override fun getBorderInsets(): Insets {
            return borderOverrider.get(super.getBorderInsets())
        }

        override val marginTop: Int
            get() = TODO("Not yet implemented")
        override val marginLeft: Int
            get() = TODO("Not yet implemented")
        override val marginBottom: Int
            get() = TODO("Not yet implemented")
        override val marginRight: Int
            get() = TODO("Not yet implemented")
        override val collapsibleMarginTop: Int
            get() = TODO("Not yet implemented")
        override val collapsibleMarginBottom: Int
            get() = TODO("Not yet implemented")
        override val paintedBackgroundColor: Color?
            get() = TODO("Not yet implemented")
        override val parentContainer: RenderableContainer?
            get() = TODO("Not yet implemented")
    }

    class ColSizeInfo {
        var htmlLength: HtmlLength? = null
        var actualSize: Int = 0
        var fullActualSize: Int = 0 // Full size including border and padding
        var layoutSize: Int = 0
        var fullLayoutSize: Int = 0 // Full size including border and padding
        var minSize: Int = 0
        var offsetX: Int = 0
    }

    class RowSizeInfo {
        var insetLeft: Int = 0
        var insetRight: Int = 0
        var htmlLength: HtmlLength? = null
        var actualSize: Int = 0
        var minSize: Int = 0

        var offsetX: Int = 0
        var offsetY: Int = 0

        var marginTop: Int = 0
        var marginBottom: Int = 0
    }

    private class RowGroupSizeInfo(width: Int, height: Int, r: RTableRowGroup, x: Int, y: Int) {
        private val height: Int
        private val width: Int
        private val x: Int
        private val y: Int

        val r: RTableRowGroup

        init {
            this.height = height
            this.width = width
            this.r = r
            this.x = x
            this.y = y
        }


        fun prePaintBackground(g: Graphics) {
            val bi = r.getBorderInsets()
            val rowGroupElem = r.getModelNode()
            r.prePaintBackground(
                g,
                width - (bi.left / 2),
                height,
                x,
                y,
                rowGroupElem,
                rowGroupElem?.renderState(),
                bi
            )
        }

        fun prePaintBorder(g: Graphics) {
            val bi = r.getBorderInsets()
            r.prePaintBorder(
                g,
                width + (bi.left) / 2 + bi.right,
                height + bi.top + bi.bottom,
                x - bi.left,
                y - bi.top,
                bi
            )
        }
    }

    companion object {
        private fun getWidthLength(element: HTMLElementImpl, availWidth: Int): HtmlLength? {
            try {
                val props = element.getCurrentStyle()
                val widthText = props.width
                if (widthText == null) {
                    // TODO: convert attributes to CSS properties
                    val widthAttr = element.getAttribute("width")
                    if (widthAttr == null) {
                        return null
                    }
                    return HtmlLength(
                        HtmlValues.getPixelSize(
                            widthAttr,
                            element.getRenderState(),
                            0,
                            availWidth
                        )
                    )
                } else {
                    return HtmlLength(
                        HtmlValues.getPixelSize(
                            widthText,
                            element.getRenderState(),
                            0,
                            availWidth
                        )
                    )
                }
            } catch (err: NumberFormatException) {
                println("Exception while parsing width: " + err)
                return null
            }
        }

        private fun getHeightLength(element: HTMLElementImpl, availHeight: Int): HtmlLength? {
            try {
                val props = element.getCurrentStyle()
                val heightText = props.height
                if (heightText == null) {
                    val ha = element.getAttribute("height")
                    if (ha == null) {
                        return null
                    } else {
                        return HtmlLength(
                            HtmlValues.getPixelSize(
                                ha,
                                element.getRenderState(),
                                0,
                                availHeight
                            )
                        )
                    }
                } else {
                    return HtmlLength(
                        HtmlValues.getPixelSize(
                            heightText,
                            element.getRenderState(),
                            0,
                            availHeight
                        )
                    )
                }
            } catch (err: NumberFormatException) {
                println("Exception while parsing height: " + err)
                return null
            }
        }

        fun getCSSInsets(rs: RenderState): Insets? {
            val borderInfo = rs.borderInfo
            val elemBorderHtmlInsets = if (borderInfo == null) null else borderInfo.insets
            return if (elemBorderHtmlInsets == null) RBlockViewport.Companion.ZERO_INSETS else elemBorderHtmlInsets.getAWTInsets(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
            )
        }

        /**
         * This method sets the tentative actual sizes of columns (rows) based on
         * specified widths (heights) if available.
         *
         * @param columnSizes
         * @param widthsOfExtras
         * @param cellAvailWidth
         */
        private fun determineTentativeSizes(
            columnSizes: List<ColSizeInfo>, widthsOfExtras: Int, cellAvailWidth: Int,
            setNoWidthColumns: Boolean
        ) {
            val numCols = columnSizes.size

            // Look at percentages first
            var widthUsedByPercent = 0
            for (i in 0..<numCols) {
                val colSizeInfo = columnSizes[i]
                val widthLength = colSizeInfo.htmlLength
                if ((widthLength != null) && (widthLength.lengthType == HtmlLength.LENGTH)) {
                    val actualSizeInt = widthLength.getLength(cellAvailWidth)
                    widthUsedByPercent += actualSizeInt
                    colSizeInfo.actualSize = actualSizeInt
                }
            }

            // Look at columns with absolute sizes
            var widthUsedByAbsolute = 0
            var numNoWidthColumns = 0
            for (i in 0..<numCols) {
                val colSizeInfo = columnSizes[i]
                val widthLength = colSizeInfo.htmlLength
                if ((widthLength != null) && (widthLength.lengthType != HtmlLength.LENGTH)) {
                    // TODO: MULTI-LENGTH not supported
                    val actualSizeInt = widthLength.rawValue
                    widthUsedByAbsolute += actualSizeInt
                    colSizeInfo.actualSize = actualSizeInt
                } else if (widthLength == null) {
                    numNoWidthColumns++
                }
            }

            // Tentative width of all columns without a declared
            // width is set to zero. The pre-render will determine
            // a better size.

            // // Assign all columns without widths now
            // int widthUsedByUnspecified = 0;
            // if(setNoWidthColumns) {
            // int remainingWidth = cellAvailWidth - widthUsedByAbsolute -
            // widthUsedByPercent;
            // if(remainingWidth > 0) {
            // for(int i = 0; i < numCols; i++) {
            // SizeInfo colSizeInfo = columnSizes[i];
            // HtmlLength widthLength = colSizeInfo.htmlLength;
            // if(widthLength == null) {
            // int actualSizeInt = remainingWidth / numNoWidthColumns;
            // widthUsedByUnspecified += actualSizeInt;
            // colSizeInfo.actualSize = actualSizeInt;
            // }
            // }
            // }
            // }

            // Contract if necessary. This is done again later, but this is
            // an optimization, as it may prevent re-layout. It is only done
            // if all columns have some kind of declared width.
            if (numNoWidthColumns == 0) {
                var totalWidthUsed = widthUsedByPercent + widthUsedByAbsolute
                var difference = totalWidthUsed - cellAvailWidth
                // See if absolutes need to be contracted
                if (difference > 0) {
                    if (widthUsedByAbsolute > 0) {
                        var expectedAbsoluteWidthTotal = widthUsedByAbsolute - difference
                        if (expectedAbsoluteWidthTotal < 0) {
                            expectedAbsoluteWidthTotal = 0
                        }
                        val ratio = expectedAbsoluteWidthTotal.toDouble() / widthUsedByAbsolute
                        for (i in 0..<numCols) {
                            val sizeInfo = columnSizes[i]
                            val widthLength = columnSizes[i].htmlLength
                            if ((widthLength != null) && (widthLength.lengthType != HtmlLength.LENGTH)) {
                                val oldActualSize = sizeInfo.actualSize
                                val newActualSize = Math.round(oldActualSize * ratio).toInt()
                                sizeInfo.actualSize = newActualSize
                                totalWidthUsed += (newActualSize - oldActualSize)
                            }
                        }
                        difference = totalWidthUsed - cellAvailWidth
                    }

                    // See if percentages need to be contracted
                    if (difference > 0) {
                        if (widthUsedByPercent > 0) {
                            var expectedPercentWidthTotal = widthUsedByPercent - difference
                            if (expectedPercentWidthTotal < 0) {
                                expectedPercentWidthTotal = 0
                            }
                            val ratio = expectedPercentWidthTotal.toDouble() / widthUsedByPercent
                            for (i in 0..<numCols) {
                                val sizeInfo = columnSizes[i]
                                val widthLength = columnSizes[i].htmlLength
                                if ((widthLength != null) && (widthLength.lengthType == HtmlLength.LENGTH)) {
                                    val oldActualSize = sizeInfo.actualSize
                                    val newActualSize = Math.round(oldActualSize * ratio).toInt()
                                    sizeInfo.actualSize = newActualSize
                                    totalWidthUsed += (newActualSize - oldActualSize)
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Expands column sizes according to layout sizes.
         */
        private fun adjustForLayoutWidths(
            columnSizes: List<ColSizeInfo>, hasBorder: Int, cellSpacing: Int,
            tableWidthKnown: Boolean
        ) {
            val numCols = columnSizes.size
            for (i in 0..<numCols) {
                val si = columnSizes[i]
                if (si.actualSize < si.layoutSize) {
                    si.actualSize = si.layoutSize
                }
                if (si.fullActualSize < si.fullLayoutSize) {
                    si.fullActualSize = si.fullLayoutSize
                }
                // else if(si.htmlLength == null) {
                // // For cells without a declared width, see if
                // // their tentative width is a bit too big.
                // if(si.actualSize > si.layoutSize) {
                // si.actualSize = si.layoutSize;
                // }
                // }
            }
        }
    }
}
