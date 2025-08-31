package io.github.remmerw.thor.cobra.html.renderer

abstract class BaseRenderable : Renderable {


    override fun isFixed(): Boolean {
        return false
    }
    override fun isReadyToPaint(): Boolean {
        return true
    }
}
