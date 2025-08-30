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
 * Created on Apr 17, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.SwingUtilities

/**
 * @author J. H. S.
 */
abstract class BaseBoundableRenderable /*
  public Point getRenderablePoint(final int guiX, final int guiY) {
    final Renderable parent = this.getParent();
    if (parent instanceof BoundableRenderable) {
      return ((BoundableRenderable) parent).getRenderablePoint(guiX - this.x, guiY - this.y);
    } else if (parent == null) {
      return new Point(guiX - this.x, guiY - this.y);
    } else {
      throw new IllegalStateException("parent=" + parent);
    }
  }*/(// protected final Rectangle bounds = new Rectangle();
    protected val container: RenderableContainer?,
    override var modelNode: ModelNode?
) : BaseRenderable(), BoundableRenderable {
    override var width: Int = 0
    override var height: Int = 0
    private var x: Int = 0
    private var y: Int = 0

    /**
     * Starts as true because ancestors could be invalidated.
     */
    var isValid: Boolean = true
        protected set


    private var delegator: BoundableRenderable? = null

    open fun markLayoutValid() {
        this.isValid = true
    }

    override fun getGUIPoint(clientX: Int, clientY: Int): Point? {
        val parent: Renderable? = this.getParent()
        if (parent is BoundableRenderable) {
            return parent.getGUIPoint(clientX + this.x, clientY + this.y)
        } else if (parent == null) {
            return this.container?.getGUIPoint(clientX + this.x, clientY + this.y)
        } else {
            throw IllegalStateException("parent=" + parent)
        }
    }

    open fun getHeight(): Int {
        return height
    }

    open fun setHeight(height: Int) {
        this.height = height
    }

    open fun getWidth(): Int {
        return width
    }

    open fun setWidth(width: Int) {
        this.width = width
    }

    open fun getVisualX(): Int {
        return x()
    }

    open fun getVisualY(): Int {
        return y()
    }

    override fun visualHeight(): Int {
        return getHeight()
    }

    override fun visualWidth(): Int {
        return getWidth()
    }

    override fun x(): Int {
        return x
    }

    override fun setX(x: Int) {
        this.x = x
    }

    override fun y(): Int {
        return y
    }

    override fun setY(y: Int) {
        this.y = y
    }

    override fun contains(x: Int, y: Int): Boolean {
        val mx = this.getVisualX()
        val my = this.getVisualY()
        return (x >= mx) && (y >= my) && (x < (mx + this.visualWidth())) && (y < (my + this.visualHeight()))
    }

    override fun bounds(): Rectangle {
        return Rectangle(this.x, this.y, this.width, this.height)
    }

    /**
     * returns the visual bounds
     * They are distinct from layout bounds when {overflow:visible} or {position:relative} is set on the element
     */
    override fun visualBounds(): Rectangle {
        return Rectangle(getVisualX(), getVisualY(), visualWidth(), visualHeight())
    }

    override fun size(): Dimension? {
        return Dimension(this.width, this.height)
    }

    override fun getModelNode(): ModelNode? {
        return this.modelNode
    }

    // /* (non-Javadoc)
    // * @see net.sourceforge.xamj.domimpl.markup.Renderable#getBounds()
    // */
    // public Rectangle getBounds() {
    // return this.bounds;
    // }
    //
    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    override fun setOrigin(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    protected abstract fun invalidateLayoutLocal()

    /**
     * Invalidates this Renderable and its parent (i.e. all ancestors).
     */
    override fun invalidateLayoutUpTree() {
        if (this.isValid) {
            this.isValid = false
            this.invalidateLayoutLocal()
            // Try original parent first.
            var parent = this.originalParent
            if (parent == null) {
                parent = this.parent
                if (parent == null) {
                    // Has to be top block
                    val rc = this.container
                    if (rc != null) {
                        rc.invalidateLayoutUpTree()
                    }
                } else {
                    parent.invalidateLayoutUpTree()
                }
            } else {
                parent.invalidateLayoutUpTree()
            }
        } else {
        }
    }

    private fun relayoutImpl(invalidateLocal: Boolean, onlyIfValid: Boolean) {
        if (onlyIfValid && !this.isValid) {
            return
        }
        if (invalidateLocal) {
            this.invalidateLayoutUpTree()
        }
        val parent: Renderable? = this.parent
        if (parent is BaseBoundableRenderable) {
            parent.relayoutImpl(false, false)
        } else if (parent == null) {
            // Has to be top RBlock.
            this.container!!.relayout()
        } else {
            if (logger.isLoggable(Level.INFO)) {
                logger.warning("relayout(): Don't know how to relayout " + this + ", parent being " + parent)
            }
        }
    }

    /**
     * Invalidates the current Renderable (which invalidates its ancestors) and
     * then requests the top level GUI container to do the layout and repaint.
     * It's safe to call this method outside the GUI thread.
     */
    override fun relayout() {
        if (SwingUtilities.isEventDispatchThread()) {
            this.relayoutImpl(true, false)
        } else {
            SwingUtilities.invokeLater(Runnable { relayoutImpl(true, false) })
        }
    }

    fun relayoutIfValid() {
        if (SwingUtilities.isEventDispatchThread()) {
            this.relayoutImpl(true, true)
        } else {
            SwingUtilities.invokeLater(Runnable { relayoutImpl(true, true) })
        }
    }

    fun getParent(): RCollection? {
        return this.parent
    }

    fun setParent(parent: RCollection?) {
        this.parent = parent
    }

    /**
     * This is the parent based on the original element hierarchy.
     */
    open fun getOriginalParent(): RCollection? {
        return this.originalParent
    }

    open fun setOriginalParent(origParent: RCollection?) {
        this.originalParent = origParent
    }

    open fun getOriginalOrCurrentParent(): RCollection? {
        val origParent = this.originalParent
        if (origParent == null) {
            return this.parent
        }
        return origParent
    }

    override fun repaint(x: Int, y: Int, width: Int, height: Int) {
        if (isDelegated()) {
            delegator!!.repaint(x, y, width, height)
            return
        }

        val parent: Renderable? = this.parent
        if (parent is BoundableRenderable) {
            parent.repaint(
                x + this.getVisualX(),
                y + this.getVisualY(),
                visualWidth(),
                visualHeight()
            )
        } else if (parent == null) {
            // Has to be top RBlock.
            this.container?.repaint(x, y, width, height)
        } else {
            if (logger.isLoggable(Level.INFO)) {
                logger.warning("repaint(): Don't know how to repaint " + this + ", parent being " + parent)
            }
        }
    }

    override fun repaint() {
        this.repaint(0, 0, this.width, this.height)
    }

    open fun blockBackgroundColor(): Color? {
      return this.container!!.paintedBackgroundColor()
    }

    override fun paintTranslated(g: Graphics) {
        val x = this.x
        val y = this.y
        g.translate(x, y)
        try {
            this.paint(g)
        } finally {
            g.translate(-x, -y)
        }
    }

    override fun onMouseOut(event: MouseEvent?, x: Int, y: Int, limit: ModelNode?) {
        if (this.isContainedByNode) {
            HtmlController.Companion.instance.onMouseOut(this.modelNode, event, x, y, limit)
        }
    }

    override fun onMouseMoved(
        event: MouseEvent?,
        x: Int,
        y: Int,
        triggerEvent: Boolean,
        limit: ModelNode?
    ) {
        if (triggerEvent) {
            if (this.isContainedByNode) {
                HtmlController.Companion.instance
                    .onMouseOver(this, this.modelNode, event, x, y, limit)
            }
        }
    }

    override fun origin(): Point? {
        return Point(this.x, this.y)
    }

    override fun getOriginRelativeTo(ancestor: RCollection?): Point {
        if (ancestor === this) {
            return Point(0, 0)
        }

        var x = this.getVisualX()
        var y = this.getVisualY()

        var parent = this.parent
        while (true) {
            if (parent == null) {
                // throw new java.lang.IllegalArgumentException("Not an ancestor: " + ancestor);
                /* This condition can legitimately happen when mousing-out of an old
                 * renderable which is no longer part of the render hierarchy due to a
                 * layout change between the mouse-in and mouse-out events.
                 */
                return Point(x, y)
            }
            if (parent === ancestor) {
                return Point(x, y)
            }
            x += parent.visualX
            y += parent.visualY
            parent = parent.parent
        }
    }

    override fun getOriginRelativeToAbs(ancestor: RCollection?): Point? {
        if (ancestor === this) {
            return Point(0, 0)
        }

        var x = this.getVisualX()
        var y = this.getVisualY()

        var nextX = 0
        var nextY = 0

        var parent = this.parent
        while (true) {
            if (parent == null) {
                // throw new java.lang.IllegalArgumentException("Not an ancestor: " + ancestor);
                /* This condition can legitimately happen when mousing-out of an old
                 * renderable which is no longer part of the render hierarchy due to a
                 * layout change between the mouse-in and mouse-out events.
                 */
                return Point(x, y)
            }
            if (parent === ancestor) {
                return Point(x, y)
            }
            x += nextX
            y += nextY
            nextX = parent.visualX
            nextY = parent.visualY
            parent = parent.parent
        }
    }

    override fun getOriginRelativeToNoScroll(ancestor: RCollection?): Point? {
        if (ancestor === this) {
            return Point(0, 0)
        }

        var x = this.getVisualX()
        var y = this.getVisualY()


        if (this is RBlockViewport) {
            x -= this.scrollX
            y -= this.scrollY
        }

        var parent = this.parent
        while (true) {
            if (parent == null) {
                // throw new java.lang.IllegalArgumentException("Not an ancestor: " + ancestor);
                /* This condition can legitimately happen when mousing-out of an old
                 * renderable which is no longer part of the render hierarchy due to a
                 * layout change between the mouse-in and mouse-out events.
                 */
                return Point(x, y)
            }
            if (parent === ancestor) {
                return Point(x, y)
            }
            x += parent.visualX
            y += parent.visualY
            parent = parent.parent
        }
    }

    override fun setInnerWidth(newWidth: Int) {
        setWidth(newWidth)
    }

    override fun setInnerHeight(newHeight: Int) {
        setHeight(newHeight)
    }

    override fun setDelegator(pDelegator: BoundableRenderable?) {
        this.delegator = pDelegator
    }

    override fun isDelegated(): Boolean {
        return delegator != null
    }

    override fun onMiddleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.instance.onMiddleClick(me, event, x, y)
        } else {
            return true
        }
    }

    companion object {
        @JvmStatic
        protected val logger: Logger =
            Logger.getLogger(BaseBoundableRenderable::class.java.name)

        @JvmStatic
        protected val SELECTION_COLOR: Color? = Color.BLUE

        @JvmStatic
        protected val SELECTION_XOR: Color? = Color.LIGHT_GRAY
    }
}
