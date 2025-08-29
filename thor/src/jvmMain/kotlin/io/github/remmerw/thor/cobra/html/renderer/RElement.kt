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
package io.github.remmerw.thor.cobra.html.renderer

import cz.vutbr.web.css.CSSProperty
import cz.vutbr.web.css.CSSProperty.VerticalAlign
import io.github.remmerw.thor.cobra.html.domimpl.UINode

/**
 * A renderer node for elements such as blocks, lists, tables, inputs, images,
 * etc.
 */
interface RElement : RCollection, UINode {
    /**
     * Lays out the subtree below the RElement. The RElement is expected to set
     * its own dimensions, but not its origin.
     *
     * @param availWidth  The available width from the parent's canvas.
     * @param availHeight The available height from the parent's canvas.
     * @param sizeOnly    Whether the layout is for sizing determination only.
     */
    fun layout(availWidth: Int, availHeight: Int, sizeOnly: Boolean)

    val vAlign: CSSProperty.VerticalAlign?
        /**
         * Vertical alignment for elements rendered in a line. Returns one of the
         * constants defined in this class.
         */
        get() = VerticalAlign.BASELINE

    val marginTop: Int

    val marginLeft: Int

    val marginBottom: Int

    val marginRight: Int

    val collapsibleMarginTop: Int

    val collapsibleMarginBottom: Int

    fun invalidateRenderStyle()

    fun setupRelativePosition(container: RenderableContainer?)

    companion object {
        const val VALIGN_TOP: Int = 0
        const val VALIGN_MIDDLE: Int = 1
        const val VALIGN_BOTTOM: Int = 2
        const val VALIGN_ABSMIDDLE: Int = 3
        const val VALIGN_ABSBOTTOM: Int = 4
        const val VALIGN_BASELINE: Int = 5
    }
}
