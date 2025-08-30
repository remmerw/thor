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
import io.github.remmerw.thor.cobra.html.domimpl.HTMLInputElementImpl
import io.github.remmerw.thor.cobra.util.gui.WrapperLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton

internal class InputButtonControl(modelNode: HTMLBaseInputElement?) : BaseInputControl(modelNode) {
    private val widget: JButton

    init {
        this.layout = WrapperLayout.instance
        val widget = JButton()
        widget.isContentAreaFilled = false
        this.widget = widget
        this.add(widget)
        widget.addActionListener(object : ActionListener {
            override fun actionPerformed(event: ActionEvent?) {
                HtmlController.Companion.instance
                    .onPressed(this@InputButtonControl.controlElement, null, 0, 0)
            }
        })
    }

    override fun reset(availWidth: Int, availHeight: Int) {
        super.reset(availWidth, availHeight)
        val ruiControl = this.ruicontrol
        val button = this.widget
        button.isContentAreaFilled = !ruiControl!!.hasBackground()
        val foregroundColor = ruiControl.foregroundColor
        if (foregroundColor != null) {
            button.setForeground(foregroundColor)
        }
        val element = this.controlElement as HTMLInputElementImpl
        var text = element.getAttribute("value")
        if ((text == null) || (text.length == 0)) {
            val type = element.getType()
            if ("submit".equals(type, ignoreCase = true)) {
                text = " "
            } else if ("reset".equals(type, ignoreCase = true)) {
                text = " "
            } else {
                text = ""
            }
        }
        button.text = text
    }



    override var component: Component?
        get() = TODO("Not yet implemented")
        set(value) {}

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.InputContext#click()
     */
    override fun click() {
        this.widget.doClick()
    }

    override var name: String?
        get() = TODO("Not yet implemented")
        set(value) {}

    override var value: String?
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getValue()
             */
        get() = this.widget.text
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setValue(java.lang.String)
             */
        set(value) {
            this.widget.setText(value)
        }

    fun setDisabled(disabled: Boolean) {
        super.disabled = disabled
        this.widget.isEnabled = !disabled
    }

    override fun resetInput() {
        // nop
    }

    companion object {
        private val serialVersionUID = -8399402892016789567L
    }
}
