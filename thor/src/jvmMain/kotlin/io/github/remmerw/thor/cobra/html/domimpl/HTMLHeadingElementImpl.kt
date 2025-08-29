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
package io.github.remmerw.thor.cobra.html.domimpl

import org.w3c.dom.html.HTMLHeadingElement

class HTMLHeadingElementImpl(name: String) : HTMLAbstractUIElement(name), HTMLHeadingElement {
    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

    override fun appendInnerTextImpl(buffer: StringBuffer) {
        val length = buffer.length
        var lineBreaks: Int
        if (length == 0) {
            lineBreaks = 2
        } else {
            var start = length - 4
            if (start < 0) {
                start = 0
            }
            lineBreaks = 0
            for (i in start..<length) {
                val ch = buffer.get(i)
                if (ch == '\n') {
                    lineBreaks++
                }
            }
        }
        for (i in 0..<(2 - lineBreaks)) {
            buffer.append("\r\n")
        }
        super.appendInnerTextImpl(buffer)
        buffer.append("\r\n\r\n")
    }
}
