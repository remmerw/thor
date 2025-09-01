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

import io.github.remmerw.thor.cobra.html.domimpl.ElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLBaseInputElement
import io.github.remmerw.thor.cobra.util.Strings
import io.github.remmerw.thor.cobra.util.gui.WrapperLayout
import java.awt.Dimension
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.text.JTextComponent

internal class InputTextAreaControl(modelNode: HTMLBaseInputElement?) :
    BaseInputControl(modelNode) {
    private val widget: JTextComponent

    init {
        this.layout = WrapperLayout.instance
        val widget = this.createTextField()
        this.widget = widget
        this.add(JScrollPane(widget))

        // Note: Value attribute cannot be set in reset() method.
        // Otherwise, layout revalidation causes typed values to
        // be lost (including revalidation due to hover.)
        val element: ElementImpl = this.controlElement!!
        val value = element.getTextContent()
        (widget as JTextArea).lineWrap = true
        widget.text = value
    }

    override fun reset(availWidth: Int, availHeight: Int) {
        super.reset(availWidth, availHeight)
        val element: ElementImpl = this.controlElement!!
        val colsStr = element.getAttribute("cols")
        if (colsStr != null) {
            try {
                this.setCols(colsStr.toInt())
            } catch (nfe: NumberFormatException) {
                // ignore
            }
        }
        val rowsStr = element.getAttribute("rows")
        if (rowsStr != null) {
            try {
                this.setRows(rowsStr.toInt())
            } catch (nfe: NumberFormatException) {
                // ignore
            }
        }
    }

    protected fun createTextField(): JTextComponent {
        return JTextArea()
    }


    override fun getCols(): Int {
        return this.getCols()
    }


    override fun setCols(value: Int) {
        if (value != this.getCols()) {
            this.setCols(value)
            this.invalidate()
        }
    }


    override fun getRows(): Int {
        return this.getRows()
    }

    override fun setRows(value: Int) {
        if (value != this.getRows()) {
            this.setRows(value)
            this.invalidate()
        }
    }

    override fun getPreferredSize(): Dimension {
        val pw: Int
        val cols = this.getCols()
        if (cols == -1) {
            pw = 100
        } else {
            val f = this.widget.getFont()
            val fm = this.widget.getFontMetrics(f)
            val insets = this.widget.insets
            pw = insets.left + insets.right + (fm.charWidth('*') * cols)
        }
        val ph: Int
        val rows = this.getRows()
        if (rows == -1) {
            ph = 100
        } else {
            val f = this.widget.getFont()
            val fm = this.widget.getFontMetrics(f)
            val insets = this.widget.insets
            ph = insets.top + insets.bottom + (fm.height * rows)
        }
        return Dimension(pw, ph)
    }



    override var readOnly: Boolean
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.renderer.BaseInputControl#getReadOnly()
             */
        get() = !this.widget.isEditable
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.renderer.BaseInputControl#setReadOnly(boolean)
             */
        set(readOnly) {
            this.widget.setEditable(readOnly)
        }

    override var value: String?
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.renderer.BaseInputControl#getValue()
             */
        get() {
            val text = this.widget.text
            return Strings.getCRLFString(text)
        }
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.renderer.BaseInputControl#setValue(java.lang.String)
             */
        set(value) {
            this.widget.setText(value)
        }

    override fun resetInput() {
        this.widget.text = ""
    }

}
