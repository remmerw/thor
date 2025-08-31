package io.github.remmerw.thor.cobra.html.renderer

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics

interface UIControl {
    fun setRUIControl(ruicontrol: RUIControl?)

    /**
     * Called as the control is layed out, either the first time HTML layout
     * occurs or when the DOM changes. This method should reset its state assuming
     * the element has changed, and change its preferred size if appropriate.
     */
    fun reset(availWidth: Int, availHeight: Int)

    fun preferredSize(): Dimension?

    fun vAlign(): VerticalAlign?

    fun setBounds(x: Int, y: Int, width: Int, height: Int)

    fun invalidate()

    fun backgroundColor(): Color?

    // public boolean paintSelection(Graphics g, boolean inSelection,
    // RenderableSpot startPoint, RenderableSpot endPoint);
    fun paint(g: Graphics?)

    fun component(): Component

    val isReadyToPaint: Boolean
        get() = true
}
