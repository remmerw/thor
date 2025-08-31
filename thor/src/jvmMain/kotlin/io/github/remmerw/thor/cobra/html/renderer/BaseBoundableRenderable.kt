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

abstract class BaseBoundableRenderable(
    protected val container: RenderableContainer?,
    private var modelNode: ModelNode?
) : BaseRenderable(), BoundableRenderable {
    private var width: Int = 0
    private var height: Int = 0
    private var x: Int = 0
    private var y: Int = 0
    private var parent: RCollection? = null
    private var originalParent: RCollection? = null
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
        val parent: Renderable? = this.parent()
        if (parent is BoundableRenderable) {
            return parent.getGUIPoint(clientX + this.x, clientY + this.y)
        } else if (parent == null) {
            return this.container?.getGUIPoint(clientX + this.x, clientY + this.y)
        } else {
            throw IllegalStateException("parent=" + parent)
        }
    }

    override fun height(): Int {
        return height
    }

    override fun setHeight(height: Int) {
        this.height = height
    }

    override fun width(): Int {
        return width
    }

    override fun setWidth(width: Int) {
        this.width = width
    }

    override fun visualX(): Int {
        return x()
    }

    override fun visualY(): Int {
        return y()
    }

    override fun visualHeight(): Int {
        return height()
    }

    override fun visualWidth(): Int {
        return width()
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
        val mx = this.visualX()
        val my = this.visualY()
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
        return Rectangle(visualX(), visualY(), visualWidth(), visualHeight())
    }

    override fun size(): Dimension? {
        return Dimension(this.width, this.height)
    }

    override fun modelNode(): ModelNode? {
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

    override fun parent(): RCollection? {
        return this.parent
    }

    override fun setParent(parent: RCollection?) {
        this.parent = parent
    }

    /**
     * This is the parent based on the original element hierarchy.
     */
    override fun originalParent(): RCollection? {
        return this.originalParent
    }

    override fun setOriginalParent(origParent: RCollection?) {
        this.originalParent = origParent
    }

    override fun originalOrCurrentParent(): RCollection? {
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
                x + this.visualX(),
                y + this.visualY(),
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
        if (this.isContainedByNode()) {
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
            if (this.isContainedByNode()) {
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

        var x = this.visualX()
        var y = this.visualY()

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
            x += parent.visualX()
            y += parent.visualY()
            parent = parent.parent()
        }
    }

    override fun getOriginRelativeToAbs(ancestor: RCollection?): Point? {
        if (ancestor === this) {
            return Point(0, 0)
        }

        var x = this.visualX()
        var y = this.visualY()

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
            nextX = parent.visualX()
            nextY = parent.visualY()
            parent = parent.parent()
        }
    }

    override fun getOriginRelativeToNoScroll(ancestor: RCollection?): Point? {
        if (ancestor === this) {
            return Point(0, 0)
        }

        var x = this.visualX()
        var y = this.visualY()


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
            x += parent.visualX()
            y += parent.visualY()
            parent = parent.parent()
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
