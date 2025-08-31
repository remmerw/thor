package io.github.remmerw.thor.cobra.html.renderer

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import io.github.remmerw.thor.cobra.html.domimpl.UINode

interface RElement : RCollection, UINode {

    fun layout(availWidth: Int, availHeight: Int, sizeOnly: Boolean)

    fun vAlign(): VerticalAlign?

    fun marginTop(): Int

    fun marginLeft(): Int

    fun marginBottom(): Int

    fun marginRight(): Int

    fun collapsibleMarginTop(): Int

    fun collapsibleMarginBottom(): Int

    fun invalidateRenderStyle()

    fun setupRelativePosition(container: RenderableContainer)


}
