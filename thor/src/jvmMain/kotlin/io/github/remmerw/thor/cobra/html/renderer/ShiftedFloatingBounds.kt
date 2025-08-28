/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The XAMJ Project

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

internal class ShiftedFloatingBounds(
    prevBounds: FloatingBounds,
    shiftLeft: Int,
    shiftRight: Int,
    shiftY: Int
) : FloatingBounds {
    private val prevBounds: FloatingBounds
    private val shiftLeft: Int
    private val shiftRight: Int
    private val shiftY: Int

    /**
     * Constructs the ShiftedFloatingBounds. Floatinb bounds moved up the
     * hierarchy of renderables will generally have positive shifts.
     *
     * @param prevBounds The baseline floating bounds.
     * @param shiftX     How much the original bounds have shifted in the X axis.
     * @param shiftY     How much the original bounds have shifted in the Y axis.
     */
    init {
        this.prevBounds = prevBounds
        this.shiftLeft = shiftLeft
        this.shiftRight = shiftRight
        this.shiftY = shiftY
    }

    override fun getClearY(y: Int): Int {
        return this.prevBounds.getClearY(y - this.shiftY) + this.shiftY
    }

    override fun getFirstClearY(y: Int): Int {
        return this.prevBounds.getFirstClearY(y - this.shiftY) + this.shiftY
    }

    override fun getLeft(y: Int): Int {
        return this.prevBounds.getLeft(y - this.shiftY) + this.shiftLeft
    }

    override fun getLeftClearY(y: Int): Int {
        return this.prevBounds.getLeftClearY(y - this.shiftY) + this.shiftY
    }

    override fun getRight(y: Int): Int {
        return this.prevBounds.getRight(y - this.shiftY) + this.shiftRight
    }

    override fun getRightClearY(y: Int): Int {
        return this.prevBounds.getRightClearY(y - this.shiftY) + this.shiftY
    }

    override fun getMaxY(): Int {
        return this.prevBounds.getMaxY() + this.shiftY
    }

    override fun equals(obj: Any?): Boolean {
        // Important for layout caching.
        if (obj !is ShiftedFloatingBounds) {
            return false
        }
        return (this.shiftY == obj.shiftY) && (this.shiftLeft == obj.shiftLeft) && (this.shiftRight == obj.shiftRight)
                && this.prevBounds == obj.prevBounds
    }

    override fun hashCode(): Int {
        return this.shiftY xor this.shiftLeft xor this.shiftRight
    }
}
