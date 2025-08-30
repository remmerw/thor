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

import java.awt.Rectangle

/**
 * A [Renderable] with children.
 */
interface RCollection : BoundableRenderable {
    /**
     * Gets the collection of [Renderable] children.
     *
     * @param topFirst If true, then the renderable that is visually on top comes first in the iterator.
     * Currently, topFirst=true is worse for performance, as it requires reversing.
     */
    fun getRenderables(topFirst: Boolean): MutableIterator<Renderable?>?

    val renderables: MutableIterator<Renderable?>?
        /**
         * Gets the collection of [Renderable] children in unspecified order.
         * Call this variant when the order of the result doesn't matter.
         * The order defaults to the order that is natural to the implementation.
         */
        get() = getRenderables(false)

    fun updateWidgetBounds(guiX: Int, guiY: Int)


    fun invalidateLayoutDeep()

    fun focus()

    fun blur()

    fun getRenderable(x: Int, y: Int): BoundableRenderable?

    val clipBounds: Rectangle?

    val clipBoundsWithoutInsets: Rectangle?
}
