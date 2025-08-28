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

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import io.github.remmerw.thor.cobra.html.domimpl.HTMLBaseInputElement
import io.github.remmerw.thor.cobra.html.domimpl.InputContext
import java.awt.Graphics
import java.io.File

abstract class BaseInputControl(modelNode: HTMLBaseInputElement?) : BaseControl(modelNode),
    InputContext {

    protected var size: Int = -1

    init {
        this.isOpaque = false
    }

    override fun reset(availWidth: Int, availHeight: Int) {
        super.reset(availWidth, availHeight)
        val sizeText = this.controlElement?.getAttribute("size")
        if (sizeText != null) {
            try {
                this.size = sizeText.toInt()
            } catch (nfe: NumberFormatException) {
                // ignore
            }
        }
    }

    override fun getVAlign(): VerticalAlign? {
        return VerticalAlign.BOTTOM
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.InputContext#blur()
     */
    override fun blur() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.InputContext#click()
     */
    override fun click() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.InputContext#focus()
     */
    override fun focus() {
        this.requestFocus()
    }

    override var checked: Boolean
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getChecked()
             */
        get() = false
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setChecked(boolean)
             */
        set(checked) {
        }

    override var disabled: Boolean
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getDisabled()
             */
        get() = !this.isEnabled
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setDisabled(boolean)
             */
        set(disabled) {
            this.setEnabled(!disabled)
        }

    override var maxLength: Int
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getMaxLength()
             */
        get() = 0
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setMaxLength(int)
             */
        set(maxLength) {
        }

    override var readOnly: Boolean
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getReadOnly()
             */
        get() = false
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setReadOnly(boolean)
             */
        set(readOnly) {
        }

    override var tabIndex: Int
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getTabIndex()
             */
        get() = 0
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setTabIndex(int)
             */
        set(tabIndex) {
        }

    override val values: Array<String?>?
        /**
         * Returns `null`. It should be overridden by controls that support
         * multiple values.
         */
        get() = null

    /*
    * (non-Javadoc)
    *
    * @see org.xamjwg.html.domimpl.InputContext#select()
    */
    override fun select() {
    }

    override var controlSize: Int
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getTextSize()
             */
        get() = this.size
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setSize(int)
             */
        set(size) {
            this.size = size
            this.invalidate()
        }

    override var cols: Int
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getCols()
             */
        get() = 0
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setCols(int)
             */
        set(cols) {
        }

    override var rows: Int
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#getRows()
             */
        get() = 0
        /*
             * (non-Javadoc)
             *
             * @see org.xamjwg.html.domimpl.InputContext#setRows(int)
             */
        set(rows) {
        }

    /*
    * (non-Javadoc)
    *
    * @see org.xamjwg.html.renderer.UIControl#paintSelection(java.awt.Graphics,
    * boolean, org.xamjwg.html.renderer.RenderablePoint,
    * org.xamjwg.html.renderer.RenderablePoint)
    */
    open fun paintSelection(
        g: Graphics?,
        inSelection: Boolean,
        startPoint: RenderableSpot?,
        endPoint: RenderableSpot?
    ): Boolean {
        return inSelection
    }

    var multiple: Boolean
        get() =// For selects
            false
        set(value) {
            // For selects
        }

    override var selectedIndex: Int
        get() =// For selects
            -1
        set(value) {
            // For selects
        }

    override var visibleSize: Int
        get() =// For selects
            0
        set(value) {
            // For selects
        }

    override val fileValue: File?
        get() =// For file inputs
            null

}
