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
import io.github.remmerw.thor.cobra.util.gui.WrapperLayout
import java.awt.Dimension
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.JTextComponent
import javax.swing.text.PlainDocument

internal abstract class BaseInputTextControl(modelNode: HTMLBaseInputElement?) :
    BaseInputControl(modelNode) {
    protected val widget: JTextComponent

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.InputContext#getMaxLength()
     *//*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.InputContext#setMaxLength(int)
     */
    var maxLength: Int = -1

    init {
        this.layout = WrapperLayout.instance
        val widget = this.createTextField()
        val font = widget.getFont()
        widget.setFont(font.deriveFont(DEFAULT_FONT_SIZE))
        widget.document = LimitedDocument()

        // Note: Value attribute cannot be set in reset() method.
        // Otherwise, layout revalidation causes typed values to
        // be lost (including revalidation due to hover.)
        val element: ElementImpl = this.controlElement
        val value = element.getAttribute("value")
        widget.text = value

        this.widget = widget
        this.add(widget)
    }

    override fun reset(availWidth: Int, availHeight: Int) {
        super.reset(availWidth, availHeight)
        val maxLengthText = this.controlElement.getAttribute("maxlength")
        if (maxLengthText != null) {
            try {
                this.maxLength = maxLengthText.toInt()
            } catch (nfe: NumberFormatException) {
                // ignore
            }
        }
    }

    protected abstract fun createTextField(): JTextComponent

    var readOnly: Boolean
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getReadOnly()
             */
        get() = !this.widget.isEditable
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setReadOnly(boolean)
             */
        set(readOnly) {
            this.widget.setEditable(!readOnly)
        }

    var value: String?
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

    /*
    * (non-Javadoc)
    *
    * @see org.xamjwg.html.domimpl.InputContext#select()
    */
    override fun select() {
        this.widget.selectAll()
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.InputContext#setDisabled(boolean)
     */
    override fun setDisabled(disabled: Boolean) {
        super.disabled = disabled
        this.widget.isEnabled = !disabled
    }

    override fun getPreferredSize(): Dimension {
        val size = this.size
        val widget = this.widget
        val fm = widget.getFontMetrics(widget.getFont())
        val insets = widget.insets
        val pw: Int
        val ph: Int
        if (size == -1) {
            pw = 100
        } else {
            pw = insets.left + insets.right + (fm.charWidth('0') * size)
        }
        ph = fm.height + insets.top + insets.bottom
        return Dimension(pw, ph)
    }

    override fun resetInput() {
        this.widget.text = ""
    }

    /**
     * Implements maxlength functionality.
     */
    private inner class LimitedDocument : PlainDocument() {
        /*
         * (non-Javadoc)
         *
         * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String,
         * javax.swing.text.AttributeSet)
         */
        @Throws(BadLocationException::class)
        override fun insertString(offs: Int, str: String, a: AttributeSet?) {
            val max = this@BaseInputTextControl.maxLength
            if (max != -1) {
                val docLength = this.length
                if (docLength >= max) {
                    return
                }
                val strLen = str.length
                if ((docLength + strLen) > max) {
                    val shorterStr = str.substring(0, max - docLength)
                    super.insertString(offs, shorterStr, a)
                } else {
                    super.insertString(offs, str, a)
                }
            } else {
                super.insertString(offs, str, a)
            }
        }

        companion object {
            private const val serialVersionUID = 5095817476961455383L
        }
    }

    companion object {
        private val serialVersionUID = -4852316720577045230L
        private const val DEFAULT_FONT_SIZE = 14.0f
    }
}
