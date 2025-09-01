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
 * Created on Feb 12, 2006
 */
package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLLIElement

class HTMLLIElementImpl(name: String) : HTMLAbstractUIElement(name), HTMLLIElement {
    override fun getType(): String? {
        return this.getAttribute("type")
    }

    override fun setType(type: String?) {
        this.setAttribute("type", type)
    }

    override fun getValue(): Int {
        val valueText = this.getAttribute("value")
        if (valueText == null) {
            return 0
        }
        try {
            return valueText.toInt()
        } catch (nfe: NumberFormatException) {
            return 0
        }
    }

    override fun setValue(value: Int) {
        this.setAttribute("value", value.toString())
    }
}
