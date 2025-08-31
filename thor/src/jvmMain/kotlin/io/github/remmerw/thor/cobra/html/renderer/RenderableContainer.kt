package io.github.remmerw.thor.cobra.html.renderer

import java.awt.Color
import java.awt.Component
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle

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

    fun addDelayedPair(pair: DelayedPair)

    fun delayedPairs(): MutableCollection<DelayedPair>?

    fun parentContainer(): RenderableContainer?

    fun clearDelayedPairs()

    fun height(): Int
    fun setHeight(height: Int)

    fun width(): Int
    fun setWidth(width: Int)

    fun x(): Int
    fun setX(x: Int)

    fun y(): Int
    fun setY(y: Int)

    fun getInsets(hscroll: Boolean, vscroll: Boolean): Insets?

    fun getInsetsMarginBorder(hscroll: Boolean, vscroll: Boolean): Insets?

    fun innerWidth(): Int {
        val insets = getInsetsMarginBorder(false, false)
        return this.width() - (insets!!.left + insets.right)
    }

    fun innerMostWidth(): Int {
        val insets = getInsets(false, false)
        return this.width() - (insets!!.left + insets.right)
    }

    fun innerMostHeight(): Int {
        val insets = getInsets(false, false)
        return this.height() - (insets!!.top + insets.bottom)
    }

    fun innerHeight(): Int {
        val insets = getInsetsMarginBorder(false, false)
        return this.height() - (insets!!.top + insets.bottom)
    }

    fun visualBounds(): Rectangle?

    fun visualWidth(): Int

    fun visualHeight(): Int

    fun translateDescendantPoint(descendant: BoundableRenderable, x: Int, y: Int): Point

    fun getOriginRelativeTo(bodyLayout: RCollection?): Point?

    fun getOriginRelativeToAbs(bodyLayout: RCollection?): Point?
}
