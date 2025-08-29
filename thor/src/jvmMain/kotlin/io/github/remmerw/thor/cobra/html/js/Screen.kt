package io.github.remmerw.thor.cobra.html.js

import io.github.remmerw.thor.cobra.js.ScriptableDelegate
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment

class Screen internal constructor() : ScriptableDelegate() {
    private val graphicsEnvironment: GraphicsEnvironment?
    private val graphicsDevice: GraphicsDevice?

    /**
     * @param context
     */
    init {
        if (GraphicsEnvironment.isHeadless()) {
            this.graphicsEnvironment = null
            this.graphicsDevice = null
        } else {
            this.graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
            this.graphicsDevice = this.graphicsEnvironment.defaultScreenDevice
        }
    }

    val height: Int
        get() {
            val gd = this.graphicsDevice
            return if (gd == null) 0 else gd.displayMode.height
        }

    val pixelDepth: Int
        get() = this.colorDepth

    val width: Int
        get() {
            val ge = this.graphicsEnvironment
            if (ge == null) {
                return 0
            }
            val gd = ge.defaultScreenDevice
            return gd.displayMode.width
        }

    val availHeight: Int
        get() {
            val ge = this.graphicsEnvironment
            if (ge == null) {
                return 0
            }
            return ge.maximumWindowBounds.height
        }

    val availWidth: Int
        get() {
            val ge = this.graphicsEnvironment
            if (ge == null) {
                return 0
            }
            return ge.maximumWindowBounds.width
        }

    val colorDepth: Int
        get() {
            val gd = this.graphicsDevice
            if (gd == null) {
                return 0
            }
            return gd.displayMode.bitDepth
        }
}
