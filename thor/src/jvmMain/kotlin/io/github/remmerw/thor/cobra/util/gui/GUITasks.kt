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
package io.github.remmerw.thor.cobra.util.gui

import java.awt.BasicStroke
import java.awt.Frame
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.KeyStroke
import kotlin.math.max

object GUITasks {
    val topFrame: Frame?
        get() {
            val frames = Frame.getFrames()
            for (frame in frames) {
                if (frame.focusOwner != null) {
                    return frame
                }
            }
            if (frames.size > 0) {
                return frames[0]
            }
            return null
        }

    fun drawDashed(g: Graphics, x1: Int, y1: Int, x2: Int, y2: Int, dashSize: Int, gapSize: Int) {
        var x1 = x1
        var y1 = y1
        var x2 = x2
        var y2 = y2
        if (x2 < x1) {
            val temp = x1
            x1 = x2
            x2 = temp
        }
        if (y2 < y1) {
            val temp = y1
            y1 = y2
            y2 = temp
        }
        val totalDash = dashSize + gapSize
        if (y1 == y2) {
            val virtualStartX = (x1 / totalDash) * totalDash
            var x = virtualStartX
            while (x < x2) {
                var topX = x + dashSize
                if (topX > x2) {
                    topX = x2
                }
                var firstX = x
                if (firstX < x1) {
                    firstX = x1
                }
                if (firstX < topX) {
                    g.drawLine(firstX, y1, topX, y1)
                }
                x += totalDash
            }
        } else if (x1 == x2) {
            val virtualStartY = (y1 / totalDash) * totalDash
            var y = virtualStartY
            while (y < y2) {
                var topY = y + dashSize
                if (topY > y2) {
                    topY = y2
                }
                var firstY = y
                if (firstY < y1) {
                    firstY = y1
                }
                if (firstY < topY) {
                    g.drawLine(x1, firstY, x1, topY)
                }
                y += totalDash
            }
        } else {
            // Not supported
            g.drawLine(x1, y1, x2, y2)
        }
    }

    fun drawDotted(g: Graphics, x1: Int, y1: Int, x2: Int, y2: Int, width: Float) {
        val ng = g.create()
        try {
            val g2d = ng.create() as Graphics2D
            val dot = max(2f, width)

            val dotPattern = floatArrayOf(dot)
            val stroke = BasicStroke(
                width,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                2.0f,
                dotPattern,
                0.0f
            )
            g2d.stroke = stroke
            g2d.drawLine(x1, y1, x2, y2)
        } finally {
            ng.dispose()
        }
    }

    // As per this http://stackoverflow.com/a/661244/161257
    fun addEscapeListener(dialog: JDialog) {
        val escListener = ActionListener { e: ActionEvent? ->
            dialog.isVisible = false
            dialog.dispose()
        }

        dialog.getRootPane().registerKeyboardAction(
            escListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )
    }
}
