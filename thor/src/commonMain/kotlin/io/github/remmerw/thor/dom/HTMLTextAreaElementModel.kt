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
package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLFormElement
import org.w3c.dom.html.HTMLTextAreaElement

class HTMLTextAreaElementModel(name: String) : HTMLElementModel(name), HTMLTextAreaElement {

    override fun getDefaultValue(): String? {
        return this.getAttribute("defaultValue")
    }

    override fun setDefaultValue(defaultValue: String?) {
        this.setAttribute("defaultValue", defaultValue)
    }


    override fun getAccessKey(): String? {
        return this.getAttribute("accessKey")
    }

    override fun setAccessKey(accept: String?) {
        this.setAttribute("accessKey", accept)
    }


    override fun getName(): String? {
        return this.getAttribute("name") // TODO: Should this return value of "id"?
    }

    override fun setName(name: String?) {
        this.setAttribute("name", name) // TODO: Should this return value of "id"?
    }

    override fun blur() {
        TODO("Not yet implemented")
    }

    override fun focus() {
        TODO("Not yet implemented")
    }

    override fun select() {
        TODO("Not yet implemented")
    }

    override fun getForm(): HTMLFormElement? {
        TODO("Not yet implemented")
    }

    override fun getCols(): Int {
        TODO("Not yet implemented")
    }

    override fun setCols(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getDisabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDisabled(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getReadOnly(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setReadOnly(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getRows(): Int {
        TODO("Not yet implemented")
    }

    override fun setRows(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getTabIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun setTabIndex(p0: Int) {
        TODO("Not yet implemented")
    }

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
