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
 * Created on Nov 19, 2005
 */
package io.github.remmerw.thor.cobra.html.gui

import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.DocumentNotificationListener
import io.github.remmerw.thor.cobra.html.domimpl.ElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLDocumentImpl
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.parser.DocumentBuilderImpl
import io.github.remmerw.thor.cobra.html.parser.InputSourceImpl
import io.github.remmerw.thor.cobra.html.renderer.BoundableRenderable
import io.github.remmerw.thor.cobra.html.renderer.FrameContext
import io.github.remmerw.thor.cobra.html.renderer.NodeRenderer
import io.github.remmerw.thor.cobra.html.renderer.RenderableSpot
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.util.EventDispatch2
import io.github.remmerw.thor.cobra.util.gui.DefferedLayoutSupport
import io.github.remmerw.thor.cobra.util.gui.WrapperLayout
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.w3c.dom.html.HTMLFrameSetElement
import org.xml.sax.SAXException
import java.awt.Color
import java.awt.Cursor
import java.awt.Rectangle
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.IOException
import java.io.StringReader
import java.util.EventListener
import java.util.EventObject
import java.util.concurrent.Future
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.Timer
import kotlin.concurrent.Volatile

/**
 * The `HtmlPanel` class is a Swing component that can render a HTML
 * DOM. It uses either [HtmlBlockPanel] or [FrameSetPanel]
 * internally, depending on whether the document is determined to be a FRAMESET
 * or not.
 *
 *
 * Invoke method [.setDocument] in order to
 * schedule a document for rendering.
 */
class HtmlPanel : JComponent(), FrameContext, DefferedLayoutSupport {
    private val selectionDispatch: EventDispatch2 = SelectionDispatch()
    private val notificationTimer: Timer
    private val notificationListener: DocumentNotificationListener
    private val notificationImmediateAction: Runnable
    private val notifications = ArrayList<DocumentNotification?>(1)

    @Volatile
    protected var htmlBlockPanel: HtmlBlockPanel? = null

    @Volatile
    protected var frameSetPanel: FrameSetPanel? = null

    @Volatile
    private var isFrameSet = false

    @Volatile
    private var nodeRenderer: NodeRenderer? = null

    /**
     * Gets the HTML DOM node currently rendered if any.
     */
    @Volatile
    var rootNode: NodeImpl? = null
        private set

    @Volatile
    private var preferredWidth = -1

    @Volatile
    private var defaultOverflowX = RenderState.OVERFLOW_AUTO

    @Volatile
    private var defaultOverflowY = RenderState.OVERFLOW_AUTO

    /**
     * Constructs an `HtmlPanel`.
     */
    init {
        this.layout = WrapperLayout.instance
        this.isOpaque = false
        this.notificationTimer = Timer(NOTIF_TIMER_DELAY, NotificationTimerAction())
        this.notificationTimer.isRepeats = false
        this.notificationListener = LocalDocumentNotificationListener()
        this.notificationImmediateAction = object : Runnable {
            override fun run() {
                processNotifications()
            }
        }
    }

    /**
     * Sets a preferred width that serves as a hint in calculating the preferred
     * size of the `HtmlPanel`. Note that the preferred size can only
     * be calculated when a document is available, and it will vary during
     * incremental rendering.
     *
     *
     * This method currently does not have any effect when the document is a
     * FRAMESET.
     *
     *
     * Note also that setting the preferred width (to a value other than
     * `-1`) will negatively impact performance.
     *
     * @param width The preferred width, or `-1` to unset.
     */
    fun setPreferredWidth(width: Int) {
        this.preferredWidth = width
        val htmlBlock = this.htmlBlockPanel
        if (htmlBlock != null) {
            htmlBlock.setPreferredWidth(width)
        }
    }

