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

internal class FloatingViewportBounds
/**
 * @param prevBounds
 * @param leftFloat
 * @param y
 * @param offsetFromBorder Width of floating box, including padding insets.
 * @param height
 */(
    private val prevBounds: FloatingBounds?,
    private val leftFloat: Boolean,
    private val y: Int,
    private val offsetFromBorder: Int,
    private val height: Int
) : FloatingBounds {
    override fun getLeft(y: Int): Int {
        var left = 0
        if (this.leftFloat && (y >= this.y) && (y < (this.y + height))) {
            left = this.offsetFromBorder
        }
        val prev = this.prevBounds
        if (prev != null) {
            val newLeft = prev.getLeft(y)
            if (newLeft > left) {
                left = newLeft
            }
        }
        return left
    }

    /**
     * The offset from the right edge, not counting padding.
     */
    override fun getRight(y: Int): Int {
        var right = 0
        if (!this.leftFloat && (y >= this.y) && (y < (this.y + this.height))) {
            right = this.offsetFromBorder
        }
        val prev = this.prevBounds
        if (prev != null) {
            val newRight = prev.getRight(y)
            if (newRight > right) {
                right = newRight
            }
        }
        return right
    }

    override fun getClearY(y: Int): Int {
        var cleary = max(y, this.y + this.height)
        val prev = this.prevBounds
        if (prev != null) {
            val pcy = prev.getClearY(y)
            if (pcy > cleary) {
                cleary = pcy
            }
        }
        return cleary
    }

    override fun getFirstClearY(y: Int): Int {
        var clearY = y
        val prev = this.prevBounds
        if (prev != null) {
            val prevClearY = prev.getFirstClearY(y)
            if (prevClearY != y) {
                clearY = prevClearY
            }
        }
        if ((clearY == y) && (y >= this.y) && (y < (this.y + this.height))) {
            clearY = this.y + this.height
        }
        return clearY
    }

    override fun getLeftClearY(y: Int): Int {
        var cleary: Int
        if (this.leftFloat) {
            cleary = max(y, this.y + this.height)
        } else {
            cleary = y
        }
        val prev = this.prevBounds
        if (prev != null) {
            val pcy = prev.getLeftClearY(y)
            if (pcy > cleary) {
                cleary = pcy
            }
        }
        return cleary
    }

    override fun getRightClearY(y: Int): Int {
        var cleary: Int
        if (!this.leftFloat) {
            cleary = max(y, this.y + this.height)
        } else {
            cleary = y
        }
        val prev = this.prevBounds
        if (prev != null) {
            val pcy = prev.getLeftClearY(y)
            if (pcy > cleary) {
                cleary = pcy
            }
        }
        return cleary
    }

    override val maxY: Int
        get() = TODO("Not yet implemented")

    fun getMaxY(): Int {
        var maxY = this.y + this.height
        val prev = this.prevBounds
        if (prev != null) {
            val prevMaxY = prev.maxY
            if (prevMaxY > maxY) {
                maxY = prevMaxY
            }
        }
        return maxY
    }

    override fun equals(other: Any?): Boolean {
        // Important for layout caching.
        if (other === this) {
            return true
        }
        if (other !is FloatingViewportBounds) {
            return false
        }
        return (other.leftFloat == this.leftFloat) && (other.y == this.y) && (other.height == this.height)
                && (other.offsetFromBorder == this.offsetFromBorder)
                && other.prevBounds == this.prevBounds
    }

    override fun hashCode(): Int {
        return (if (this.leftFloat) 1 else 0) xor this.y xor this.height xor this.offsetFromBorder
    }
}
