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
import org.w3c.dom.html.HTMLInputElement
import java.util.Locale
import java.util.logging.Level

class HTMLInputElementImpl(name: String) : HTMLBaseInputElement(name), HTMLInputElement {
    private var defaultChecked = false

    override fun getDefaultChecked(): Boolean {
        return this.defaultChecked
    }

    override fun setDefaultChecked(defaultChecked: Boolean) {
        this.defaultChecked = defaultChecked
    }

    override fun getChecked(): Boolean {
        val ic = this.inputContext
        if (ic == null) {
            return this.getAttributeAsBoolean("checked")
        } else {
            return ic.checked
        }
    }

    override fun setChecked(checked: Boolean) {
        val ic = this.inputContext
        if (ic != null) {
            ic.checked = (checked)
        }
    }

    override fun getMaxLength(): Int {
        val ic = this.inputContext
        return if (ic == null) 0 else ic.maxLength
    }

    override fun setMaxLength(maxLength: Int) {
        val ic = this.inputContext
        if (ic != null) {
            ic.maxLength = (maxLength)
        }
    }

    /* public int getSize() {
    final InputContext ic = this.inputContext;
    return ic == null ? 0 : ic.getControlSize();

  public void setSize(final int size) {
    final InputContext ic = this.inputContext;
    if (ic != null) {
      ic.setControlSize(size);
    }
  }
  }*/
    override fun getSize(): String {
        val ic = this.inputContext
        val size = if (ic == null) 0 else ic.controlSize
        return size.toString()
    }

    override fun setSize(size: String) {
        val ic = this.inputContext
        if (ic != null) {
            ic.controlSize = (size.toInt())
        }
    }

    override fun getSrc(): String? {
        return this.getAttribute("src")
    }

    override fun setSrc(src: String?) {
        this.setAttribute("src", src)
    }

    /**
     * Gets input type in lowercase.
     */
    override fun getType(): String? {
        val type = this.getAttribute("type")
        return if (type == null) null else type.lowercase(Locale.getDefault())
    }

    fun setType(type: String?) {
        this.setAttribute("type", type)
    }

    override fun getUseMap(): String? {
        return this.getAttribute("usemap")
    }

    override fun setUseMap(useMap: String?) {
        this.setAttribute("usemap", useMap)
    }

    override fun click() {
        val ic = this.inputContext
        if (ic != null) {
            ic.click()
        }
    }

    val isSubmittableWithEnterKey: Boolean
        get() {
            val type = this.getType()
            return ((type == null) || "" == type || "text" == type || "password" == type)
        }

    val isSubmittableWithPress: Boolean
        get() {
            val type = this.getType()
            return "submit" == type || "image" == type
        }

    val isSubmitInput: Boolean
        get() {
            val type = this.getType()
            return "submit" == type
        }

    val isImageInput: Boolean
        get() {
            val type = this.getType()
            return "image" == type
        }

    val isResetInput: Boolean
        get() {
            val type = this.getType()
            return "reset" == type
        }

    override fun resetInput() {
        val ic = this.inputContext
        if (ic != null) {
            ic.resetInput()
        }
    }

    protected fun getFormInputs(): Array<FormInput>? {
        val type = this.getType()
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
                if (this.getChecked()) {
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
                val file = this.fileValue
                if (file == null) {
                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("getFormInputs(): File input named " + name + " has null file.")
                    }
                    return null
                } else {
                    return arrayOf<FormInput>(FormInput(name, file))
                }
            } else {
                return null
            }
        }
    }
}
