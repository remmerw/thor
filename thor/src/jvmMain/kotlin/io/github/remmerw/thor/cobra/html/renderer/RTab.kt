/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2015 Uproot Labs India Pvt Ltd

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

 */
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import java.awt.FontMetrics

class RTab(
    me: ModelNode?,
    container: RenderableContainer?,
    fontMetrics: FontMetrics,
    descent: Int,
    ascentPlusLeading: Int,
    height: Int,
    numSpaces: Int
) : RWord(me, "\t", container, fontMetrics, descent, ascentPlusLeading, height, 0) {
    init {
        this.setWidth(fontMetrics.charWidth(' ') * numSpaces)
    }

    override fun extractSelectionText(
        buffer: StringBuffer,
        inSelection: Boolean,
        startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean {
        var startX = -1
        var endX = -1
        if (this === startPoint.renderable) {
            startX = startPoint.x
        }
        if (this === endPoint.renderable) {
            endX = endPoint.x
        }
        if (!inSelection && (startX == -1) && (endX == -1)) {
            return false
        }
        if ((startX != -1) && (endX != -1)) {
            if (endX < startX) {
                val temp = startX
                startX = endX
                endX = temp
            }
        } else if ((startX != -1) && (endX == -1) && inSelection) {
            endX = startX
            startX = -1
        } else if ((startX == -1) && (endX != -1) && !inSelection) {
            startX = endX
            endX = -1
        }
        var index1 = -1
        var index2 = -1
        if (startX != -1) {
            index1 = 0
        }
        if (endX != -1) {
            index2 = 0
        }
        if ((index1 != -1) || (index2 != -1)) {
            if (index2 == -1) {
                buffer.append('\t')
            }
        } else {
            if (inSelection) {
                buffer.append('\t')
                return true
            }
        }
        if ((index1 != -1) && (index2 != -1)) {
            return false
        } else {
            return !inSelection
        }
    }

    override fun zIndex(): Int {
        TODO("Not yet implemented")
    }
}
