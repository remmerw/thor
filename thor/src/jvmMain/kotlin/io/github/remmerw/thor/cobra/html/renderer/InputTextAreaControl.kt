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
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.text.JTextComponent

internal class InputTextAreaControl(modelNode: HTMLBaseInputElement?) :
    BaseInputControl(modelNode) {
    private val widget: JTextComponent
    override var cols = -1
    override var rows = -1

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

    override var preferredSize: Dimension?
        get() = TODO("Not yet implemented")
        set(value) {}

    override var component: Component?
        get() = TODO("Not yet implemented")
        set(value) {}

    protected fun createTextField(): JTextComponent {
        return JTextArea()
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.BaseInputControl#getCols()
     */
    fun getCols(): Int {
        return this.cols
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.BaseInputControl#setCols(int)
     */
    fun setCols(cols: Int) {
        if (cols != this.cols) {
            this.cols = cols
            this.invalidate()
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.BaseInputControl#getRows()
     */
    fun getRows(): Int {
        return this.rows
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.BaseInputControl#setRows(int)
     */
    fun setRows(rows: Int) {
        if (rows != this.rows) {
            this.rows = rows
            this.invalidate()
        }
    }

    override fun getPreferredSize(): Dimension {
        val pw: Int
        val cols = this.cols
        if (cols == -1) {
            pw = 100
        } else {
            val f = this.widget.getFont()
            val fm = this.widget.getFontMetrics(f)
            val insets = this.widget.insets
            pw = insets.left + insets.right + (fm.charWidth('*') * cols)
        }
        val ph: Int
        val rows = this.rows
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

    override var name: String?
        get() = TODO("Not yet implemented")
        set(value) {}

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
