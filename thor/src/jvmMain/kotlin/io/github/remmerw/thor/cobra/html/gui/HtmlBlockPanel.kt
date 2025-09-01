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
 * Created on Apr 16, 2005
 */
package io.github.remmerw.thor.cobra.html.gui

import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.HTMLDocumentImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.domimpl.UINode
import io.github.remmerw.thor.cobra.html.renderer.BoundableRenderable
import io.github.remmerw.thor.cobra.html.renderer.DelayedPair
import io.github.remmerw.thor.cobra.html.renderer.FrameContext
import io.github.remmerw.thor.cobra.html.renderer.NodeRenderer
import io.github.remmerw.thor.cobra.html.renderer.PositionedRenderable
import io.github.remmerw.thor.cobra.html.renderer.RBlock
import io.github.remmerw.thor.cobra.html.renderer.RCollection
import io.github.remmerw.thor.cobra.html.renderer.RElement
import io.github.remmerw.thor.cobra.html.renderer.Renderable
import io.github.remmerw.thor.cobra.html.renderer.RenderableContainer
import io.github.remmerw.thor.cobra.html.renderer.RenderableSpot
import io.github.remmerw.thor.cobra.html.renderer.TranslatedRenderable
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.util.Nodes
import io.github.remmerw.thor.cobra.util.gui.ColorFactory
import org.w3c.dom.Node
import java.awt.Adjustable
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.JComponent
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import kotlin.concurrent.Volatile
import kotlin.math.max

/**
 * A Swing component that renders a HTML block, given by a DOM root or an
 * internal element, typically a DIV. This component *cannot* render
 * FRAMESETs. `HtmlBlockPanel` is used by [HtmlPanel] whenever
 * the DOM is determined *not* to be a FRAMESET.
 *
 * @author J. H. S.
 * @see HtmlPanel
 *
 * @see FrameSetPanel
 */
