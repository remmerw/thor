package io.github.remmerw.thor.cobra.html.renderer

import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics

internal class BrokenComponent : Component() {
    override fun getPreferredSize(): Dimension {
        return Dimension(10, 10)
    }

    override fun update(g: Graphics) {
        this.paint(g)
    }

    override fun paint(g: Graphics) {
        g.color = Color.RED
        val size = this.size
        g.drawRect(0, 0, size.width, size.height)
        g.drawLine(0, 0, size.width - 1, size.height - 1)
        g.drawLine(size.width - 1, 0, 0, size.height - 1)
    }

    companion object {
        private val serialVersionUID = -6506487314783190388L
    }
}
