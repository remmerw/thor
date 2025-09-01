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
package io.github.remmerw.thor.style

import java.awt.Color

class BorderInfo {
    var topStyle: Int = 0
    var leftStyle: Int = 0
    var bottomStyle: Int = 0
    var rightStyle: Int = 0

    var topColor: Color? = null
    var leftColor: Color? = null
    var bottomColor: Color? = null
    var rightColor: Color? = null

    var insets: HtmlInsets? = null

    override fun toString(): String {
        return ("BorderInfo [topStyle=" + topStyle + ", leftStyle=" + leftStyle + ", bottomStyle=" + bottomStyle + ", rightStyle=" + rightStyle
                + ", topColor=" + topColor + ", leftColor=" + leftColor + ", bottomColor=" + bottomColor + ", rightColor=" + rightColor
                + ", insets=" + insets + "]")
    }
}