    /**
     * If the current document is not a FRAMESET, this method scrolls the body
     * area to the given location.
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
        val htmlBlock = this.htmlBlockPanel
        if (htmlBlock != null) {
            htmlBlock.scrollTo(bounds, xIfNeeded, yIfNeeded)
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
        val htmlBlock = this.htmlBlockPanel
        if (htmlBlock != null) {
            htmlBlock.scrollTo(node)
        }
    }

    val blockRenderable: BoundableRenderable?
        /**
         * Gets the root `Renderable` of the HTML block. It returns
         * `null` for FRAMESETs.
         */
        get() {
            val htmlBlock = this.htmlBlockPanel
            return if (htmlBlock == null) null else htmlBlock.rootRenderable
        }

    /**
     * Gets an instance of [FrameSetPanel] in case the currently rendered
     * page is a FRAMESET.
     *
     *
     * Note: This method should be invoked in the GUI thread.
     *
     * @return A `FrameSetPanel` instance or `null` if the
     * document currently rendered is not a FRAMESET.
     */
    fun getFrameSetPanel(): FrameSetPanel? {
        val componentCount = this.componentCount
        if (componentCount == 0) {
            return null
        }
        val c: Any? = this.getComponent(0)
        if (c is FrameSetPanel) {
            return c
        }
        return null
    }

    private fun setUpAsBlock(ucontext: UserAgentContext?, rcontext: HtmlRendererContext?) {
        val shp = this.createHtmlBlockPanel(ucontext, rcontext)
        shp.setPreferredWidth(this.preferredWidth)
        shp.setDefaultOverflowX(this.defaultOverflowX)
        shp.setDefaultOverflowY(this.defaultOverflowY)
        this.htmlBlockPanel = shp
        this.frameSetPanel = null
        this.removeAll()
        this.add(shp)
        this.nodeRenderer = shp
    }

    private fun setUpFrameSet(fsrn: NodeImpl?) {
        this.isFrameSet = true
        this.htmlBlockPanel = null
        val fsp = this.createFrameSetPanel()
        this.frameSetPanel = fsp
        this.nodeRenderer = fsp
        this.removeAll()
        this.add(fsp)
        fsp.setRootNode(fsrn)
    }

    /**
     * Method invoked internally to create a [HtmlBlockPanel]. It is made
     * available so it can be overridden.
     */
    protected fun createHtmlBlockPanel(
        ucontext: UserAgentContext?,
        rcontext: HtmlRendererContext?
    ): HtmlBlockPanel {
        return HtmlBlockPanel(Color.WHITE, true, ucontext, rcontext, this)
    }

    /**
     * Method invoked internally to create a [FrameSetPanel]. It is made
     * available so it can be overridden.
     */
    protected fun createFrameSetPanel(): FrameSetPanel {
        return FrameSetPanel()
    }

    /**
     * Scrolls the document such that x and y coordinates are placed in the
     * upper-left corner of the panel.
     *
     *
     * This method may be called outside of the GUI Thread.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    fun scroll(x: Int, y: Int) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.scrollImpl(x, y)
        } else {
            SwingUtilities.invokeLater(Runnable { scrollImpl(x, y) })
        }
    }

    fun scrollBy(x: Int, y: Int) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.scrollByImpl(x, y)
        } else {
            SwingUtilities.invokeLater(Runnable { scrollByImpl(x, y) })
        }
    }

    private fun scrollImpl(x: Int, y: Int) {
        this.scrollTo(Rectangle(x, y, 16, 16), false, false)
    }

    private fun scrollByImpl(xOffset: Int, yOffset: Int) {
        val bp = this.htmlBlockPanel
        if (bp != null) {
            bp.scrollBy(xOffset, yOffset)
        }
    }

    /**
     * Clears the current document if any. If called outside the GUI thread, the
     * operation will be scheduled to be performed in the GUI thread.
     */
    fun clearDocument() {
        if (SwingUtilities.isEventDispatchThread()) {
            this.clearDocumentImpl()
        } else {
            SwingUtilities.invokeLater(Runnable { this@HtmlPanel.clearDocumentImpl() })
        }
    }

