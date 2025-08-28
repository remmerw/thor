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
package io.github.remmerw.thor.cobra.html.style

import java.awt.Image

class ListStyle {
    var type: Int = 0
    var image: Image? = null
    var position: Int = 0

    constructor(type: Int, image: Image?, position: Int) : super() {
        this.type = type
        this.image = image
        this.position = position
    }

    constructor()

    companion object {
        const val TYPE_UNSET: Int = 256
        const val TYPE_NONE: Int = 0
        const val TYPE_DISC: Int = 1
        const val TYPE_CIRCLE: Int = 2
        const val TYPE_SQUARE: Int = 3
        const val TYPE_DECIMAL: Int = 4
        const val TYPE_LOWER_ALPHA: Int = 5
        const val TYPE_UPPER_ALPHA: Int = 6
        const val TYPE_LOWER_LATIN: Int = 7
        const val TYPE_UPPER_LATIN: Int = 8

        const val POSITION_UNSET: Int = 0
        const val POSITION_INSIDE: Int = 0
        const val POSITION_OUTSIDE: Int = 0
    }
}
