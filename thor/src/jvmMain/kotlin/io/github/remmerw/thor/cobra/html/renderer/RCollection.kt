package io.github.remmerw.thor.cobra.html.renderer

import java.awt.Rectangle

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

    fun clipBounds(): Rectangle?

    fun clipBoundsWithoutInsets(): Rectangle?
}
