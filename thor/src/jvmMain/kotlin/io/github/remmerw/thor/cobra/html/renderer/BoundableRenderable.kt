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
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent

/**
 * A renderer node with well-defined bounds. Most renderer nodes implement this
 * interface.
 */
interface BoundableRenderable : Renderable {
    fun getModelNode(): ModelNode?

    fun bounds(): Rectangle?

    fun visualBounds(): Rectangle?

    fun contains(x: Int, y: Int): Boolean

    fun size(): Dimension?

    fun origin(): Point?

    fun getOriginRelativeTo(ancestor: RCollection?): Point

    fun getOriginRelativeToAbs(ancestor: RCollection?): Point?

    fun getOriginRelativeToNoScroll(ancestor: RCollection?): Point?

    /**
     * Gets the parent where the renderable is rendered.
     */
    var parent: RCollection?

    /**
     * Gets the parent set with [.setOriginalParent]. It
     * represents the parent where the renderable would have been originally
     * rendered according to the DOM. This will be non-null only if
     * [.getParent] is not the parent where this renderable would have
     * been originally rendered.
     */
    var originalParent: RCollection?

    /**
     * Returns [.getOriginalParent] if not null. Otherwise it returns
     * [.getParent].
     */
    val originalOrCurrentParent: RCollection?

    fun setBounds(x: Int, y: Int, with: Int, height: Int)

    fun setOrigin(x: Int, y: Int)

    fun x(): Int
    fun setX(x: Int)

    fun y(): Int
    fun setY(y: Int)

    val visualX: Int

    val visualY: Int

    var height: Int

    var width: Int

    val visualHeight: Int

    val visualWidth: Int

    // public Point getRenderablePoint(int guiX, int guiY);
    fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot?

    fun repaint()

    /**
     * Returns false if the event is consumed. True to propagate further.
     */
    fun onMousePressed(event: MouseEvent?, x: Int, y: Int): Boolean

    fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean

    fun onMouseDisarmed(event: MouseEvent?): Boolean

    fun onMouseClick(event: MouseEvent?, x: Int, y: Int): Boolean

    fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean

    fun onMiddleClick(event: MouseEvent?, x: Int, y: Int): Boolean

    fun onRightClick(event: MouseEvent?, x: Int, y: Int): Boolean

    fun onMouseMoved(event: MouseEvent?, x: Int, y: Int, triggerEvent: Boolean, limit: ModelNode?)

    fun onMouseOut(event: MouseEvent?, x: Int, y: Int, limit: ModelNode?)

    /**
     * Returns true if the renderable is fully contained by its modelNode, but
     * said modelNode does not fully contain an ancestor renderable.
     */
    val isContainedByNode: Boolean

    /**
     * Asks the Renderable to paint the selection between two points. Nothing will
     * be done if the points are outside the Renderable.
     *
     * @param g
     * @param inSelection
     * @param startPoint
     * @param endPoint
     * @return True iff it's in selection when finished painting.
     */
    fun paintSelection(
        g: Graphics,
        inSelection: Boolean,
        startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean

    /**
     * Paints by either creating a new clipped graphics context corresponding to
     * the bounds of the Renderable, or by translating the origin.
     *
     * @param g Parent's Graphics context.
     */
    fun paintTranslated(g: Graphics)

    fun extractSelectionText(
        buffer: StringBuffer,
        inSelection: Boolean,
        startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean

    fun repaint(x: Int, y: Int, width: Int, height: Int)

    fun relayout()

    fun getGUIPoint(clientX: Int, clientY: Int): Point?

    fun zIndex(): Int

    fun invalidateLayoutUpTree()

    fun setInnerWidth(newWidth: Int)

    fun setInnerHeight(newHeight: Int)

    fun setDelegator(pDelegator: BoundableRenderable?)

    val isDelegated: Boolean

    val horizontalScrollBarHeight: Int
        get() = 0

    val verticalScrollBarHeight: Int
        get() = 0
}
