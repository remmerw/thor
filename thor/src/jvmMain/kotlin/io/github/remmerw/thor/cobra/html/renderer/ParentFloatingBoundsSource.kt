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

class ParentFloatingBoundsSource(
    private val blockShiftRight: Int,
    private val expectedBlockWidth: Int,
    private val newX: Int,
    private val newY: Int,
    private val floatBounds: FloatingBounds
) : FloatingBoundsSource {
    override fun getChildBlockFloatingBounds(apparentBlockWidth: Int): FloatingBounds {
        val actualRightShift = this.blockShiftRight + (this.expectedBlockWidth - apparentBlockWidth)
        return ShiftedFloatingBounds(this.floatBounds, -this.newX, -actualRightShift, -this.newY)
    }

    override fun equals(obj: Any?): Boolean {
        // Important for layout caching.
        if (obj !is ParentFloatingBoundsSource) {
            return false
        }
        return (this.blockShiftRight == obj.blockShiftRight) && (this.expectedBlockWidth == obj.expectedBlockWidth)
                && (this.newX == obj.newX)
                && (this.newY == obj.newY) && this.floatBounds == obj.floatBounds
    }

    override fun hashCode(): Int {
        return this.newX xor this.newY xor this.blockShiftRight xor this.expectedBlockWidth
    }
}
