package io.github.remmerw.thor.cobra.html.renderer

import org.w3c.dom.html.HTMLHtmlElement

object RenderUtils {
    fun findHtmlRenderable(root: RCollection): Renderable? {
        val rs = root.getRenderables()
        if (rs != null) {
            while (rs.hasNext()) {
                val r = rs.next()
                if (r.getModelNode() is HTMLHtmlElement) {
                    return r
                }
            }
        }

        return null
    }
}
