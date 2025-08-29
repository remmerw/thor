package io.github.remmerw.thor.cobra.html.style

class BaseFontRenderState(prevRenderState: RenderState, override val fontBase: Int) :
    RenderStateDelegator(prevRenderState) {
    override fun getFontBase(): Int {
        return this.fontBase
    }
}
