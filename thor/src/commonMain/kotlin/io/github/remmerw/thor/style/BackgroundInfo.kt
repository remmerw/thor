package io.github.remmerw.thor.style

import androidx.compose.ui.graphics.Color
import java.net.URL

class BackgroundInfo {
    var backgroundColor: Color? = null
    var backgroundImage: URL? = null
    var backgroundXPositionAbsolute: Boolean = false
    var backgroundXPosition: Int = 0
    var backgroundYPositionAbsolute: Boolean = false
    var backgroundYPosition: Int = 0
    var backgroundRepeat: Int = BR_REPEAT

    override fun toString(): String {
        return ("BackgroundInfo [color=" + backgroundColor + ", img=" + backgroundImage + ", xposAbs="
                + backgroundXPositionAbsolute + ", xpos=" + backgroundXPosition + ", yposAbs="
                + backgroundYPositionAbsolute + ", ypos=" + backgroundYPosition + ", repeat=" + backgroundRepeat + "]")
    }

    companion object {
        const val BR_REPEAT: Int = 0
    }
}
