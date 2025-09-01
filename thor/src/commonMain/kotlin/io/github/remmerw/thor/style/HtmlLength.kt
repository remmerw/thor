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
 * Created on Nov 19, 2005
 */
package io.github.remmerw.thor.style

import kotlin.concurrent.Volatile

class HtmlLength {
    /**
     * @return Returns the lengthType.
     */
    val lengthType: Int

    /**
     * @return Returns the spec.
     */
    @Volatile
    var rawValue: Int = 0
        private set

    constructor(spec: String) {
        var spec = spec
        spec = spec.trim { it <= ' ' }
        val length = spec.length
        val lastChar = spec.get(length - 1)
        val parseable: String?
        if (spec.endsWith("px")) {
            this.lengthType = PIXELS
            parseable = spec.substring(0, length - 2)
        } else if (lastChar == '%') {
            this.lengthType = LENGTH
            parseable = spec.substring(0, length - 1).trim { it <= ' ' }
        } else if (lastChar == '*') {
            this.lengthType = MULTI_LENGTH
            if (length <= 1) {
                parseable = "1"
            } else {
                parseable = spec.substring(0, length - 1).trim { it <= ' ' }
            }
        } else {
            this.lengthType = PIXELS
            parseable = spec
        }
        try {
            this.rawValue = parseable.toInt()
        } catch (nfe: NumberFormatException) {
            this.rawValue = parseable.toDouble().toInt()
        }
    }

    constructor(pixels: Int) {
        this.lengthType = PIXELS
        this.rawValue = pixels
    }

    fun getLength(availLength: Int): Int {
        val lt = this.lengthType
        if (lt == LENGTH) {
            return (availLength * this.rawValue) / 100
        } else {
            return this.rawValue
        }
    }

    fun divideBy(denominator: Int) {
        var `val` = this.rawValue
        `val` = `val` / denominator
        this.rawValue = `val`
    }

    fun isPreferredOver(otherLength: HtmlLength?): Boolean {
        if (otherLength == null) {
            return true
        }
        if (this.lengthType > otherLength.lengthType) {
            return true
        }
        return this.rawValue > otherLength.rawValue
    }

    companion object {
        // Note: Preferred type has higher value
        const val PIXELS: Int = 1
        const val LENGTH: Int = 2
        const val MULTI_LENGTH: Int = 0

        val EMPTY_ARRAY: Array<HtmlLength?> = arrayOfNulls<HtmlLength>(0)
    }
}
