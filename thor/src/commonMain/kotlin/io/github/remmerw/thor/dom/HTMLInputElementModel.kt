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
import org.w3c.dom.html.HTMLInputElement

class HTMLInputElementModel(name: String) : HTMLBaseInputElement(name), HTMLInputElement {
    private var defaultChecked = false


    override fun getDefaultChecked(): Boolean {
        return this.defaultChecked
    }

    override fun setDefaultChecked(defaultChecked: Boolean) {
        this.defaultChecked = defaultChecked
    }

    override fun getForm(): HTMLFormElement? {
        TODO("Not yet implemented")
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

    override fun getChecked(): Boolean {
        return this.getAttributeAsBoolean("checked")
    }

    override fun setChecked(checked: Boolean) {
        this.setAttribute("checked", checked.toString())
    }

    override fun getDisabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDisabled(p0: Boolean) {
        TODO("Not yet implemented")
    }


    override fun getMaxLength(): Int {
        TODO("Not yet implemented")
    }

    override fun setMaxLength(maxLength: Int) {
        TODO("Not yet implemented")
    }

    override fun getReadOnly(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setReadOnly(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getSize(): String {
        TODO("Not yet implemented")
    }

    override fun setSize(size: String) {
        TODO("Not yet implemented")
    }

    override fun getSrc(): String? {
        return this.getAttribute("src")
    }

    override fun setSrc(src: String?) {
        this.setAttribute("src", src)
    }

    override fun getTabIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun setTabIndex(p0: Int) {
        TODO("Not yet implemented")
    }


    override fun getType(): String? {
        return this.getAttribute("type")
    }


    override fun getUseMap(): String? {
        return this.getAttribute("usemap")
    }

    override fun setUseMap(useMap: String?) {
        this.setAttribute("usemap", useMap)
    }

    override fun getValue(): String? {
        TODO("Not yet implemented")
    }

    override fun setValue(p0: String?) {
        TODO("Not yet implemented")
    }


    override fun click() {
        TODO("Not yet implemented")
    }


    override fun getFormInputs(): Array<FormInput>? {
        val type = this.type
        val name = this.name
        if (name == null) {
            return null
        }
        if (type == null) {
            return arrayOf<FormInput>(FormInput(name, this.value))
        } else {
            if ("text" == type || "password" == type || "hidden" == type || "url" == type || "number" == type || "search" == type || "" == type) {
                return arrayOf<FormInput>(FormInput(name, this.value))
            } else if ("submit" == type) {
                // It's done as an "extra" form input
                return null
            } else if ("radio" == type || "checkbox" == type) {
                if (this.checked) {
                    var value = this.value
                    if ((value == null) || (value.length == 0)) {
                        value = "on"
                    }
                    return arrayOf<FormInput>(FormInput(name, value))
                } else {
                    return null
                }
            } else if ("image" == type) {
                // It's done as an "extra" form input
                return null
            } else if ("file" == type) {
                TODO("Not yet implemented")
            } else {
                return null
            }
        }
    }
}
