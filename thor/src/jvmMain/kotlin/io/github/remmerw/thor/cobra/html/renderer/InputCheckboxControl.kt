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
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.HTMLBaseInputElement
import io.github.remmerw.thor.cobra.util.gui.WrapperLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.JCheckBox

internal class InputCheckboxControl(modelNode: HTMLBaseInputElement?) :
    BaseInputControl(modelNode) {
    private val widget: JCheckBox

    init {
        this.layout = WrapperLayout.instance
        val checkBox = JCheckBox()
        checkBox.isOpaque = false
        this.widget = checkBox

        // Note: Value attribute cannot be set in reset() method.
        // Otherwise, layout revalidation causes typed values to
        // be lost (including revalidation due to hover.)
        checkBox.isSelected = this.controlElement?.getAttributeAsBoolean("checked") == true

        this.add(checkBox)
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.InputContext#click()
     */
    override fun click() {
        this.widget.doClick()
    }

    override var checked: Boolean
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getChecked()
             */
        get() = this.widget.isSelected
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setChecked(boolean)
             */
        set(checked) {
            this.widget.setSelected(checked)
        }
    override var name: String?
        get() = TODO("Not yet implemented")
        set(value) {}

    /*
    * (non-Javadoc)
    *
    * @see org.xamjwg.html.domimpl.InputContext#setDisabled(boolean)
    */
    fun setDisabled(disabled: Boolean) {
        super.disabled = disabled
        this.widget.isEnabled = !disabled
    }

    override fun resetInput() {
        this.widget.isSelected = this.controlElement?.getAttributeAsBoolean("checked") == true
    }

    override var value: String? = null
        get() = this.controlElement?.getAttribute("value")
    override var preferredSize: Dimension?
        get() = TODO("Not yet implemented")
        set(value) {}

    override var component: Component?
        get() = TODO("Not yet implemented")
        set(value) {}

    companion object {
        private val serialVersionUID = -7156618963339104117L
    }
}
