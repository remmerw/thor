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
package io.github.remmerw.thor.cobra.html.style

import java.awt.Insets

class HtmlInsets {
    var top: Int = 0
    var bottom: Int = 0
    var left: Int = 0
    var right: Int = 0

    /* Types assumed to be initialized as UNDEFINED */
    var topType: Int = 0
    var bottomType: Int = 0
    var leftType: Int = 0
    var rightType: Int = 0

    constructor()

    constructor(top: Int, left: Int, bottom: Int, right: Int, type: Int) {
        this.top = top
        this.left = left
        this.bottom = bottom
        this.right = right
        this.topType = type
        this.leftType = type
        this.bottomType = type
        this.rightType = type
    }

    fun getAWTInsets(
        defaultTop: Int, defaultLeft: Int, defaultBottom: Int, defaultRight: Int,
        availWidth: Int,
        availHeight: Int, autoX: Int, autoY: Int
    ): Insets {
        val top: Int = getInsetPixels(this.top, this.topType, defaultTop, availHeight, autoY)
        val left: Int = getInsetPixels(this.left, this.leftType, defaultLeft, availWidth, autoX)
        val bottom: Int =
            getInsetPixels(this.bottom, this.bottomType, defaultBottom, availHeight, autoY)
        val right: Int = getInsetPixels(this.right, this.rightType, defaultRight, availWidth, autoX)
        return Insets(top, left, bottom, right)
    }

    fun getSimpleAWTInsets(availWidth: Int, availHeight: Int): Insets {
        val top: Int = getInsetPixels(this.top, this.topType, 0, availHeight, 0)
        val left: Int = getInsetPixels(this.left, this.leftType, 0, availWidth, 0)
        val bottom: Int = getInsetPixels(this.bottom, this.bottomType, 0, availHeight, 0)
        val right: Int = getInsetPixels(this.right, this.rightType, 0, availWidth, 0)
        return Insets(top, left, bottom, right)
    }

    override fun toString(): String {
        return "[" + this.top + "," + this.left + "," + this.bottom + "," + this.right + "]"
    }

    companion object {
        const val TYPE_UNDEFINED: Int = 0
        const val TYPE_PIXELS: Int = 1
        const val TYPE_AUTO: Int = 2
        const val TYPE_PERCENT: Int = 3

        private fun getInsetPixels(
            value: Int,
            type: Int,
            defaultValue: Int,
            availSize: Int,
            autoValue: Int
        ): Int {
            if (type == TYPE_PIXELS) {
                return value
            } else if (type == TYPE_UNDEFINED) {
                return defaultValue
            } else if (type == TYPE_AUTO) {
                return autoValue
            } else if (type == TYPE_PERCENT) {
                return (availSize * value) / 100
            } else {
                throw IllegalStateException()
            }
        }
    }
}
