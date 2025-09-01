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
 * Created on Jan 15, 2006
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.FormInput
import org.w3c.dom.html.HTMLTextAreaElement

class HTMLTextAreaElementImpl : HTMLBaseInputElement, HTMLTextAreaElement {
    constructor(name: String) : super(name)

    override fun getFormInputs(): Array<FormInput>? {
        val name = this.name
        if (name == null) {
            return null
        }
        return arrayOf<FormInput>(FormInput(name, this.value))
    }


    override fun getCols(): Int {
        val ic = this.getInputContext()
        return if (ic == null) 0 else ic.getCols()
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.html2.HTMLTextAreaElement#setCols(int)
     */
    override fun setCols(cols: Int) {
        val ic = this.getInputContext()
        if (ic != null) {
            ic.setCols(cols)
        }
    }

    override fun getRows(): Int {
        val ic = this.getInputContext()
        return if (ic == null) 0 else ic.getRows()
    }


    override fun setRows(rows: Int) {
        val ic = this.getInputContext()
        if (ic != null) {
            ic.setRows(rows)
        }
    }

    override fun getType(): String {
        return "textarea"
    }

}