class HtmlBlockPanel(
    background: Color?,
    opaque: Boolean,
    pcontext: UserAgentContext?,
    rcontext: HtmlRendererContext?,
    frameContext: FrameContext
) : JComponent(), NodeRenderer, RenderableContainer, ClipboardOwner {
    protected val frameContext: FrameContext
    protected val ucontext: UserAgentContext?
    protected val rcontext: HtmlRendererContext?

    protected var startSelection: RenderableSpot? = null
    protected var endSelection: RenderableSpot? = null
    protected var rblock: RBlock? = null
    private var preferredWidth: Int = -1
    private var defaultOverflowX: Int = RenderState.OVERFLOW_AUTO
    private var defaultOverflowY: Int = RenderState.OVERFLOW_SCROLL

    @Volatile
    private var scrollCompleted = false
    private var mousePressTarget: BoundableRenderable? = null
    private var desktopHints: MutableMap<*, *>? = null
    private var applyRenderHints = true
    private var processingDocumentNotification = false
    private var components: MutableSet<Component?>? = null
    private var layoutCompleted = CompletableFuture<Boolean?>()

    constructor(
        pcontext: UserAgentContext?,
        rcontext: HtmlRendererContext?,
        frameContext: FrameContext
    ) : this(ColorFactory.TRANSPARENT, false, pcontext, rcontext, frameContext)

    init {
        this.layout = null
        this.autoscrolls = true
        this.frameContext = frameContext
        this.ucontext = pcontext
        this.rcontext = rcontext
        this.isOpaque = opaque
        this.setBackground(background)
        val actionListener: ActionListener = object : ActionListener {
            override fun actionPerformed(e: ActionEvent) {
                val command = e.getActionCommand()
                if ("copy" == command) {
                    copy()
                }
            }
        }
        if (!GraphicsEnvironment.isHeadless()) {
            this.registerKeyboardAction(
                actionListener,
                "copy",
                KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0),
                WHEN_FOCUSED
            )
            this.registerKeyboardAction(
                actionListener, "copy",
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_C,
                    Toolkit.getDefaultToolkit().menuShortcutKeyMask
                ), WHEN_FOCUSED
            )
        }
        this.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                onMouseClick(e)
            }

            override fun mouseEntered(e: MouseEvent?) {
            }

            override fun mouseExited(e: MouseEvent?) {
                onMouseExited(e)
            }

            override fun mousePressed(e: MouseEvent) {
                onMousePressed(e)
            }

            override fun mouseReleased(e: MouseEvent) {
                onMouseReleased(e)
            }
        })
        this.addMouseMotionListener(object : MouseMotionListener {
            /*
             * (non-Javadoc)
             *
             * @see
             * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
             * )
             */
            override fun mouseDragged(e: MouseEvent) {
                onMouseDragged(e)
            }

            /*
             * (non-Javadoc)
             *
             * @see
             * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent
             * )
             */
            override fun mouseMoved(arg0: MouseEvent) {
                onMouseMoved(arg0)
            }
        })
        this.addMouseWheelListener(object : MouseWheelListener {
            override fun mouseWheelMoved(e: MouseWheelEvent) {
                onMouseWheelMoved(e)
            }
        })
    }

    /**
     * Scrolls the body area to the given location.
     *
     *
     * This method should be called from the GUI thread.
     *
     * @param bounds    The bounds in the scrollable block area that should become
     * visible.
     * @param xIfNeeded If this parameter is true, scrolling will only occur if the
     * requested bounds are not currently visible horizontally.
     * @param yIfNeeded If this parameter is true, scrolling will only occur if the
     * requested bounds are not currently visible vertically.
     */
    fun scrollTo(bounds: Rectangle, xIfNeeded: Boolean, yIfNeeded: Boolean) {
        val doc = getRootNode() as HTMLDocumentImpl?
        if (doc != null) {
            val bodyBlock = (doc.body as HTMLElementImpl).uINode as RBlock?
            bodyBlock!!.scrollTo(bounds, xIfNeeded, yIfNeeded)
        }
    }

    fun scrollBy(xOffset: Int, yOffset: Int) {
        val block = this.rblock
        if (block != null) {
            if (xOffset != 0) {
                block.scrollBy(Adjustable.HORIZONTAL, xOffset)
            }
            if (yOffset != 0) {
                block.scrollBy(Adjustable.VERTICAL, yOffset)
            }
        }
    }

    /**
     * Scrolls the body area to the node given, if it is part of the current
     * document.
     *
     *
     * This method should be called from the GUI thread.
     *
     * @param node A DOM node.
     */
    fun scrollTo(node: Node?) {
        val bounds = this.getNodeBounds(node, true)
        if (bounds == null) {
            return
        }
        this.scrollTo(bounds, true, false)
    }

    /**
     * Gets the rectangular bounds of the given node.
     *
     *
     * This method should be called from the GUI thread.
     *
     * @param node                 A node in the current document.
     * @param relativeToScrollable Whether the bounds should be relative to the scrollable body area.
     * Otherwise, they are relative to the root block (which is the
     * essentially the same as being relative to this
     * `HtmlBlockPanel` minus Swing borders).
     */
    fun getNodeBounds(node: Node?, relativeToScrollable: Boolean): Rectangle? {
        val block = this.rblock
        if (block == null) {
            return null
        }
        // Find UINode first
        var currentNode = node
        var uiNode: UINode? = null
        while (currentNode != null) {
            if (currentNode is HTMLElementImpl) {
                uiNode = currentNode.uINode
                if (uiNode != null) {
                    break
                }
            }
            currentNode = currentNode.parentNode
        }
        if (uiNode == null) {
            return null
        }
        val relativeTo: RCollection? =
            if (relativeToScrollable) block.rBlockViewport else block
        if (node === currentNode) {
            val br = uiNode as BoundableRenderable
            val guiPoint = br.getOriginRelativeTo(relativeTo)
            val size = br.size()
            return Rectangle(guiPoint, size)
        } else {
            return this.scanNodeBounds(uiNode as RCollection, node, relativeTo)
        }
    }

    /**
     * Gets an aggregate of the bounds of renderer leaf nodes.
     */
    private fun scanNodeBounds(
        root: RCollection,
        node: Node?,
        relativeTo: RCollection?
    ): Rectangle? {
        val i = root.getRenderables(false)
        var resultBounds: Rectangle? = null
        var prevBoundable: BoundableRenderable? = null
        if (i != null) {
            while (i.hasNext()) {
                val rn = i.next()
                val r: Renderable =
                    (if (rn is PositionedRenderable) ((rn as PositionedRenderable).renderable()) else rn)!!
                var subBounds: Rectangle? = null
                if (r is RCollection) {
                    prevBoundable = r
                    subBounds = this.scanNodeBounds(r, node, relativeTo)
                } else if (r is BoundableRenderable) {
                    prevBoundable = r
                    if (Nodes.isSameOrAncestorOf(node, r.modelNode() as Node)) {
                        val origin = r.getOriginRelativeTo(relativeTo)
                        val size = r.size()
                        subBounds = Rectangle(origin, size)
                    }
                } else {
                    // This would have to be a RStyleChanger. We rely on these
                    // when the target node has blank content.
                    if (Nodes.isSameOrAncestorOf(node, r.modelNode() as Node)) {
                        val xInRoot =
                            if (prevBoundable == null) 0 else prevBoundable.visualX() + prevBoundable.visualWidth()
                        val rootOrigin = root.getOriginRelativeTo(relativeTo)
                        subBounds = Rectangle(
                            rootOrigin.x + xInRoot,
                            rootOrigin.y,
                            0,
                            root.visualHeight()
                        )
                    }
                }
                if (subBounds != null) {
                    if (resultBounds == null) {
                        resultBounds = subBounds
                    } else {
                        resultBounds = subBounds.union(resultBounds)
                    }
                }
            }
        }
        return resultBounds
    }

    val rootRenderable: BoundableRenderable?
        get() = this.rblock

    /**
     * Allows [.getPreferredSize] to render the HTML block in order to
     * determine the preferred size of this component. Note that
     * `getPreferredSize()` is a potentially time-consuming
     * operation if the preferred width is set.
     *
     * @param width The preferred blocked width. Use `-1` to unset.
    `` */
    fun setPreferredWidth(width: Int) {
        this.preferredWidth = width
    }

    /**
     * If the preferred size has been set with
     * [.setPreferredSize], then that size is returned. Otherwise
     * a preferred size is calculated by rendering the HTML DOM, provided one is
     * available and a preferred width other than `-1` has been set
     * with [.setPreferredWidth]. An arbitrary preferred size is
     * returned in other scenarios.
     */
    override fun getPreferredSize(): Dimension? {
        // Expected to be invoked in the GUI thread.
        if (this.isPreferredSizeSet) {
            return super.getPreferredSize()
        }
        val pw = this.preferredWidth
        if (pw != -1) {
            val block = this.rblock
            if (block != null) {
                // Layout should always be done in the GUI thread.
                if (SwingUtilities.isEventDispatchThread()) {
                    block.layout(
                        pw,
                        0,
                        false,
                        false,
                        RenderState.OVERFLOW_VISIBLE,
                        RenderState.OVERFLOW_VISIBLE,
                        true
                    )
                } else {
                    try {
                        SwingUtilities.invokeAndWait(object : Runnable {
                            override fun run() {
                                block.layout(
                                    pw,
                                    0,
                                    false,
                                    false,
                                    RenderState.OVERFLOW_VISIBLE,
                                    RenderState.OVERFLOW_VISIBLE,
                                    true
                                )
                            }
                        })
                    } catch (err: Exception) {
                        logger.log(Level.SEVERE, "Unable to do preferred size layout.", err)
                    }
                }
                // Adjust for permanent vertical scrollbar.
                val newPw = max(block.width() + block.vScrollBarWidth, pw)
                return Dimension(newPw, block.height())
            }
        }
        return Dimension(600, 400)
    }


    fun copy(): Boolean {
        val selection = this@HtmlBlockPanel.selectionText
        if (selection != null) {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(StringSelection(selection), this@HtmlBlockPanel)
            return true
        } else {
            return false
        }
    }

    val firstLineHeight: Int
        get() {
            val block = this.rblock
            return if (block == null) 0 else block.firstLineHeight
        }

    fun setSelectionEnd(rpoint: RenderableSpot?) {
        this.endSelection = rpoint
    }

    fun setSelectionStart(rpoint: RenderableSpot?) {
        this.startSelection = rpoint
    }

    val isSelectionAvailable: Boolean
        get() {
            val start = this.startSelection
            val end = this.endSelection
            return (start != null) && (end != null) && (start != end)
        }

    val selectionNode: Node?
        get() {
            val start = this.startSelection
            val end = this.endSelection
            if ((start != null) && (end != null)) {
                return Nodes.getCommonAncestor(
                    start.renderable.modelNode() as Node?,
                    end.renderable.modelNode() as Node?
                )
            } else {
                return null
            }
        }

    protected fun validateAll() {
        var toValidate: Component? = this
        while (true) {
            val parent = toValidate!!.getParent()
            if ((parent == null) || parent.isValid) {
                break
            }
            toValidate = parent
        }
        toValidate.validate()
    }

    protected fun revalidatePanel() {
        // Called in the GUI thread.
        this.invalidate()
        this.validate()
        // TODO: Could be paintImmediately.
        this.repaint()
    }

    private fun getRootNode(): NodeImpl? {
        val block = this.rblock
        return if (block == null) null else block.modelNode() as NodeImpl?
    }

    /**
     * Sets the root node to render. This method should be invoked in the GUI
     * dispatch thread.
     */
    override fun setRootNode(node: NodeImpl?) {
        scrollCompleted = false
        layoutCompleted = CompletableFuture<Boolean?>()
        if (node != null) {
            val block = RBlock(node, 0, this.ucontext, this.rcontext, this.frameContext, this)
            block.defaultOverflowX = this.defaultOverflowX
            block.defaultOverflowY = this.defaultOverflowY
            node.uINode = block
            this.rblock = block
        } else {
            this.rblock = null
        }
        this.invalidate()
        this.validateAll()
        this.repaint()
    }

    private fun onMouseClick(event: MouseEvent) {
        // Rely on AWT mouse-click only for double-clicks
        val block = this.rblock
        if (block != null) {
            val clickCount = event.getClickCount()
            if (SwingUtilities.isLeftMouseButton(event) && (clickCount > 1)) {
                // TODO: Double-click must be revised. It generates
                // a single click via mouse release.
                val point = event.point
                block.onDoubleClick(event, point.x, point.y)
            } else if (SwingUtilities.isMiddleMouseButton(event) && (clickCount == 1)) {
                block.onMiddleClick(event, event.getX(), event.getY())
            } else if (SwingUtilities.isRightMouseButton(event) && (clickCount == 1)) {
                block.onRightClick(event, event.getX(), event.getY())
            }
        }
    }

    private fun onMousePressed(event: MouseEvent) {
        this.requestFocus()
        val block = this.rblock
        if (block != null) {
            val point = event.point
            this.mousePressTarget = block
            val rx = point.x
            val ry = point.y
            block.onMousePressed(event, rx, ry)
            val rp = block.getLowestRenderableSpot(rx, ry)
            this.frameContext.resetSelection(rp)
        }
    }

    private fun onMouseReleased(event: MouseEvent) {
        val block = this.rblock
        if (block != null) {
            val point = event.point
            val rx = point.x
            val ry = point.y
            if (SwingUtilities.isLeftMouseButton(event)) {
                // TODO: This will be raised twice on a double-click.
                if (event.isControlDown) {
                    block.onMiddleClick(event, rx, ry)
                } else {
                    block.onMouseClick(event, rx, ry)
                }
            } else if (SwingUtilities.isRightMouseButton(event)) {
                block.onRightClick(event, rx, ry)
            }
            block.onMouseReleased(event, rx, ry)
            val oldTarget = this.mousePressTarget
            if (oldTarget != null) {
                this.mousePressTarget = null
                if (oldTarget !== block) {
                    oldTarget.onMouseDisarmed(event)
                }
            }
        } else {
            this.mousePressTarget = null
        }
    }

    private fun onMouseExited(event: MouseEvent?) {
        val oldTarget = this.mousePressTarget
        if (oldTarget != null) {
            this.mousePressTarget = null
            oldTarget.onMouseDisarmed(event)
        }
    }

    private fun getInnerMostRenderable(x: Int, y: Int): Renderable? {
        val block = this.rblock
        var r = block!!.getRenderable(x - block.visualX(), y - block.visualY())

        var xi = x
        var yi = y
        var inner: BoundableRenderable? = null
        var prevR: BoundableRenderable? = null
        do {
            if (r is RCollection) {
                if (prevR != null) {
                    val oi = prevR.getOriginRelativeTo(r)
                    xi -= oi.x
                    yi -= oi.y
                }

                // xi -= rc.getVisualX();
                // yi -= rc.getVisualY();
                inner = r.getRenderable(xi, yi)
                if (inner != null) {
                    prevR = r
                    r = inner
                }
            } else {
                inner = null
            }
        } while (inner != null)

        return r
    }

    private fun getContainingBlock(r: Renderable?): RBlock? {
        if (r is RBlock) {
            return r
        } else if (r is TranslatedRenderable) {
            return getContainingBlock(r.child)
        } else if (r == null) {
            return null
        } else if (r is BoundableRenderable) {
            return getContainingBlock(r.parent())
        } else {
            return null
        }
    }

    private fun onMouseWheelMoved(mwe: MouseWheelEvent) {
        val block = this.rblock
        if (block != null) {
            when (mwe.getScrollType()) {
                MouseWheelEvent.WHEEL_UNIT_SCROLL -> {
                    val factor = if (mwe.isShiftDown) 2 else 1
                    val units = mwe.getWheelRotation() * mwe.getScrollAmount() * factor
                    val innerMostRenderable = getInnerMostRenderable(mwe.getX(), mwe.getY())
                    var consumed = false
                    var innerBlock = getContainingBlock(innerMostRenderable)
                    do {
                        if (innerBlock != null) {
                            consumed = innerBlock.scrollByUnits(Adjustable.VERTICAL, units)
                            innerBlock = getContainingBlock(innerBlock.parent())
                        }
                    } while ((!consumed) && (innerBlock != null))
                }
            }
        }
    }

    private fun onMouseDragged(event: MouseEvent) {
        val block = this.rblock
        if (block != null) {
            val point = event.point
            val rp = block.getLowestRenderableSpot(point.x, point.y)
            if (rp != null) {
                this.frameContext.expandSelection(rp)
            }
            block.ensureVisible(point)
        }
    }

    private fun onMouseMoved(event: MouseEvent) {
        val block = this.rblock
        if (block != null) {
            val point = event.point
            block.onMouseMoved(event, point.x, point.y, false, null)
        }
    }

    fun disableRenderHints() {
        this.applyRenderHints = false
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    // protected void paintComponent(Graphics g) {
    override fun paint(g: Graphics) {
        // We go against Sun's advice and override
        // paint() instead of paintComponent(). Scrollbars
        // do not repaint correctly if we use
        // paintComponent.
        if (this.isOpaque) {
            // Background not painted by default in JComponent.
            val clipBounds = g.clipBounds
            g.color = this.getBackground()
            g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height)
        }
        if (applyRenderHints && g is Graphics2D) {
            if (desktopHints == null) {
                desktopHints = (Toolkit.getDefaultToolkit()
                    .getDesktopProperty("awt.font.desktophints")) as MutableMap<*, *>?
            }
            if (desktopHints != null) {
                g.addRenderingHints(desktopHints)
            } else {
                try {
                    g.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP
                    )
                } catch (e: NoSuchFieldError) {
                    g.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                    )
                }
            }

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }
        val block = this.rblock
        if (block != null) {
            block.paint(g)

            // Paint FrameContext selection
            val start = this.startSelection
            val end = this.endSelection
            if ((start != null) && (end != null) && (start != end)) {
                block.paintSelection(g, false, start, end)
            }
        }

        // For debugging
        // drawGrid(g);
    }

    private fun drawGrid(g: Graphics) {
        val GRID_SIZE = 50
        val OFFSET_X = 0
        val OFFSET_Y = 0
        // Draw grid for debug
        val clipBounds = g.clipBounds
        g.color = Color(0, 0, 0, 30)
        var i = 0
        while (i < clipBounds.width) {
            g.drawLine(i + OFFSET_X, 0, i + OFFSET_X, clipBounds.height)
            i += GRID_SIZE
        }
        var j = 0
        while (j < clipBounds.height) {
            g.drawLine(0, j + OFFSET_Y, clipBounds.width, j + OFFSET_Y)
            j += GRID_SIZE
        }
    }

    override fun doLayout() {
        val rootNode = getRootNode()
        if (rootNode is HTMLDocumentImpl) {
            val layoutBlocked = rootNode.layoutBlocked.get()
            if (layoutBlocked) {
                return
            }

            // Note: There were issues with this previously. See GH #147
            rootNode.primeNodeData()
        }

        try {
            val size = this.size
            this.clearComponents()
            val block = this.rblock
            if (block != null) {
                block.layout(size.width, size.height, true, true, null, false)
                // Only set origin
                block.setOrigin(0, 0)
                block.updateWidgetBounds(0, 0)
                this.updateGUIComponents()
                // dumpRndTree(block);
                if (!scrollCompleted) {
                    scrollCompleted = true
                    if (rootNode is HTMLDocumentImpl) {
                        val ref = rootNode.getDocumentURL()!!.ref
                        if (ref != null && ref.length > 0) {
                            scrollTo(rootNode.getElementById(ref))
                        }
                    }
                }
                layoutCompleted.complete(true)
            } else {
                if (this.componentCount > 0) {
                    this.removeAll()
                }
            }
        } catch (thrown: Exception) {
            logger.log(
                Level.SEVERE,
                "Unexpected error in layout engine. Document is " + this.getRootNode(),
                thrown
            )
        }
    }

    /**
     * Implementation of UINode.repaint().
     */
    fun repaint(modelNode: ModelNode?) {
        // this.rblock.invalidateRenderStyle();
        this.repaint()
    }

    val selectionText: String?
        get() {
            val start = this.startSelection
            val end = this.endSelection
            if ((start != null) && (end != null)) {
                val buffer = StringBuffer()
                this.rblock!!.extractSelectionText(buffer, false, start, end)
                return buffer.toString()
            } else {
                return null
            }
        }

    fun hasSelection(): Boolean {
        val start = this.startSelection
        val end = this.endSelection
        return (start != null) && (end != null) && (start != end)
    }

    override fun paintChildren(g: Graphics?) {
        // Overridding with NOP. For various reasons,
        // the regular mechanism for painting children
        // needs to be handled by Cobra.
    }

    fun getPaintedBackgroundColor(): Color? {
        return if (this.isOpaque) this.getBackground() else null
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer
     * .Clipboard, java.awt.datatransfer.Transferable)
     */
    override fun lostOwnership(arg0: Clipboard?, arg1: Transferable?) {
    }

    override fun relayout() {
        // Expected to be called in the GUI thread.
        // Renderable branch should be invalidated at this
        // point, but this GUI component not necessarily.
        this.revalidatePanel()
    }

    override fun invalidateLayoutUpTree() {
        // Called when renderable branch is invalidated.
        // We shouldn't do anything here. Changes in renderer
        // tree do not have any bearing on validity of GUI
        // component.
    }

    override fun updateAllWidgetBounds() {
        this.rblock!!.updateWidgetBounds(0, 0)
    }

    override fun paintedBackgroundColor(): Color? {
        TODO("Not yet implemented")
    }


    override fun getGUIPoint(clientX: Int, clientY: Int): Point {
        // This is the GUI!
        return Point(clientX, clientY)
    }

    override fun focus() {
        this.grabFocus()
    }

    fun processDocumentNotifications(notifications: List<DocumentNotification>) {
        // Called in the GUI thread.
        check(!this.processingDocumentNotification) { "Recursive" }
        this.processingDocumentNotification = true
        try {
            // Note: It may be assumed that usually only generic
            // notifications come in batches. Other types
            // of noitifications probably come one by one.
            var topLayout = false
            var repainters: ArrayList<RElement>? = null
            val length = notifications.size
            for (i in 0..<length) {
                val dn = notifications[i]
                val type = dn.type
                when (type) {
                    DocumentNotification.Companion.GENERIC, DocumentNotification.Companion.SIZE -> {
                        val node = dn.node
                        if (node == null) {
                            // This is all-invalidate (new style sheet)
                            if (loggableInfo) {
                                logger.info("processDocumentNotifications(): Calling invalidateLayoutDeep().")
                            }
                            this.rblock!!.invalidateLayoutDeep()
                            // this.rblock.invalidateRenderStyle();
                        } else {
                            val uiNode = node.findUINode()
                            if (uiNode != null) {
                                val relement = uiNode as RElement
                                relement.invalidateLayoutUpTree()
                                relement.invalidateLayoutDeep()
                                // if(type == DocumentNotification.GENERIC) {
                                // relement.invalidateRenderStyle();
                                // }
                            } else {
                                if (loggableInfo) {
                                    logger.info("processDocumentNotifications(): Unable to find UINode for " + node)
                                }
                            }
                        }
                        topLayout = true
                    }

                    DocumentNotification.Companion.POSITION -> {
                        // TODO: Could be more efficient.
                        val node = dn.node
                        val parent = node?.getParentNode() as NodeImpl?
                        if (parent != null) {
                            val uiNode = parent.findUINode()
                            if (uiNode != null) {
                                val relement = uiNode as RElement
                                relement.invalidateLayoutUpTree()
                            }
                        }
                        topLayout = true
                    }

                    DocumentNotification.Companion.LOOK -> {
                        val node = dn.node
                        val uiNode = node?.findUINode()
                        if (uiNode != null) {
                            if (repainters == null) {
                                repainters = ArrayList<RElement>(1)
                            }
                            val relement = uiNode as RElement
                            relement.invalidateRenderStyle()
                            repainters.add(relement)
                        }
                    }

                    else -> {}
                }
            }
            if (topLayout) {
                this.revalidatePanel()
            } else {
                if (repainters != null) {
                    val i: MutableIterator<RElement> = repainters.iterator()
                    while (i.hasNext()) {
                        val element = i.next()
                        element.repaint()
                    }
                }
            }
        } finally {
            this.processingDocumentNotification = false
        }
    }

    override fun addDelayedPair(pair: DelayedPair) {
        throw UnsupportedOperationException("Delayed pairs are not being handled at this level.")
    }

    override fun delayedPairs(): MutableCollection<DelayedPair>? {
        TODO("Not yet implemented")
    }


    override fun parentContainer(): RenderableContainer? {
       return null
    }


    fun getDelayedPairs(): MutableCollection<DelayedPair?>? {
        throw UnsupportedOperationException("Delayed pairs are not being handled at this level.")
    }

    override fun clearDelayedPairs() {
        throw UnsupportedOperationException("Delayed pairs are not being handled at this level.")
    }

    override fun height(): Int {
        TODO("Not yet implemented")
    }

    override fun setHeight(height: Int) {
        TODO("Not yet implemented")
    }

    override fun width(): Int {
        TODO("Not yet implemented")
    }

    override fun setWidth(width: Int) {
        TODO("Not yet implemented")
    }


    override fun x(): Int {
        TODO("Not yet implemented")
    }

    override fun setX(x: Int) {
        TODO("Not yet implemented")
    }

    override fun y(): Int {
        TODO("Not yet implemented")
    }

    override fun setY(y: Int) {
        TODO("Not yet implemented")
    }


    private fun clearComponents() {
        val c: MutableSet<Component?>? = this.components
        if (c != null) {
            c.clear()
        }
    }

    override fun addComponent(component: Component?): Component? {
        var c: MutableSet<Component?>? = this.components
        if (c == null) {
            c = HashSet<Component?>()
            this.components = c
        }
        if (c.add(component)) {
            return component
        } else {
            return null
        }
    }

    private fun updateGUIComponents() {
        // We use this method, instead of removing all components and
        // adding them back, because removal of components can cause
        // them to lose focus.

        val c: MutableSet<Component?>? = this.components
        if (c == null) {
            if (this.componentCount != 0) {
                this.removeAll()
            }
        } else {
            // Remove children not in the set.
            val workingSet: MutableSet<Component?> = HashSet<Component?>()
            workingSet.addAll(c)
            var count = this.componentCount
            var i = 0
            while (i < count) {
                val component = this.getComponent(i)
                if (!c.contains(component)) {
                    this.remove(i)
                    count = this.componentCount
                } else {
                    i++
                    workingSet.remove(component)
                }
            }
            // Add components in set that were not previously children.
            val wsi = workingSet.iterator()
            while (wsi.hasNext()) {
                val component = wsi.next()
                this.add(component)
            }
        }
    }

    fun getDefaultOverflowX(): Int {
        return defaultOverflowX
    }

    fun setDefaultOverflowX(defaultOverflowX: Int) {
        if (defaultOverflowX != this.defaultOverflowX) {
            this.defaultOverflowX = defaultOverflowX
            val block = this.rblock
            if (block != null) {
                block.defaultOverflowX = defaultOverflowX
                block.relayoutIfValid()
            }
        }
    }

    fun getDefaultOverflowY(): Int {
        return defaultOverflowY
    }

    fun setDefaultOverflowY(defaultOverflowY: Int) {
        if (this.defaultOverflowY != defaultOverflowY) {
            this.defaultOverflowY = defaultOverflowY
            val block = this.rblock
            if (block != null) {
                block.defaultOverflowY = defaultOverflowY
                block.relayoutIfValid()
            }
        }
    }

    override fun getInsets(hscroll: Boolean, vscroll: Boolean): Insets? {
        throw UnsupportedOperationException(
            "Method added while implementing absolute positioned elements inside relative elements. But not implemented yet."
        )
    }

    override fun getInsetsMarginBorder(hscroll: Boolean, vscroll: Boolean): Insets? {
        throw UnsupportedOperationException("Method added while fixing #32. Not implemented yet.")
    }

    override fun visualBounds(): Rectangle? {
        TODO("Not yet implemented")
    }



    override fun visualHeight(): Int {
        return rblock!!.visualHeight()
    }

    override fun visualWidth(): Int {
        return rblock!!.visualWidth()
    }

    fun getVisualBounds(): Rectangle {
        return Rectangle(x, y, visualWidth(), visualHeight())
    }

    override fun translateDescendantPoint(descendant: BoundableRenderable, x: Int, y: Int): Point {
        return rblock!!.translateDescendantPoint(descendant, x, y)
    }

    override fun getOriginRelativeTo(bodyLayout: RCollection?): Point? {
        // TODO Auto-generated method stub
        return null
    }

    override fun getOriginRelativeToAbs(bodyLayout: RCollection?): Point? {
        // TODO Auto-generated method stub
        return null
    }

    fun layoutCompletion(): Future<Boolean?> {
        return layoutCompleted
    }

    val isReadyToPaint: Boolean
        get() {
            val block = this.rblock
            if (block != null) {
                val doc = block.modelNode() as HTMLDocumentImpl
                return  block.isReadyToPaint()
            }
            return false
        }

    companion object {

        private val logger: Logger = Logger.getLogger(HtmlBlockPanel::class.java.name)
        private val loggableInfo: Boolean = logger.isLoggable(Level.INFO)

        @Suppress("unused")
        private fun dumpRndTree(root: Renderable) {
            println("------------------------------")
            RBlock.dumpRndTree("", true, root, true)
            println("------------------------------")
        }
    }
}