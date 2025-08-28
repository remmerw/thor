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

import kotlin.math.max

internal class CombinedFloatingBounds(
    private val floatBounds1: FloatingBounds,
    private val floatBounds2: FloatingBounds
) : FloatingBounds {
    override fun getClearY(y: Int): Int {
        return max(this.floatBounds1.getClearY(y), this.floatBounds2.getClearY(y))
    }

    override fun getFirstClearY(y: Int): Int {
        return max(this.floatBounds1.getFirstClearY(y), this.floatBounds2.getFirstClearY(y))
    }

    override fun getLeft(y: Int): Int {
        return max(this.floatBounds1.getLeft(y), this.floatBounds2.getLeft(y))
    }

    override fun getLeftClearY(y: Int): Int {
        return max(this.floatBounds1.getLeftClearY(y), this.floatBounds2.getLeftClearY(y))
    }

    override fun getMaxY(): Int {
        return max(this.floatBounds1.getMaxY(), this.floatBounds2.getMaxY())
    }

    override fun getRight(y: Int): Int {
        return max(this.floatBounds1.getRight(y), this.floatBounds2.getRight(y))
    }

    override fun getRightClearY(y: Int): Int {
        return max(this.floatBounds1.getRightClearY(y), this.floatBounds2.getRightClearY(y))
    }

    override fun equals(obj: Any?): Boolean {
        // Important for layout caching.
        if (obj !is CombinedFloatingBounds) {
            return false
        }
        return obj.floatBounds1 == this.floatBounds1 && obj.floatBounds2 == this.floatBounds2
    }

    override fun hashCode(): Int {
        val fbounds1 = this.floatBounds1
        val fbounds2 = this.floatBounds2
        return (if (fbounds1 == null) 0 else fbounds1.hashCode()) xor (if (fbounds2 == null) 0 else fbounds2.hashCode())
    }
}
