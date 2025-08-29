package io.github.remmerw.thor.cobra.html.renderer

import org.w3c.dom.html.HTMLHtmlElement

object RenderUtils {
    fun findHtmlRenderable(root: RCollection): Renderable? {
        val rs = root.renderables
        if (rs != null) {
            rs.forEach { r ->
                if (r?.modelNode is HTMLHtmlElement) {
                    return r
                }
            }
        }

        return null
    }
}
