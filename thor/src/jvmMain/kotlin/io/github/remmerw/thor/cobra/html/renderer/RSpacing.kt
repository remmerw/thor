/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

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

    Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on May 21, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import java.awt.Graphics
import java.awt.event.MouseEvent

/**
 * This is used when there's padding or margins in inline elements.
 */
internal class RSpacing(me: ModelNode?, container: RenderableContainer?, width: Int, height: Int) :
    BaseBoundableRenderable(container, me) {
    init {
        // Dimensions set when constructed.
        this.width = width
        this.height = height
    }

    override fun invalidateLayoutLocal() {
    }

    override fun onMouseClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.getInstance().onMouseClick(me, event, x, y)
        } else {
            return true
        }
    }

    override fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.getInstance().onDoubleClick(me, event, x, y)
        } else {
            return true
        }
    }

    override fun onMousePressed(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.getInstance().onMouseDown(me, event, x, y)
        } else {
            return true
        }
    }

    override fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.getInstance().onMouseUp(me, event, x, y)
        } else {
            return true
        }
    }

    override fun onMouseDisarmed(event: MouseEvent?): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.getInstance().onMouseDisarmed(me, event)
        } else {
            return true
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.xamj.domimpl.markup.Renderable#paint(java.awt.Graphics)
     */
    override fun paint(g: Graphics?) {
        // Nothing to paint in spacing.
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#paintSelection(java.awt.Graphics
     * , boolean, org.xamjwg.html.renderer.RenderablePoint,
     * org.xamjwg.html.renderer.RenderablePoint)
     */
    override fun paintSelection(
        g: Graphics,
        inSelection: Boolean,
        startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean {
        if ((this == startPoint.renderable) || (this == endPoint.renderable)) {
            if (inSelection) {
                return false
            }
        } else if (!inSelection) {
            return false
        }
        g.color = SELECTION_COLOR
        g.setXORMode(SELECTION_XOR)
        g.fillRect(0, 0, this.width, this.height)
        g.setPaintMode()
        return true
    }

    override fun extractSelectionText(
        buffer: StringBuffer?, inSelection: Boolean, startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean {
        if ((this == startPoint.renderable) || (this == endPoint.renderable)) {
            return !inSelection
        } else return inSelection
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.BoundableRenderable#getRenderable(int, int)
     */
    override fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot {
        return RenderableSpot(this, x, y)
    }

    override fun isContainedByNode(): Boolean {
        return true
    }

    override fun onRightClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.getInstance().onContextMenu(me, event, x, y)
        } else {
            return true
        }
    }
}
