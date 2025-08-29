/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2014 Uproot Labs India Pvt Ltd

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 */
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.util.CollectionUtilities
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent

class RInlineBlock(
    container: RenderableContainer?, modelNode: HTMLElementImpl, uacontext: UserAgentContext?,
    rendererContext: HtmlRendererContext?, frameContext: FrameContext?
) : BaseElementRenderable(container, modelNode, uacontext) {
    private val child: BaseBlockyRenderable

    init {
        val display = modelNode.getRenderState().display
        val child = if (display == RenderState.DISPLAY_INLINE_TABLE)
            RTable(modelNode, userAgentContext, rendererContext, frameContext, this)
        else
            RBlock(modelNode, 0, userAgentContext, rendererContext, frameContext, this)
        child.setOriginalParent(this)
        child.setParent(this)
        this.child = child
    }

    private fun assignDimension() {
        this.width = child.getWidth()
        this.height = child.getHeight()
    }

    override fun getRenderables(topFirst: Boolean): MutableIterator<Renderable?> {
        return CollectionUtilities.singletonIterator<Renderable?>(this.child as Renderable)
    }

    override val clipBounds: Rectangle?
        get() = TODO("Not yet implemented")
    override val clipBoundsWithoutInsets: Rectangle?
        get() = TODO("Not yet implemented")
    override val bounds: Rectangle?
        get() = TODO("Not yet implemented")
    override val visualBounds: Rectangle?
        get() = TODO("Not yet implemented")
    override val size: Dimension?
        get() = TODO("Not yet implemented")
    override val origin: Point?
        get() = TODO("Not yet implemented")
    override var parent: RCollection?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var originalParent: RCollection?
        get() = TODO("Not yet implemented")
        set(value) {}
    override val originalOrCurrentParent: RCollection?
        get() = TODO("Not yet implemented")
    override val visualX: Int
        get() = TODO("Not yet implemented")
    override val visualY: Int
        get() = TODO("Not yet implemented")
    override val visualHeight: Int
        get() = TODO("Not yet implemented")
    override val visualWidth: Int
        get() = TODO("Not yet implemented")

    override fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot? {
        return this.child.getLowestRenderableSpot(x, y)
    }

    override fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        return this.child.onDoubleClick(event, x, y)
    }

    override val isContainedByNode: Boolean
        get() = TODO("Not yet implemented")
    override val isDelegated: Boolean
        get() = TODO("Not yet implemented")

    override fun onMouseClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        return this.child.onMouseClick(event, x, y)
    }

    override fun onMouseDisarmed(event: MouseEvent?): Boolean {
        return this.child.onMouseDisarmed(event)
    }

    override fun onMousePressed(event: MouseEvent?, x: Int, y: Int): Boolean {
        return this.child.onMousePressed(event, x, y)
    }

    override fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean {
        return this.child.onMouseReleased(event, x, y)
    }

    public override fun paintShifted(g: Graphics) {
        this.child.paint(g)
    }

    override fun repaint(modelNode: ModelNode?) {
        // TODO: Who calls this?
        this.child.repaint(modelNode)
    }

    fun getPaintedBackgroundColor(): Color? {
        return this.backgroundColor
    }

    public override fun doLayout(availWidth: Int, availHeight: Int, sizeOnly: Boolean) {
        this.child.layout(availWidth, availHeight, false, false, null, sizeOnly)
        // this.child.doLayout(availWidth, availHeight, sizeOnly);
        sendDelayedPairsToParent()
        assignDimension()
        markLayoutValid()
    }

    override fun addComponent(component: Component?): Component? {
        this.container!!.addComponent(component)
        return super.addComponent(component)
    }

    override val paintedBackgroundColor: Color?
        get() = TODO("Not yet implemented")
    override val parentContainer: RenderableContainer?
        get() = TODO("Not yet implemented")

    override fun toString(): String {
        return "RInlineBlock [" + this.child + "]"
    }

    override fun applyLook() {
        this.child.applyLook()
    }

    override fun invalidateLayoutLocal() {
        super.invalidateLayoutLocal()
        this.child.invalidateLayoutLocal()
    }

    override fun markLayoutValid() {
        super.markLayoutValid()
        this.child.markLayoutValid()
    }

    override fun getZIndex(): Int {
        return this.child.getZIndex()
    }

    override fun isReadyToPaint(): Boolean {
        return child.isReadyToPaint()
    }

    override val marginTop: Int
        get() = TODO("Not yet implemented")
    override val marginLeft: Int
        get() = TODO("Not yet implemented")
    override val marginBottom: Int
        get() = TODO("Not yet implemented")
    override val marginRight: Int
        get() = TODO("Not yet implemented")
    override val collapsibleMarginTop: Int
        get() = TODO("Not yet implemented")
    override val collapsibleMarginBottom: Int
        get() = TODO("Not yet implemented")
}
