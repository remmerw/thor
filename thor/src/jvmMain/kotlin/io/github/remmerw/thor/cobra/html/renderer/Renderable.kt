package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.dom.ModelNode
import java.awt.Graphics

/**
 * Represents a renderer (view) node.
 */
interface Renderable {
    fun paint(g: Graphics)

    fun modelNode(): ModelNode?

    fun isFixed(): Boolean

    fun isReadyToPaint(): Boolean

    val isReplacedElement: Boolean
        get() =// TODO: Match all other replaced elements such as audio, video, canvas, etc. Refer: http://stackoverflow.com/a/12468620
            this.modelNode()?.nodeName()?.uppercase() == "IMG"

    companion object {
        val EMPTY_ARRAY: Array<Renderable?> = arrayOfNulls<Renderable>(0)
    }
}
