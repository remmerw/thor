package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.ua.UserAgentContext

abstract class BaseBlockyRenderable(
    container: RenderableContainer?,
    modelNode: ModelNode?,
    ucontext: UserAgentContext?
) : BaseElementRenderable(container, modelNode, ucontext) {
    abstract fun layout(
        availWidth: Int,
        availHeight: Int,
        b: Boolean,
        c: Boolean,
        source: FloatingBoundsSource?,
        sizeOnly: Boolean
    )
}
