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
import javax.swing.ButtonGroup
import javax.swing.JRadioButton

internal class InputRadioControl(modelNode: HTMLBaseInputElement?) : BaseInputControl(modelNode) {
    private val widget: JRadioButton
    private var buttonGroup: ButtonGroup? = null

    init {
        this.layout = WrapperLayout.instance
        val radio = JRadioButton()
        radio.isOpaque = false
        this.widget = radio

        // Note: Value attribute cannot be set in reset() method.
        // Otherwise, layout revalidation causes typed values to
        // be lost (including revalidation due to hover.)
        val controlElement = this.controlElement!!
        val name = controlElement.getAttribute("name")
        val prevGroup = this.buttonGroup
        if (prevGroup != null) {
            prevGroup.remove(radio)
        }
        if (name != null) {
            val key = "cobra.radio.group." + name
            var group = controlElement.getDocumentItem(key) as ButtonGroup?
            if (group == null) {
                group = ButtonGroup()
                controlElement.setDocumentItem(key, group)
            }
            group.add(radio)
            this.buttonGroup = group
        } else {
            this.buttonGroup = null
        }
        radio.isSelected = controlElement.getAttributeAsBoolean("checked")

        this.add(radio)
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
        this.widget.isSelected = this.controlElement!!.getAttributeAsBoolean("checked")
    }

    override var value: String? = null
        get() = this.controlElement?.getAttribute("value")

}
