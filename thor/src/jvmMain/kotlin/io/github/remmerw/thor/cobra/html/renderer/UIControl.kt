package io.github.remmerw.thor.cobra.html.renderer

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics

interface UIControl {
    fun setRUIControl(ruiControl: RUIControl?)

    fun reset(availWidth: Int, availHeight: Int)

    fun preferredSize(): Dimension?

    fun vAlign(): VerticalAlign?

    fun setBounds(x: Int, y: Int, width: Int, height: Int)

    fun invalidate()

    fun backgroundColor(): Color?

    fun paint(g: Graphics?)

    fun component(): Component
}
