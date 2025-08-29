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
import org.w3c.dom.html.HTMLFormElement
import org.w3c.dom.html.HTMLTextAreaElement

class HTMLTextAreaElementImpl : HTMLBaseInputElement, HTMLTextAreaElement {
    constructor(name: String) : super(name)

    constructor() : super("TEXTAREA")

    protected fun getFormInputs(): Array<FormInput>? {
        val name = this.name
        if (name == null) {
            return null
        }
        return arrayOf<FormInput>(FormInput(name, this.value))
    }

    override fun getDefaultValue(): String? {
        TODO("Not yet implemented")
    }

    override fun setDefaultValue(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getForm(): HTMLFormElement? {
        TODO("Not yet implemented")
    }

    override fun getAccessKey(): String? {
        TODO("Not yet implemented")
    }

    override fun setAccessKey(p0: String?) {
        TODO("Not yet implemented")
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.html2.HTMLTextAreaElement#getCols()
     */
    override fun getCols(): Int {
        val ic = this.inputContext
        return if (ic == null) 0 else ic.cols
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.html2.HTMLTextAreaElement#setCols(int)
     */
    override fun setCols(cols: Int) {
        val ic = this.inputContext
        if (ic != null) {
            ic.cols = (cols)
        }
    }

    override fun getDisabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDisabled(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getName(): String? {
        TODO("Not yet implemented")
    }

    override fun setName(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getReadOnly(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setReadOnly(p0: Boolean) {
        TODO("Not yet implemented")
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.html2.HTMLTextAreaElement#getRows()
     */
    override fun getRows(): Int {
        val ic = this.inputContext
        return if (ic == null) 0 else ic.rows
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.html2.HTMLTextAreaElement#setRows(int)
     */
    override fun setRows(rows: Int) {
        val ic = this.inputContext
        if (ic != null) {
            ic.rows = (rows)
        }
    }

    override fun getTabIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun setTabIndex(p0: Int) {
        TODO("Not yet implemented")
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.html2.HTMLTextAreaElement#getType()
     */
    override fun getType(): String {
        return "textarea"
    }

    override fun getValue(): String? {
        TODO("Not yet implemented")
    }

    override fun setValue(p0: String?) {
        TODO("Not yet implemented")
    }
}