    private fun clearDocumentImpl() {
        val prevDocument = this.rootNode as HTMLDocumentImpl?
        if (prevDocument != null) {
            prevDocument.removeDocumentNotificationListener(this.notificationListener)
        }
        val nr = this.nodeRenderer
        if (nr != null) {
            nr.setRootNode(null)
        }
        this.rootNode = null
        this.htmlBlockPanel = null
        this.nodeRenderer = null
        this.isFrameSet = false
        this.removeAll()
        this.revalidate()
        this.repaint()
    }

    /**
     * Sets an HTML DOM node and invalidates the component so it is rendered as
     * soon as possible in the GUI thread.
     *
     *
     * If this method is called from a thread that is not the GUI dispatch thread,
     * the document is scheduled to be set later. Note that
     * [preferred size][.setPreferredWidth] calculations should be done
     * in the GUI dispatch thread for this reason.
     *
     * @param node     This should normally be a Document instance obtained with
     * [DocumentBuilderImpl].
     *
     *
     * @param rcontext A renderer context.
     * @see DocumentBuilderImpl.parse
     * @see org.cobraparser.html.test.SimpleHtmlRendererContext
     */
    fun setDocument(node: Document?, rcontext: HtmlRendererContext) {
        setCursor(Cursor.getDefaultCursor())

        if (SwingUtilities.isEventDispatchThread()) {
            this.setDocumentImpl(node, rcontext)
        } else {
            SwingUtilities.invokeLater(Runnable { this@HtmlPanel.setDocumentImpl(node, rcontext) })
        }
    }

    override fun setCursor(cursor: Cursor?) {
        if (cursor !== getCursor()) {
            super.setCursor(cursor)
        }
    }

