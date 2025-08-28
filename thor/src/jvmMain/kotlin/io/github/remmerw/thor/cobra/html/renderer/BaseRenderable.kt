package io.github.remmerw.thor.cobra.html.renderer

internal abstract class BaseRenderable : Renderable {
    var ordinal: Int = 0

    open val zIndex: Int
        get() = 0
}
