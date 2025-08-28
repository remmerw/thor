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

import java.awt.Color
import java.net.URL

class BackgroundInfo {
    var backgroundColor: Color? = null
    var backgroundImage: URL? = null
    var backgroundXPositionAbsolute: Boolean = false
    var backgroundXPosition: Int = 0
    var backgroundYPositionAbsolute: Boolean = false
    var backgroundYPosition: Int = 0
    var backgroundRepeat: Int = BR_REPEAT

    override fun toString(): String {
        return ("BackgroundInfo [color=" + backgroundColor + ", img=" + backgroundImage + ", xposAbs="
                + backgroundXPositionAbsolute + ", xpos=" + backgroundXPosition + ", yposAbs="
                + backgroundYPositionAbsolute + ", ypos=" + backgroundYPosition + ", repeat=" + backgroundRepeat + "]")
    }

    companion object {
        const val BR_REPEAT: Int = 0
        const val BR_NO_REPEAT: Int = 1
        const val BR_REPEAT_X: Int = 2
        const val BR_REPEAT_Y: Int = 3
    }
}
