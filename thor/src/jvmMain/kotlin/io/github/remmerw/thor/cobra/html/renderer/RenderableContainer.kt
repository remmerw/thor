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

import java.awt.Color
import java.awt.Component
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle

/**
 * A RenderableContainer is either usually a parent block or the root GUI
 * component. It's is a Renderable or GUI component whose layout may be
 * invalidated.
 */
interface RenderableContainer {
    // public Insets getInsets();
    fun addComponent(component: Component?): Component?

    // public void remove(Component component);
    fun invalidateLayoutUpTree()

    fun repaint(x: Int, y: Int, width: Int, height: Int)

    fun relayout()

    fun updateAllWidgetBounds()

    fun paintedBackgroundColor(): Color?

    fun getGUIPoint(x: Int, y: Int): Point?

    fun focus()

    fun addDelayedPair(pair: DelayedPair?)

    val delayedPairs: MutableCollection<DelayedPair?>?

    val parentContainer: RenderableContainer?

    fun clearDelayedPairs()

    val height: Int

    val width: Int

    val x: Int

    val y: Int

    fun getInsets(hscroll: Boolean, vscroll: Boolean): Insets?

    fun getInsetsMarginBorder(hscroll: Boolean, vscroll: Boolean): Insets?

    val innerWidth: Int
        get() {
            val insets = getInsetsMarginBorder(false, false)
            return this.width - (insets!!.left + insets.right)
        }

    val innerMostWidth: Int
        get() {
            val insets = getInsets(false, false)
            return this.width - (insets!!.left + insets.right)
        }

    val innerMostHeight: Int
        get() {
            val insets = getInsets(false, false)
            return this.height - (insets!!.top + insets.bottom)
        }

    val innerHeight: Int
        get() {
            val insets = getInsetsMarginBorder(false, false)
            return this.height - (insets!!.top + insets.bottom)
        }

    val visualBounds: Rectangle?

    val visualWidth: Int

    val visualHeight: Int

    fun translateDescendentPoint(descendent: BoundableRenderable, x: Int, y: Int): Point

    fun getOriginRelativeTo(bodyLayout: RCollection?): Point?

    fun getOriginRelativeToAbs(bodyLayout: RCollection?): Point?
}
