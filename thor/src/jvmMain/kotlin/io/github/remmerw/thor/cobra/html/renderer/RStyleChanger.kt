package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.style.RenderState
import java.awt.Graphics
import java.awt.event.MouseEvent

class RStyleChanger(private var modelNode: ModelNode?) : BaseRenderable() {


    override fun modelNode(): ModelNode {
        return this.modelNode!!
    }


    override fun paint(g: Graphics) {
        val rs: RenderState = this.modelNode!!.renderState()!!
        g.color = rs.getColor()
        g.font = rs.getFont()
    }

    fun invalidateLayoutUpTree() {
    }

    companion object {
        fun onMouseClick(event: MouseEvent?, x: Int, y: Int) {
            throw UnsupportedOperationException("unexpected")
        }

        fun onMousePressed(event: MouseEvent?, x: Int, y: Int) {
            throw UnsupportedOperationException("unexpected")
        }

        fun onMouseReleased(event: MouseEvent?, x: Int, y: Int) {
            throw UnsupportedOperationException("unexpected")
        }
    }
}