    /**
     * Scrolls to the element identified by the given ID in the current document.
     *
     *
     * If this method is invoked outside the GUI thread, the operation is
     * scheduled to be performed as soon as possible in the GUI thread.
     *
     * @param nameOrId The name or ID of the element in the document.
     */
    fun scrollToElement(nameOrId: String?) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.scrollToElementImpl(nameOrId)
        } else {
            SwingUtilities.invokeLater(Runnable { scrollToElementImpl(nameOrId) })
        }
    }

    private fun scrollToElementImpl(nameOrId: String?) {
        val node = this.rootNode
        if (node is HTMLDocumentImpl) {
            val element = node.getElementById(nameOrId)
            if (element != null) {
                this.scrollTo(element)
            }
        }
    }

    private fun setDocumentImpl(node: Document?, rcontext: HtmlRendererContext) {
        // Expected to be called in the GUI thread.
        /*
    if (!(node instanceof HTMLDocumentImpl)) {
      throw new IllegalArgumentException("Only nodes of type HTMLDocumentImpl are currently supported. Use DocumentBuilderImpl.");
    }
    */

        if (this.rootNode is HTMLDocumentImpl) {
            (rootNode as HTMLDocumentImpl).removeDocumentNotificationListener(this.notificationListener)
        }
        if (node is HTMLDocumentImpl) {
            node.addDocumentNotificationListener(this.notificationListener)
        }

        if (node is NodeImpl) {
            this.rootNode = node
            val fsrn = this.getFrameSetRootNode(node)
            val newIfs = fsrn != null
            if ((newIfs != this.isFrameSet) || (this.componentCount == 0)) {
                this.isFrameSet = newIfs
                if (newIfs) {
                    this.setUpFrameSet(fsrn)
                } else {
                    this.setUpAsBlock(rcontext.userAgentContext(), rcontext)
                }
            }
            val nr = this.nodeRenderer
            if (nr != null) {
                // These subcomponents should take care
                // of revalidation.
                if (newIfs) {
                    nr.setRootNode(fsrn)
                } else {
                    nr.setRootNode(node)
                }
            } else {
                this.invalidate()
                this.validate()
                this.repaint()
            }
        }
    }

    /**
     * Renders HTML given as a string.
     *
     * @param htmlSource The HTML source code.
     * @param uri        A base URI used to resolve item URIs.
     * @param rcontext   The [HtmlRendererContext] instance.
     * @see org.cobraparser.html.test.SimpleHtmlRendererContext
     *
     * @see .setDocument
     */
    fun setHtml(htmlSource: String, uri: String?, rcontext: HtmlRendererContext) {
        try {
            val builder = DocumentBuilderImpl(rcontext.userAgentContext(), rcontext)
            StringReader(htmlSource).use { reader ->
                val `is` = InputSourceImpl(reader, uri)
                val document = builder.parse(`is`)
                this.setDocument(document, rcontext)
            }
        } catch (ioe: IOException) {
            throw IllegalStateException("Unexpected condition.", ioe)
        } catch (se: SAXException) {
            throw IllegalStateException("Unexpected condition.", se)
        }
    }

    private fun resetIfFrameSet(): Boolean {
        val nodeImpl = this.rootNode
        val fsrn = this.getFrameSetRootNode(nodeImpl)
        val newIfs = fsrn != null
        if ((newIfs != this.isFrameSet) || (this.componentCount == 0)) {
            this.isFrameSet = newIfs
            if (newIfs) {
                this.setUpFrameSet(fsrn)
                val nr = this.nodeRenderer
                nr!!.setRootNode(fsrn)
                // Set proper bounds and repaint.
                this.validate()
                this.repaint()
                return true
            }
        }
        return false
    }

    private fun getFrameSetRootNode(node: NodeImpl?): NodeImpl? {
        if (node is Document) {
            val element = (node as Document).documentElement as ElementImpl?
            if ((element != null) && "HTML".equals(element.tagName, ignoreCase = true)) {
                return this.getFrameSet(element)
            } else {
                return this.getFrameSet(node)
            }
        } else {
            return null
        }
    }

    private fun getFrameSet(node: NodeImpl): NodeImpl? {
        val children = node.childrenArray
        if (children == null) {
            return null
        }
        val length = children.size
        var frameSet: NodeImpl? = null
        for (i in 0..<length) {
            val child = children[i]
            if (child is Text) {
                // Ignore
            } else if (child is ElementImpl) {
                val tagName = child.nodeName
                if ("HEAD".equals(tagName, ignoreCase = true) || "NOFRAMES".equals(
                        tagName,
                        ignoreCase = true
                    ) || "TITLE".equals(tagName, ignoreCase = true)
                    || "META".equals(tagName, ignoreCase = true) || "SCRIPT".equals(
                        tagName,
                        ignoreCase = true
                    ) || "NOSCRIPT".equals(tagName, ignoreCase = true)
                ) {
                    // ignore it
                } else if ("FRAMESET".equals(tagName, ignoreCase = true)) {
                    frameSet = child
                    break
                } else {
                    if (this.hasSomeHtml(child)) {
                        return null
                    }
                }
            }
        }
        return frameSet
    }

    private fun hasSomeHtml(element: ElementImpl): Boolean {
        val tagName = element.tagName
        if ("HEAD".equals(tagName, ignoreCase = true) || "TITLE".equals(
                tagName,
                ignoreCase = true
            ) || "META".equals(tagName, ignoreCase = true)
        ) {
            return false
        }
        val children = element.childrenArray
        if (children != null) {
            val length = children.size
            for (i in 0..<length) {
                val child = children[i]
                if (child is Text) {
                    val textContent = child.getTextContent()
                    if ((textContent != null) && "" != textContent.trim { it <= ' ' }) {
                        return false
                    }
                } else if (child is ElementImpl) {
                    if (this.hasSomeHtml(child)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    /**
     * Internal method used to expand the selection to the given point.
     *
     *
     * Note: This method should be invoked in the GUI thread.
     */
    override fun expandSelection(rpoint: RenderableSpot?) {
        val block = this.htmlBlockPanel
        if (block != null) {
            block.setSelectionEnd(rpoint)
            block.repaint()
            this.selectionDispatch.fireEvent(
                SelectionChangeEvent(
                    this,
                    block.isSelectionAvailable
                )
            )
        }
    }

    /**
     * Internal method used to reset the selection so that it is empty at the
     * given point. This is what is called when the user clicks on a point in the
     * document.
     *
     *
     * Note: This method should be invoked in the GUI thread.
     */
    override fun resetSelection(rpoint: RenderableSpot?) {
        val block = this.htmlBlockPanel
        if (block != null) {
            block.setSelectionStart(rpoint)
            block.setSelectionEnd(rpoint)
            block.repaint()
        }
        this.selectionDispatch.fireEvent(SelectionChangeEvent(this, false))
    }

    val selectionText: String?
        /**
         * Gets the selection text.
         *
         *
         * Note: This method should be invoked in the GUI thread.
         */
        get() {
            val block = this.htmlBlockPanel
            if (block == null) {
                return null
            } else {
                return block.selectionText
            }
        }

    val selectionNode: Node?
        /**
         * Gets a DOM node enclosing the selection. The node returned should be the
         * inner-most node that encloses both selection start and end points. Note
         * that the selection end point may be just outside of the selection.
         *
         *
         * Note: This method should be invoked in the GUI thread.
         *
         * @return A node enclosing the current selection, or `null` if
         * there is no such node. It also returns `null` for
         * FRAMESETs.
         */
        get() {
            val block = this.htmlBlockPanel
            if (block == null) {
                return null
            } else {
                return block.selectionNode
            }
        }

    /**
     * Returns true only if the current block has a selection. This method has no
     * effect in FRAMESETs at the moment.
     */
    fun hasSelection(): Boolean {
        val block = this.htmlBlockPanel
        if (block == null) {
            return false
        } else {
            return block.hasSelection()
        }
    }

    /**
     * Copies the current selection, if any, into the clipboard. This method has
     * no effect in FRAMESETs at the moment.
     */
    fun copy(): Boolean {
        val block = this.htmlBlockPanel
        if (block != null) {
            return block.copy()
        } else {
            return false
        }
    }

    /**
     * Adds listener of selection changes. Note that it does not have any effect
     * on FRAMESETs.
     *
     * @param listener An instance of [SelectionChangeListener].
     */
    fun addSelectionChangeListener(listener: SelectionChangeListener?) {
        this.selectionDispatch.addListener(listener)
    }

    /**
     * Removes a listener of selection changes that was previously added.
     */
    fun removeSelectionChangeListener(listener: SelectionChangeListener?) {
        this.selectionDispatch.removeListener(listener)
    }

    /**
     * Sets the default horizontal overflow.
     *
     *
     * This method has no effect on FRAMESETs.
     *
     * @param overflow See [RenderState].
     */
    fun setDefaultOverflowX(overflow: Int) {
        this.defaultOverflowX = overflow
        val block = this.htmlBlockPanel
        if (block != null) {
            block.setDefaultOverflowX(overflow)
        }
    }

    /**
     * Sets the default vertical overflow.
     *
     *
     * This method has no effect on FRAMESETs.
     *
     * @param overflow See [RenderState].
     */
    fun setDefaultOverflowY(overflow: Int) {
        this.defaultOverflowY = overflow
        val block = this.htmlBlockPanel
        if (block != null) {
            block.setDefaultOverflowY(overflow)
        }
    }

    private fun addNotification(notification: DocumentNotification?) {
        // This can be called in a random thread.
        val notifs = this.notifications
        synchronized(notifs) {
            notifs.add(notification)
        }
        if (SwingUtilities.isEventDispatchThread()) {
            // In this case we want the notification to be processed
            // immediately. However, we don't want potential recursions
            // to occur when a Javascript property is set in the GUI thread.
            // Additionally, many property values may be set in one
            // event block.
            SwingUtilities.invokeLater(this.notificationImmediateAction)
        } else {
            this.notificationTimer.restart()
        }
    }

    /**
     * Invalidates the layout of the given node and schedules it to be layed out
     * later. Multiple invalidations may be processed in a single document layout.
     */
    override fun delayedRelayout(node: NodeImpl?) {
        val notifs = this.notifications
        synchronized(notifs) {
            notifs.add(DocumentNotification(DocumentNotification.Companion.SIZE, node))
        }
        this.notificationTimer.restart()
    }

    private fun processNotifications() {
        // This is called in the GUI thread.
        val notifs = this.notifications
        val notifsArray: MutableList<DocumentNotification> = mutableListOf()
        synchronized(notifs) {
            val size = notifs.size
            if (size == 0) {
                return
            }
            notifs.forEach { i ->
                if (i != null) {
                    notifsArray.add(i)
                }
            }
            notifs.clear()
        }
        val length = notifsArray.size
        for (i in 0..<length) {
            val dn = notifsArray[i]
            if ((dn.node is HTMLFrameSetElement) && (this.htmlBlockPanel != null)) {
                if (this.resetIfFrameSet()) {
                    // Revalidation already taken care of.
                    return
                }
            }
        }
        val blockPanel = this.htmlBlockPanel
        if (blockPanel != null) {
            blockPanel.processDocumentNotifications(notifsArray)
        }
        val frameSetPanel = this.frameSetPanel
        if (frameSetPanel != null) {
            frameSetPanel.processDocumentNotifications(notifsArray)
        }
    }

    override fun layoutCompletion(): Future<Boolean?>? {
        return htmlBlockPanel!!.layoutCompletion()
    }

    val isReadyToPaint: Boolean
        get() {
            val htmlBlock = this.htmlBlockPanel
            if (htmlBlock != null) {
                return (notifications.size == 0) && htmlBlock.isReadyToPaint
            }
            return false
        }

    fun disableRenderHints() {
        this.htmlBlockPanel!!.disableRenderHints()
    }

    private inner class SelectionDispatch : EventDispatch2() {
        /*
         * (non-Javadoc)
         *
         * @see
         * org.xamjwg.util.EventDispatch2#dispatchEvent(java.util.EventListener,
         * java.util.EventObject)
         */
        override fun dispatchEvent(listener: EventListener, event: EventObject?) {
            (listener as SelectionChangeListener).selectionChanged(event as SelectionChangeEvent?)
        }
    }

    private inner class LocalDocumentNotificationListener : DocumentNotificationListener {
        override fun allInvalidated() {
            this@HtmlPanel.addNotification(
                DocumentNotification(
                    DocumentNotification.Companion.GENERIC,
                    null
                )
            )
        }

        override fun invalidated(node: NodeImpl) {
            this@HtmlPanel.addNotification(
                DocumentNotification(
                    DocumentNotification.Companion.GENERIC,
                    node
                )
            )
        }

        override fun lookInvalidated(node: NodeImpl) {
            this@HtmlPanel.addNotification(
                DocumentNotification(
                    DocumentNotification.Companion.LOOK,
                    node
                )
            )
        }

        override fun positionInvalidated(node: NodeImpl) {
            this@HtmlPanel.addNotification(
                DocumentNotification(
                    DocumentNotification.Companion.POSITION,
                    node
                )
            )
        }

        override fun sizeInvalidated(node: NodeImpl) {
            this@HtmlPanel.addNotification(
                DocumentNotification(
                    DocumentNotification.Companion.SIZE,
                    node
                )
            )
        }

        override fun externalScriptLoading(node: NodeImpl) {
            // Ignorable here.
        }

        override fun nodeLoaded(node: NodeImpl) {
            this@HtmlPanel.addNotification(
                DocumentNotification(
                    DocumentNotification.Companion.GENERIC,
                    node
                )
            )
        }

        override fun structureInvalidated(node: NodeImpl) {
            this@HtmlPanel.addNotification(
                DocumentNotification(
                    DocumentNotification.Companion.GENERIC,
                    node
                )
            )
        }
    }

    private inner class NotificationTimerAction : ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            this@HtmlPanel.processNotifications()
        }
    }

    companion object {

        private const val NOTIF_TIMER_DELAY = 150
    }
}
