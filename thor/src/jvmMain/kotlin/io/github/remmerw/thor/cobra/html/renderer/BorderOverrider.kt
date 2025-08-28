package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.style.HtmlInsets
import java.awt.Insets

internal class BorderOverrider {
    var leftOverridden: Boolean = false
    var rightOverridden: Boolean = false
    var bottomOverridden: Boolean = false
    var topOverridden: Boolean = false

    fun get(borderInsets: Insets): Insets {
        if (leftOverridden || rightOverridden || topOverridden || bottomOverridden) {
            val topDash = if (topOverridden) 0 else borderInsets.top
            val leftDash = if (leftOverridden) 0 else borderInsets.left
            val bottomDash = if (bottomOverridden) 0 else borderInsets.bottom
            val rightDash = if (rightOverridden) 0 else borderInsets.right
            return Insets(topDash, leftDash, bottomDash, rightDash)
        }
        return borderInsets
    }

    fun copyFrom(other: BorderOverrider) {
        this.topOverridden = other.topOverridden
        this.leftOverridden = other.leftOverridden
        this.rightOverridden = other.rightOverridden
        this.bottomOverridden = other.bottomOverridden
    }

    fun get(borderInsets: HtmlInsets?): HtmlInsets? {
        if ((borderInsets != null) && (leftOverridden || rightOverridden || topOverridden || bottomOverridden)) {
            val topDash = if (topOverridden) 0 else borderInsets.top
            val leftDash = if (leftOverridden) 0 else borderInsets.left
            val bottomDash = if (bottomOverridden) 0 else borderInsets.bottom
            val rightDash = if (rightOverridden) 0 else borderInsets.right
            return HtmlInsets(topDash, leftDash, bottomDash, rightDash, HtmlInsets.TYPE_PIXELS)
        }
        return borderInsets
    }
}