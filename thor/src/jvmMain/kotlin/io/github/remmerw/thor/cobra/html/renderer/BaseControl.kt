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
 * Created on Oct 23, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.util.logging.Logger
import javax.swing.JComponent

abstract class BaseControl
/**
 * @param context
 */(protected val controlElement: HTMLElementImpl?) : JComponent(), UIControl {
    protected var ruicontrol: RUIControl? = null

    fun getComponent(): Component {
        return this
    }

    override fun setRUIControl(ruicontrol: RUIControl?) {
        this.ruicontrol = ruicontrol
    }

    open fun getVAlign(): VerticalAlign? {
        return VerticalAlign.BASELINE
    }

    /**
     * Method invoked when image changes size. It's expected to be called outside
     * the GUI thread.
     */
    protected fun invalidateAndRepaint() {
        val rc = this.ruicontrol
        if (rc == null) {
            logger.severe("invalidateAndPaint(): RUIControl not set.")
            return
        }
        if (rc.isValid) {
            rc.relayout()
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.UIControl#getBackgroundColor()
     */
    fun getBackgroundColor(): Color? {
        return this.getBackground()
    }

    override fun reset(availWidth: Int, availHeight: Int) {
    }

    companion object {
        protected val ZERO_DIMENSION: Dimension = Dimension(0, 0)
        private const val serialVersionUID = 7061225345785659580L
        private val logger: Logger = Logger.getLogger(BaseControl::class.java.name)
    }
}
