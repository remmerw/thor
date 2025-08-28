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

class LineBreak(val breakType: Int) {
    companion object {
        const val NONE: Int = 0
        const val LEFT: Int = 1
        const val RIGHT: Int = 2
        const val ALL: Int = 3

        fun getBreakType(clearAttr: String?): Int {
            if (clearAttr == null) {
                return NONE
            } else if ("all".equals(clearAttr, ignoreCase = true)) {
                return ALL
            } else if ("left".equals(clearAttr, ignoreCase = true)) {
                return LEFT
            } else if ("right".equals(clearAttr, ignoreCase = true)) {
                return RIGHT
            } else {
                return NONE
            }
        }
    }
}
