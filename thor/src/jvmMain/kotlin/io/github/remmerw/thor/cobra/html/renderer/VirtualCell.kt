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

import io.github.remmerw.thor.cobra.html.style.HtmlLength
import io.github.remmerw.thor.cobra.html.style.HtmlValues

class VirtualCell(cell: RAbstractCell, isTopLeft: Boolean) {
    /**
     * @return Returns the actualCell.
     */
    val actualCell: RAbstractCell
    val isTopLeft: Boolean
    /**
     * @return Returns the column.
     */
    /**
     * @param column The column to set.
     */
    var column: Int = 0
    /**
     * @return Returns the row.
     */
    /**
     * @param row The row to set.
     */
    var row: Int = 0

    /**
     * @param cell
     */
    init {
        actualCell = cell
        this.isTopLeft = isTopLeft
    }

    val heightLength: HtmlLength?
        get() {
            // TODO: Does not consider cellpadding and border
            val cell = this.actualCell
            val heightText = cell.getHeightText()
            var length: HtmlLength?
            try {
                length = if (heightText == null) null else HtmlLength(
                    HtmlValues.getPixelSize(
                        heightText,
                        cell.modelNode()?.renderState(),
                        0
                    )
                )
            } catch (err: NumberFormatException) {
                length = null
            }
            if (length != null) {
                length.divideBy(cell.getRowSpan())
            }
            return length
        }

    val widthLength: HtmlLength?
        get() {
            val cell = this.actualCell
            val widthText = cell.getWidthText()
            var length: HtmlLength?
            try {
                length = if (widthText == null) null else HtmlLength(
                    HtmlValues.getPixelSize(
                        widthText,
                        cell.modelNode()?.renderState(),
                        0
                    )
                )
            } catch (err: NumberFormatException) {
                length = null
            }
            if (length != null) {
                length.divideBy(cell.getColSpan())
            }
            return length
        }
}
