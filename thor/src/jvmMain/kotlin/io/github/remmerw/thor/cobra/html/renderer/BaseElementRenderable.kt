/*  GNU LESSER GENERAL PUBLIC LICENSE
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

import io.github.remmerw.thor.cobra.html.domimpl.HTMLDocumentImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.style.BackgroundInfo
import io.github.remmerw.thor.cobra.html.style.BorderInfo
import io.github.remmerw.thor.cobra.html.style.HtmlInsets
import io.github.remmerw.thor.cobra.html.style.HtmlValues
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.ImageResponse
import io.github.remmerw.thor.cobra.ua.NetworkRequest
import io.github.remmerw.thor.cobra.ua.NetworkRequestEvent
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.ua.UserAgentContext.RequestKind
import io.github.remmerw.thor.cobra.util.SecurityUtil
import io.github.remmerw.thor.cobra.util.Strings
import io.github.remmerw.thor.cobra.util.gui.GUITasks
import org.w3c.dom.css.CSS2Properties
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle
import java.awt.image.ImageObserver
import java.io.IOException
import java.net.URL
import java.security.PrivilegedAction
import java.util.LinkedList
import java.util.function.Function
import java.util.logging.Level
import javax.swing.SwingUtilities
import kotlin.concurrent.Volatile

abstract class BaseElementRenderable(
    container: RenderableContainer?,
    modelNode: ModelNode?,
    protected val userAgentContext: UserAgentContext?
) : BaseRCollection(container, modelNode), RElement, RenderableContainer, ImageObserver {
    protected val renderStyleCanBeInvalidated = true
    val borderOverrider: BorderOverrider = BorderOverrider()

    /**
     * A list of absolute positioned or float parent-child pairs.
     */
    override var delayedPairs: MutableCollection<DelayedPair?>? = null

    /**
     * Background color which may be different to that from RenderState in the
     * case of a Document node.
     */
    protected var backgroundColor: Color? = null

    @Volatile
    protected var backgroundImage: Image? = null

    @Volatile
    protected var backgroundImageError: Boolean = false
    override var zIndex: Int = 0
    protected var borderTopColor: Color? = null
    protected var borderLeftColor: Color? = null
    protected var borderBottomColor: Color? = null
    protected var borderRightColor: Color? = null
    protected var borderInsets: Insets? = null
    protected var marginInsets: Insets? = null
    protected var paddingInsets: Insets? = null
    protected var borderInfo: BorderInfo? = null
    protected var lastBackgroundImageUri: URL? = null
    protected var overflowX: Int = 0
    protected var overflowY: Int = 0
    protected var layoutDeepCanBeInvalidated: Boolean = false

    // Used for relative positioning
    protected var relativeOffsetX: Int = 0
    protected var relativeOffsetY: Int = 0

    /**
     * A collection of all GUI components added by descendents.
     */
    private var guiComponents: MutableCollection<Component?>? = null
    private var declaredWidth: Int? = INVALID_SIZE
    private var declaredHeight: Int? = INVALID_SIZE
    private var lastAvailWidthForDeclared = -1
    private var lastAvailHeightForDeclared = -1

    var layoutUpTreeCanBeInvalidated = false

    /**
     * Invalidates this Renderable and all descendents. This is only used in
     * special cases, such as when a new style sheet is added.
     */
    override fun invalidateLayoutDeep() {
        if (this.layoutDeepCanBeInvalidated) {
            this.layoutDeepCanBeInvalidated = false
            this.invalidateLayoutLocal()
            val i = this.getRenderables(false)
            if (i != null) {
                while (i.hasNext()) {
                    val rn = i.next()
                    val r = if (rn is PositionedRenderable) rn.renderable else rn
                    if (r is RCollection) {
                        r.invalidateLayoutDeep()
                    }
                }
            }
        }
    }

    public override fun invalidateLayoutLocal() {
        val rs = this.modelNode?.renderState()
        if (rs != null) {
            rs.invalidate()
        }
        this.delayedPairs = null
        this.overflowX = RenderState.OVERFLOW_NONE
        this.overflowY = RenderState.OVERFLOW_NONE
        this.declaredWidth = INVALID_SIZE
        this.declaredHeight = INVALID_SIZE
        this.lastAvailHeightForDeclared = -1
        this.lastAvailWidthForDeclared = -1
    }

    protected open fun getDeclaredWidth(renderState: RenderState?, actualAvailWidth: Int): Int? {
        var dw = this.declaredWidth
        if ((dw === INVALID_SIZE) || (actualAvailWidth != this.lastAvailWidthForDeclared)) {
            this.lastAvailWidthForDeclared = actualAvailWidth
            val dwInt = this.getDeclaredWidthImpl(renderState, actualAvailWidth)
            dw = if (dwInt == -1) null else dwInt
            this.declaredWidth = dw
        }
        return dw
    }

    protected fun getDeclaredHelper(
        renderState: RenderState?, baseValue: Int,
        propertyGetter: Function<CSS2Properties?, String?>, ignorePercentage: Boolean
    ): Int? {
        val rootNode: Any? = this.modelNode
        if (rootNode is HTMLElementImpl) {
            val props: CSS2Properties = rootNode.getCurrentStyle()
            val valueText = propertyGetter.apply(props)
            if ((valueText == null) || "" == valueText || "none" == valueText || (ignorePercentage && valueText.endsWith(
                    "%"
                ))
            ) {
                return null
            }
            return HtmlValues.getPixelSize(valueText, renderState, -1, baseValue)
        } else {
            return null
        }
    }

    private val isParentHeightDeclared: Boolean
        get() {
            val parentNode =
                getModelNode()?.parentModelNode()
            if (parentNode is HTMLElementImpl) {
                val props: CSS2Properties = parentNode.getCurrentStyle()
                val decHeight = props.height
                return !(Strings.isBlank(decHeight) || "auto" == decHeight)
            }
            return false
        }

    private val isParentWidthDeclared: Boolean
        get() {
            val parentNode =
                getModelNode()?.parentModelNode()
            if (parentNode is HTMLElementImpl) {
                val props: CSS2Properties = parentNode.getCurrentStyle()
                val decWidth = props.width
                return !(Strings.isBlank(decWidth) || "auto" == decWidth)
            }
            return false
        }

    protected fun getDeclaredMaxWidth(renderState: RenderState?, actualAvailWidth: Int): Int? {
        return getDeclaredHelper(
            renderState,
            actualAvailWidth,
            Function { props: CSS2Properties? -> props!!.maxWidth },
            !this.isParentWidthDeclared
        )
    }

    protected fun getDeclaredMinWidth(renderState: RenderState?, actualAvailWidth: Int): Int? {
        return getDeclaredHelper(
            renderState,
            actualAvailWidth,
            Function { props: CSS2Properties? -> props!!.minWidth },
            !this.isParentWidthDeclared
        )
    }

    protected fun getDeclaredMaxHeight(renderState: RenderState?, actualAvailHeight: Int): Int? {
        return getDeclaredHelper(
            renderState,
            actualAvailHeight,
            Function { props: CSS2Properties? -> props!!.maxHeight },
            !this.isParentHeightDeclared
        )
    }

    protected fun getDeclaredMinHeight(renderState: RenderState?, actualAvailHeight: Int): Int? {
        return getDeclaredHelper(
            renderState,
            actualAvailHeight,
            Function { props: CSS2Properties? -> props!!.minHeight },
            !this.isParentHeightDeclared
        )
    }

    fun hasDeclaredWidth(): Boolean {
        val dw = this.declaredWidth
        if (dw === INVALID_SIZE) {
            val rootNode: Any? = this.modelNode
            if (rootNode is HTMLElementImpl) {
                val props: CSS2Properties = rootNode.getCurrentStyle()
                return !Strings.isBlank(props.width) || !Strings.isBlank(props.maxWidth)
            }
            return false
        }
        return dw != null
    }

    private fun getDeclaredWidthImpl(renderState: RenderState?, availWidth: Int): Int {
        val rootNode: Any? = this.modelNode
        if (rootNode is HTMLElementImpl) {
            val props: CSS2Properties = rootNode.getCurrentStyle()
            val widthText = props.width
            if ((widthText == null) || "" == widthText) {
                return -1
            }
            return HtmlValues.getPixelSize(widthText, renderState, -1, availWidth)
        } else {
            return -1
        }
    }

    protected open fun getDeclaredHeight(renderState: RenderState?, actualAvailHeight: Int): Int? {
        var dh = this.declaredHeight
        if ((dh === INVALID_SIZE) || (actualAvailHeight != this.lastAvailHeightForDeclared)) {
            this.lastAvailHeightForDeclared = actualAvailHeight
            val dhInt = this.getDeclaredHeightImpl(renderState, actualAvailHeight)
            dh = if (dhInt == -1) null else dhInt
            this.declaredHeight = dh
        }
        return dh
    }

    protected fun getDeclaredHeightImpl(renderState: RenderState?, availHeight: Int): Int {
        val rootNode: Any? = this.modelNode
        if (rootNode is HTMLElementImpl) {
            val props: CSS2Properties = rootNode.getCurrentStyle()
            val heightText = props.height
            if ((heightText == null) || "" == heightText) {
                return -1
            }
            return HtmlValues.getPixelSize(heightText, renderState, -1, availHeight)
        } else {
            return -1
        }
    }

    /**
     * All overriders should call super implementation.
     */
    override fun paint(gIn: Graphics) {
        val isRelative = (relativeOffsetX or relativeOffsetY) != 0
        val g = if (isRelative) gIn.create() else gIn
        if (isRelative) {
            g.translate(relativeOffsetX, relativeOffsetY)
        }

        try {
            paintShifted(g)
        } finally {
            if (isRelative) {
                g.dispose()
            }
        }
    }

    protected abstract fun paintShifted(g: Graphics)

    /**
     * Lays out children, and deals with "valid" state. Override doLayout method
     * instead of this one.
     */
    override fun layout(availWidth: Int, availHeight: Int, sizeOnly: Boolean) {
        // Must call doLayout regardless of validity state.
        try {
            this.doLayout(availWidth, availHeight, sizeOnly)
        } finally {
            this.layoutUpTreeCanBeInvalidated = true
            this.layoutDeepCanBeInvalidated = true
        }
    }

    protected abstract fun doLayout(availWidth: Int, availHeight: Int, sizeOnly: Boolean)

    protected fun sendGUIComponentsToParent() {
        // Ensures that parent has all the components
        // below this renderer node. (Parent expected to have removed them).
        val gc = this.guiComponents
        if (gc != null) {
            val rc = this.container!!
            for (c in gc) {
                rc.addComponent(c)
            }
        }
    }

    protected fun clearGUIComponents() {
        val gc = this.guiComponents
        gc?.clear()
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.RenderableContainer#add(java.awt.Component)
     */
    override fun addComponent(component: Component?): Component? {
        // TODO: This gets called too many times!

        // Expected to be called in GUI thread.
        // Adds only in local collection.
        // Does not remove from parent.
        // Sending components to parent is done by sendGUIComponentsToParent().

        var gc = this.guiComponents
        if (gc == null) {
            gc = HashSet<Component?>(1)
            this.guiComponents = gc
        }
        gc.add(component)
        return component
    }

    override fun updateAllWidgetBounds() {
        this.container?.updateAllWidgetBounds()
    }

    /**
     * Updates widget bounds below this node only. Should not be called during
     * general rendering.
     */
    fun updateWidgetBounds() {
        val guiPoint = this.getGUIPoint(0, 0)!!
        this.updateWidgetBounds(guiPoint.x, guiPoint.y)
    }

    override fun boundsRelativeToBlock(): Rectangle? {
            var parent: RCollection? = this
            var x = 0
            var y = 0
            while (parent != null) {
                x += parent.x()
                y += parent.y()
                parent = parent.parent
                if (parent is RElement) {
                    break
                }
            }
            return Rectangle(x, y, this.getWidth(), this.getHeight())
        }

    protected open fun clearStyle(isRootBlock: Boolean) {
        this.borderInfo = null
        this.borderInsets = null
        this.borderTopColor = null
        this.borderLeftColor = null
        this.borderBottomColor = null
        this.borderRightColor = null
        this.zIndex = 0
        this.backgroundColor = null
        this.backgroundImage = null
        this.backgroundImageError = false
        this.lastBackgroundImageUri = null
        this.overflowX = RenderState.OVERFLOW_VISIBLE
        this.overflowY = RenderState.OVERFLOW_VISIBLE

        this.marginInsets = null
        this.paddingInsets = null

        this.relativeOffsetX = 0
        this.relativeOffsetY = 0
    }

    open fun applyLook() {
        applyStyle(0, 0, false)
    }

    protected fun applyStyle(availWidth: Int, availHeight: Int) {
        applyStyle(availWidth, availHeight, true)
    }

    open fun applyStyle(availWidth: Int, availHeight: Int, updateLayout: Boolean) {
        // TODO: Can be optimized if there's no style?
        // TODO: There's part of this that doesn't depend on availWidth
        // and availHeight, so it doesn't need to be redone on
        // every resize.
        // Note: Overridden by tables and lists.
        val rootNode = this.modelNode
        val rootElement: HTMLElementImpl?
        val isRootBlock: Boolean
        if (rootNode is HTMLDocumentImpl) {
            isRootBlock = true
            // Need to get HTML tag, for bgcolor, etc.
            // TODO: Use a faster / direct way to get the html element
            rootElement = rootNode.getElementsByTagName("html").item(0) as HTMLElementImpl?
        } else {
            isRootBlock = false
            if (rootNode is HTMLElementImpl) {
                rootElement = rootNode
            } else {
                rootElement = null
            }
        }
        if (rootElement == null) {
            this.clearStyle(isRootBlock)
            return
        }
        val rs = rootElement.getRenderState()

        var binfo = rs.backgroundInfo
        if (isRootBlock && (binfo == null || (binfo.backgroundColor == null && binfo.backgroundImage == null))) {
            val bodyNode = rootElement.getElementsByTagName("body").item(0)
            if (bodyNode != null && bodyNode is HTMLElementImpl) {
                binfo = bodyNode.getRenderState().backgroundInfo
            }
        }

        this.backgroundColor = if (binfo == null) null else binfo.backgroundColor
        val backgroundImageUri = if (binfo == null) null else binfo.backgroundImage
        if (backgroundImageUri == null) {
            this.backgroundImage = null
            this.backgroundImageError = false
            this.lastBackgroundImageUri = null
        } else if (backgroundImageUri != this.lastBackgroundImageUri) {
            this.lastBackgroundImageUri = backgroundImageUri
            this.loadBackgroundImage(backgroundImageUri)
        }
        if (!isRootBlock) {
            val props = rootElement.getCurrentStyle()
            val borderInfo = rs.borderInfo
            if (borderInfo != null) {
                this.borderTopColor = borderInfo.topColor
                this.borderLeftColor = borderInfo.leftColor
                this.borderBottomColor = borderInfo.bottomColor
                this.borderRightColor = borderInfo.rightColor
            } else {
                this.borderTopColor = null
                this.borderLeftColor = null
                this.borderBottomColor = null
                this.borderRightColor = null
            }
            if (updateLayout) {
                this.borderInfo = borderInfo
                val binsets =
                    if (borderInfo == null) null else borderOverrider.get(borderInfo.insets)
                val minsets = rs.marginInsets
                val pinsets = rs.paddingInsets
                // TODO: These zero values are not modified anywhere; can be inlined
                val dmleft = 0
                val dmright = 0
                val dmtop = 0
                val dmbottom = 0
                val dpleft = 0
                val dpright = 0
                val dptop = 0
                val dpbottom = 0
                var borderInsets = if (binsets == null) null else binsets.getAWTInsets(
                    0,
                    0,
                    0,
                    0,
                    availWidth,
                    availHeight,
                    0,
                    0
                )
                if (borderInsets == null) {
                    borderInsets = RBlockViewport.Companion.ZERO_INSETS
                }
                var paddingInsets = if (pinsets == null) null else pinsets.getAWTInsets(
                    dptop, dpleft, dpbottom, dpright, availWidth,
                    availHeight, 0, 0
                )
                if (paddingInsets == null) {
                    paddingInsets = RBlockViewport.Companion.ZERO_INSETS
                }
                var tentativeMarginInsets = if (minsets == null) null else minsets.getAWTInsets(
                    dmtop, dmleft, dmbottom, dmright,
                    availWidth, availHeight, 0, 0
                )
                if (tentativeMarginInsets == null) {
                    tentativeMarginInsets = RBlockViewport.Companion.ZERO_INSETS
                }
                this.borderInsets = borderInsets
                if (isRootBlock) {
                    // In the root block, the margin behaves like an extra padding.
                    var regularMarginInsets = tentativeMarginInsets
                    if (regularMarginInsets == null) {
                        regularMarginInsets = RBlockViewport.Companion.ZERO_INSETS
                    }
                    this.marginInsets = null
                    this.paddingInsets = Insets(
                        paddingInsets!!.top + regularMarginInsets!!.top,
                        paddingInsets.left + regularMarginInsets.left,
                        paddingInsets.bottom + regularMarginInsets.bottom,
                        paddingInsets.right + regularMarginInsets.right
                    )
                } else {
                    this.paddingInsets = paddingInsets
                    this.marginInsets = tentativeMarginInsets
                }
                // TODO: Why is props from root element being used here and not the renderstate of the current element?
                val zIndex = props.getZIndex()
                if (zIndex != null) {
                    try {
                        this.zIndex = zIndex.toInt()
                    } catch (err: NumberFormatException) {
                        logger.log(
                            Level.WARNING,
                            "Unable to parse z-index [" + zIndex + "] in element " + this.modelNode + ".",
                            err
                        )
                        this.zIndex = 0
                    }
                } else {
                    // TODO: when zIndex is not specified or auto, that information should be retained, for GH-193
                    this.zIndex = 0
                }
                this.overflowX = rs.overflowX
                this.overflowY = rs.overflowY
            }
        }

        // Check if background image needs to be loaded
    }

    // TODO: Move to RBlock and make private
    protected fun applyAutoStyles(availWidth: Int, availHeight: Int): Dimension? {
        val rootNode: Any? = this.modelNode
        val rootElement: HTMLElementImpl?
        if (rootNode is HTMLDocumentImpl) {
            // Need to get BODY tag, for bgcolor, etc.
            rootElement = rootNode.body as HTMLElementImpl?
        } else {
            if (rootNode is HTMLElementImpl) {
                rootElement = rootNode
            } else {
                rootElement = null
            }
        }
        if (rootElement == null) {
            return null
        }
        val rs = rootElement.getRenderState()
        val changes = Dimension()
        val minsets = rs.marginInsets
        if (minsets != null) {
            if (availWidth > 1) {
                // TODO: Consider the case when only one is auto
                val autoMarginX = availWidth / 2
                if (minsets.leftType == HtmlInsets.TYPE_AUTO) {
                    this.marginInsets!!.left = autoMarginX
                    changes.width += autoMarginX
                }
                if (minsets.rightType == HtmlInsets.TYPE_AUTO) {
                    this.marginInsets!!.right = autoMarginX
                    changes.width += autoMarginX
                }
            }
            /* auto for margin-top and margin-bottom is supposed to compute to zero, except when parent is a flex box */
            /*
      if (availHeight > 1) {
        // TODO: Consider the case when only one is auto
        final int autoMarginY = availHeight / 2;
        if (minsets.topType == HtmlInsets.TYPE_AUTO) {
          this.marginInsets.top = autoMarginY;
          changes.height += autoMarginY;
        }
        if (minsets.bottomType == HtmlInsets.TYPE_AUTO) {
          this.marginInsets.bottom = autoMarginY;
          changes.height += autoMarginY;
        }
      }*/
        }
        return changes
    }

    protected fun loadBackgroundImage(imageURL: URL) {
        val ctx = this.userAgentContext
        if (ctx != null) {
            val request = ctx.createHttpRequest()
            request?.addNetworkRequestListener { event: NetworkRequestEvent? ->
                val readyState = request.readyState
                if (readyState == NetworkRequest.STATE_COMPLETE) {
                    val status = request.status
                    if ((status == 200) || (status == 0)) {
                        val imgResp = request.responseImage
                        if (imgResp.state == ImageResponse.State.loaded) {
                            checkNotNull(imgResp.img)
                            val img: Image = imgResp.img
                            this@BaseElementRenderable.backgroundImage = img
                            backgroundImageError = false
                            // Cause observer to be called
                            val w = img.getWidth(this@BaseElementRenderable)
                            val h = img.getHeight(this@BaseElementRenderable)
                            // Maybe image already done...
                            if ((w != -1) && (h != -1)) {
                                SwingUtilities.invokeLater(Runnable {
                                    this@BaseElementRenderable.repaint()
                                })
                            }
                        } else {
                            backgroundImageError = true
                        }
                    } else {
                        backgroundImageError = true
                    }
                } else if (readyState == NetworkRequest.STATE_ABORTED) {
                    backgroundImageError = true
                }
            }

            SecurityUtil.doPrivileged<Any?>(PrivilegedAction {
                // Code might have restrictions on accessing items from elsewhere.
                try {
                    request?.open("GET", imageURL)
                    request?.send(null, UserAgentContext.Request(imageURL, RequestKind.Image))
                } catch (thrown: IOException) {
                    println("Caught exception")
                    // logger.log(Level.WARNING, "loadBackgroundImage()", thrown);
                    backgroundImageError = true
                }
                null
            })
        }
    }

    open fun getZIndex(): Int {
        return this.zIndex
    }

    private fun getBorderTopColor(): Color {
        val c = this.borderTopColor
        return if (c == null) Color.black else c
    }

    private fun getBorderLeftColor(): Color {
        val c = this.borderLeftColor
        return if (c == null) Color.black else c
    }

    private fun getBorderBottomColor(): Color {
        val c = this.borderBottomColor
        return if (c == null) Color.black else c
    }

    private fun getBorderRightColor(): Color {
        val c = this.borderRightColor
        return if (c == null) Color.black else c
    }

    protected fun prePaint(g: Graphics) {
        val startWidth = this.width
        val startHeight = this.height
        var totalWidth = startWidth
        var totalHeight = startHeight
        var startX = 0
        var startY = 0
        val node = this.modelNode!!
        val rs = node.renderState()
        val marginInsets = this.marginInsets
        if (marginInsets != null) {
            totalWidth -= (marginInsets.left + marginInsets.right)
            totalHeight -= (marginInsets.top + marginInsets.bottom)
            startX += marginInsets.left
            startY += marginInsets.top
        }

        val borderInsets = this.borderInsets

        prePaintBackground(g, totalWidth, totalHeight, startX, startY, node, rs, borderInsets)

        prePaintBorder(g, totalWidth, totalHeight, startX, startY, borderInsets)
    }

    fun prePaintBackground(
        g: Graphics, totalWidth: Int, totalHeight: Int, startX: Int, startY: Int, node: ModelNode?,
        rs: RenderState?, borderInsets: Insets?
    ) {
        // TODO: Check if we can use TexturePaint to draw repeated background images
        // See example: http://www.informit.com/articles/article.aspx?p=26349&seqNum=4

        // Using clientG (clipped below) beyond this point.

        val clientG = g.create(startX, startY, totalWidth, totalHeight)
        try {
            var bkgBounds: Rectangle? = null
            if (node != null) {
                val btop = if (borderInsets == null) 0 else borderInsets.top
                val bleft = if (borderInsets == null) 0 else borderInsets.left

                val bkg = this.backgroundColor
                if ((bkg != null) && (bkg.alpha > 0)) {
                    clientG.color = bkg
                    clientG.fillRect(0, 0, totalWidth, totalHeight)
                }
                val binfo = if (rs == null) null else rs.backgroundInfo
                val image = this.backgroundImage
                if (image != null) {
                    bkgBounds = clientG.clipBounds

                    val w = image.getWidth(this)
                    val h = image.getHeight(this)
                    if ((w != -1) && (h != -1)) {
                        val imageY: Int = getImageY(totalHeight, binfo, h)
                        val imageX: Int = getImageX(totalWidth, binfo, w)

                        // TODO: optimise this. Although it works fine, it makes an extra `draw` call when imageX or imageY are negative
                        val baseX = (bleft % w) + ((bkgBounds.x / w) * w) - (w - (imageX % w))
                        val baseY = (btop % h) + ((bkgBounds.y / h) * h) - (h - (imageY % h))

                        when (if (binfo == null) BackgroundInfo.BR_REPEAT else binfo.backgroundRepeat) {
                            BackgroundInfo.BR_NO_REPEAT -> {
                                clientG.drawImage(image, bleft + imageX, btop + imageY, w, h, this)
                            }

                            BackgroundInfo.BR_REPEAT_X -> {
                                // Modulate starting x.
                                val topX = bkgBounds.x + bkgBounds.width
                                run {
                                    var x = baseX
                                    while (x < topX) {
                                        clientG.drawImage(image, x, btop + imageY, w, h, this)
                                        x += w
                                    }
                                }
                            }

                            BackgroundInfo.BR_REPEAT_Y -> {
                                // Modulate starting y.
                                val topY = bkgBounds.y + bkgBounds.height
                                run {
                                    var y = baseY
                                    while (y < topY) {
                                        clientG.drawImage(image, bleft + imageX, y, w, h, this)
                                        y += h
                                    }
                                }
                            }

                            else -> {
                                // Modulate starting x and y.
                                val topX = bkgBounds.x + bkgBounds.width
                                val topY = bkgBounds.y + bkgBounds.height
                                // Replacing this:
                                run {
                                    var x = baseX
                                    while (x < topX) {
                                        run {
                                            var y = baseY
                                            while (y < topY) {
                                                clientG.drawImage(image, x, y, w, h, this)
                                                y += h
                                            }
                                        }
                                        x += w
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            clientG.dispose()
        }
    }

    fun prePaintBorder(
        g: Graphics,
        totalWidth: Int,
        totalHeight: Int,
        startX: Int,
        startY: Int,
        borderInsets: Insets?
    ) {
        if (borderInsets != null) {
            val btop = borderInsets.top
            val bleft = borderInsets.left
            val bright = borderInsets.right
            val bbottom = borderInsets.bottom
            if ((btop or bleft or bright or bbottom) != 0) {
                val newTotalWidth = totalWidth - (bleft + bright)
                val newTotalHeight = totalHeight - (btop + bbottom)
                val newStartX = startX + bleft
                val newStartY = startY + btop
                val clientRegion = Rectangle(newStartX, newStartY, newTotalWidth, newTotalHeight)

                // Paint borders if the clip bounds are not contained
                // by the content area.
                val clipBounds = g.clipBounds
                if (!clientRegion.contains(clipBounds)) {
                    val borderInfo = this.borderInfo
                    val bPainter = BorderPainter(
                        g,
                        totalWidth,
                        totalHeight,
                        startX,
                        startY,
                        btop,
                        bbottom,
                        bleft,
                        bright
                    )

                    if (btop > 0) {
                        val borderStyle =
                            if (borderInfo == null) HtmlValues.BORDER_STYLE_SOLID else borderInfo.topStyle
                        val normalColor = this.getBorderTopColor()
                        val width = btop
                        val widthBy2 = width / 2
                        val vertMid = startY + widthBy2
                        val yComputer: Function<Int?, Int?> = Function { i: Int? -> startY + i!! }
                        bPainter.drawHorizBorder(
                            borderStyle,
                            normalColor,
                            width,
                            widthBy2,
                            vertMid,
                            yComputer,
                            true
                        )
                    }
                    if (bright > 0) {
                        val borderStyle =
                            if (borderInfo == null) HtmlValues.BORDER_STYLE_SOLID else borderInfo.rightStyle
                        val normalColor = this.getBorderRightColor()
                        val lastX = (startX + totalWidth) - 1
                        val width = bright
                        val widthBy2 = width / 2
                        val horizMid = lastX - widthBy2
                        val xComputer: Function<Int?, Int?> = Function { i: Int? -> lastX - i!! }
                        bPainter.drawVertBorder(
                            borderStyle,
                            normalColor,
                            width,
                            widthBy2,
                            horizMid,
                            xComputer,
                            false
                        )
                    }
                    if (bbottom > 0) {
                        val borderStyle =
                            if (borderInfo == null) HtmlValues.BORDER_STYLE_SOLID else borderInfo.bottomStyle
                        val normalColor = this.getBorderBottomColor()
                        val width = bbottom
                        val lastY = (startY + totalHeight) - 1
                        val widthBy2 = width / 2
                        val vertMid = lastY - widthBy2
                        val yComputer: Function<Int?, Int?> = Function { i: Int? -> lastY - i!! }
                        bPainter.drawHorizBorder(
                            borderStyle,
                            normalColor,
                            width,
                            widthBy2,
                            vertMid,
                            yComputer,
                            false
                        )
                    }
                    if (bleft > 0) {
                        val borderStyle =
                            if (borderInfo == null) HtmlValues.BORDER_STYLE_SOLID else borderInfo.leftStyle
                        val normalColor = this.getBorderLeftColor()
                        val width = bleft
                        val widthBy2 = width / 2
                        val horizMid = startX + widthBy2
                        val xComputer: Function<Int?, Int?> = Function { i: Int? -> startX + i!! }
                        bPainter.drawVertBorder(
                            borderStyle,
                            normalColor,
                            width,
                            widthBy2,
                            horizMid,
                            xComputer,
                            true
                        )
                    }
                }
            }
        }
    }

    override fun imageUpdate(img: Image?, infoflags: Int, x: Int, y: Int, w: Int, h: Int): Boolean {
        // This is so that a loading image doesn't cause
        // too many repaint events.
        if (((infoflags and ImageObserver.ALLBITS) != 0) || ((infoflags and ImageObserver.FRAMEBITS) != 0)) {
            SwingUtilities.invokeLater(Runnable {
                this.repaint()
            })
        }
        return true
    }

    open fun getBorderInsets(): Insets {
        val bi = this.borderInsets
        return if (bi == null) RBlockViewport.Companion.ZERO_INSETS else borderOverrider.get(bi)
    }

    /**
     * Gets insets of content area. It includes margin, borders, padding and
     * scrollbars.
     */
    override fun getInsets(hscroll: Boolean, vscroll: Boolean): Insets {
        return getInsets(hscroll, vscroll, true, true, true)
    }

    /**
     * Gets insets of content area. It includes margin, borders, and scrollbars
     * but excludes padding.
     */
    override fun getInsetsMarginBorder(hscroll: Boolean, vscroll: Boolean): Insets {
        return getInsets(hscroll, vscroll, true, true, false)
    }

    val insetsMarginPadding: Insets
        get() = getInsets(false, false, true, false, true)

    fun getInsetsPadding(hscroll: Boolean, vscroll: Boolean): Insets {
        return getInsets(hscroll, vscroll, false, false, true)
    }

    // TODO: This method could be inlined manually for performance
    private fun getInsets(
        hscroll: Boolean, vscroll: Boolean,
        includeMI: Boolean, includeBI: Boolean, includePI: Boolean
    ): Insets {
        val mi = this.marginInsets
        val bi = this.borderInsets
        val pi = this.paddingInsets
        var top = 0
        var bottom = 0
        var left = 0
        var right = 0
        if (includeMI && (mi != null)) {
            top += mi.top
            left += mi.left
            bottom += mi.bottom
            right += mi.right
        }
        if (includeBI && (bi != null)) {
            top += bi.top
            left += bi.left
            bottom += bi.bottom
            right += bi.right
        }
        if (includePI && (pi != null)) {
            top += pi.top
            left += pi.left
            bottom += pi.bottom
            right += pi.right
        }
        if (hscroll) {
            bottom += SCROLL_BAR_THICKNESS
        }
        if (vscroll) {
            right += SCROLL_BAR_THICKNESS
        }
        return Insets(top, left, bottom, right)
    }

    protected fun sendDelayedPairsToParent() {
        // Ensures that parent has all the components
        // below this renderer node. (Parent expected to have removed them).
        val gc = this.delayedPairs
        if (gc != null) {
            val rc = this.container
            val i: MutableIterator<DelayedPair?> = gc.iterator()
            while (i.hasNext()) {
                val pair = i.next()
                if (pair!!.containingBlock !== this) {
                    rc?.addDelayedPair(pair)
                }
            }
        }
    }

    override fun clearDelayedPairs() {
        val gc = this.delayedPairs
        if (gc != null) {
            gc.clear()
        }
    }

    fun getDelayedPairs(): MutableCollection<DelayedPair?>? {
        return this.delayedPairs
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.RenderableContainer#add(java.awt.Component)
     */
    override fun addDelayedPair(pair: DelayedPair?) {
        // Expected to be called in GUI thread.
        // Adds only in local collection.
        // Does not remove from parent.
        // Sending components to parent is done
        // by sendDelayedPairsToParent().
        var gc = this.delayedPairs
        if (gc == null) {
            // Sequence is important.
            // TODO: But possibly added multiple
            // times in table layout?
            gc = LinkedList<DelayedPair?>()
            this.delayedPairs = gc
        }
        gc.add(pair)
    }

    fun getParentContainer(): RenderableContainer {
        return this.container!!
    }

    fun isContainedByNode(): Boolean {
        return true
    }

    fun getCollapsibleMarginBottom(): Int {
        var cm: Int
        val paddingInsets = this.paddingInsets
        if ((paddingInsets != null) && (paddingInsets.bottom > 0)) {
            cm = 0
        } else {
            val borderInsets = this.borderInsets
            if ((borderInsets != null) && (borderInsets.bottom > 0)) {
                cm = 0
            } else {
                cm = this.getMarginBottom()
            }
        }
        if (this.isMarginBoundary) {
            val rs = this.modelNode?.renderState()
            if (rs != null) {
                val fm = rs.fontMetrics
                val fontHeight = fm!!.height
                if (fontHeight > cm) {
                    cm = fontHeight
                }
            }
        }
        return cm
    }

    protected open val isMarginBoundary: Boolean
        get() = ((this.overflowY != RenderState.OVERFLOW_VISIBLE) && (this.overflowX != RenderState.OVERFLOW_NONE))
                || (this.modelNode is HTMLDocumentImpl)

    fun getCollapsibleMarginTop(): Int {
        var cm: Int
        val paddingInsets = this.paddingInsets
        if ((paddingInsets != null) && (paddingInsets.top > 0)) {
            cm = 0
        } else {
            val borderInsets = this.borderInsets
            if ((borderInsets != null) && (borderInsets.top > 0)) {
                cm = 0
            } else {
                cm = this.getMarginTop()
            }
        }
        if (this.isMarginBoundary) {
            val rs = this.modelNode!!.renderState()
            if (rs != null) {
                val fm = rs.fontMetrics!!
                val fontHeight = fm.height
                if (fontHeight > cm) {
                    cm = fontHeight
                }
            }
        }
        return cm
    }

    fun getMarginBottom(): Int {
        val marginInsets = this.marginInsets
        return if (marginInsets == null) 0 else marginInsets.bottom
    }

    fun getMarginLeft(): Int {
        val marginInsets = this.marginInsets
        return if (marginInsets == null) 0 else marginInsets.left
    }

    fun getMarginRight(): Int {
        val marginInsets = this.marginInsets
        return if (marginInsets == null) 0 else marginInsets.right
    }

    fun getMarginTop(): Int {
        val marginInsets = this.marginInsets
        return if (marginInsets == null) 0 else marginInsets.top
    }

    override fun invalidateRenderStyle() {
        applyLook()
    }

    override fun translateDescendentPoint(
        descendent: BoundableRenderable,
        px: Int,
        py: Int
    ): Point {

        val p = descendent.getOriginRelativeTo(this)
        p.translate(px, py)
        return p

        /* The following is the original implementation. It should be equivalent to the above */
        /*
    while (descendent != this) {
      if (descendent == null) {
        // throw new IllegalStateException("Not descendent");
        System.err.println("Descendant not found!");
        return new java.awt.Point(x, y);
      }
      x += descendent.getVisualX();
      y += descendent.getVisualY();
      // Coordinates are always relative to actual parent?
      descendent = descendent.getParent();
    }
    return new java.awt.Point(x, y);
    */
    }

    open fun getClipBounds(): Rectangle? {
        // TODO: Check when this is called and see whether to use margin-border insets just as in rblock's override
        val insets = this.getInsetsPadding(false, false)
        val hInset = insets.left + insets.right
        val vInset = insets.top + insets.bottom
        if (((overflowX == RenderState.OVERFLOW_NONE) || (overflowX == RenderState.OVERFLOW_VISIBLE))
            && ((overflowY == RenderState.OVERFLOW_NONE) || (overflowY == RenderState.OVERFLOW_VISIBLE))
        ) {
            // return new Rectangle(insets.left, insets.top, this.getVisualWidth() - hInset, this.getVisualHeight() - vInset);
            return null
        } else {
            return Rectangle(insets.left, insets.top, this.width - hInset, this.height - vInset)
        }
    }

    override fun getClipBoundsWithoutInsets(): Rectangle? {
        // TODO: Stub
        return null
    }

    private fun setupRelativePosition(rs: RenderState, containerWidth: Int, containerHeight: Int) {
        if (rs.position == RenderState.POSITION_RELATIVE) {
            val leftText = rs.left
            val topText = rs.top

            var left = 0

            if (leftText != null && ("auto" != leftText)) {
                left = HtmlValues.getPixelSize(leftText, rs, 0, containerWidth)
            } else {
                val rightText = rs.right
                if (rightText != null) {
                    val right = HtmlValues.getPixelSize(rightText, rs, 0, containerWidth)
                    left = -right
                    // If right==0 and renderable.width is larger than the parent's width,
                    // the expected behavior is for newLeft to be negative.
                }
            }

            var top = 0

            if (topText != null && ("auto" != topText)) {
                top = HtmlValues.getPixelSize(topText, rs, top, containerHeight)
            } else {
                val bottomText = rs.bottom
                if (bottomText != null) {
                    val bottom = HtmlValues.getPixelSize(bottomText, rs, 0, containerHeight)
                    top = -bottom
                }
            }

            this.relativeOffsetX = left
            this.relativeOffsetY = top
        } else {
            this.relativeOffsetX = 0
            this.relativeOffsetY = 0
        }
    }

    override fun getVisualX(): Int {
        return super.getVisualX() + relativeOffsetX
    }

    override fun getVisualY(): Int {
        return super.getVisualY() + relativeOffsetY
    }

    override fun setupRelativePosition(container: RenderableContainer) {

        // TODO Use parent height
        setupRelativePosition(
            getModelNode()!!.renderState()!!,
            container.innerMostWidth,
            container.innerMostHeight
        )
    }

    override fun isReadyToPaint(): Boolean {
        val superReady: Boolean = isReadyToPaint
        if (!superReady) {
            return false
        }

        val node = this.modelNode!!
        val rs = node.renderState()
        val binfo = if (rs == null) null else rs.backgroundInfo
        if (binfo != null && binfo.backgroundImage != null) {
            return this.backgroundImage != null || backgroundImageError
        } else {
            return true
        }
    }

    internal class BorderPainter(
        val g: Graphics,
        val totalWidth: Int,
        val totalHeight: Int,
        val startX: Int,
        val startY: Int,
        val btop: Int,
        val bbottom: Int,
        val bleft: Int,
        val bright: Int
    ) {
        fun drawVertBorder(
            borderStyle: Int, normalColor: Color, width: Int, widthBy2: Int, horizMid: Int,
            xComputer: Function<Int?, Int?>, mirror: Boolean
        ) {
            val darkColor = if (mirror) normalColor.brighter() else normalColor.darker().darker()
            val lightColor = if (mirror) normalColor.darker().darker() else normalColor.brighter()

            if (borderStyle == HtmlValues.BORDER_STYLE_DOTTED) {
                g.color = normalColor
                GUITasks.drawDotted(
                    g,
                    horizMid,
                    startY,
                    horizMid,
                    startY + totalHeight,
                    width.toFloat()
                )
            } else {
                val widthBy3 = width / 3
                g.color = getInitialBorderColor(borderStyle, normalColor, lightColor, darkColor)
                var i = 0
                while (i < width) {
                    val x: Int = xComputer.apply(i)!!
                    val topOffset = (i * btop) / width
                    val bottomOffset = (i * bbottom) / width
                    val y1 = startY + topOffset
                    val y2 = (startY + totalHeight) - bottomOffset - 1
                    i += drawBorderSlice(
                        borderStyle,
                        darkColor,
                        lightColor,
                        width,
                        widthBy2,
                        widthBy3,
                        i,
                        x,
                        y1,
                        x,
                        y2
                    )
                    i++
                }
            }
        }

        fun drawHorizBorder(
            borderStyle: Int, normalColor: Color, width: Int, widthBy2: Int, vertMid: Int,
            yComputer: Function<Int?, Int?>, mirror: Boolean
        ) {
            val darkColor = if (mirror) normalColor.brighter() else normalColor.darker().darker()
            val lightColor = if (mirror) normalColor.darker().darker() else normalColor.brighter()

            if (borderStyle == HtmlValues.BORDER_STYLE_DOTTED) {
                g.color = normalColor
                GUITasks.drawDotted(
                    g,
                    startX,
                    vertMid,
                    startX + totalWidth,
                    vertMid,
                    width.toFloat()
                )
            } else {
                val widthBy3 = width / 3
                g.color = getInitialBorderColor(borderStyle, normalColor, lightColor, darkColor)
                var i = 0
                while (i < width) {
                    val y: Int = yComputer.apply(i)!!
                    val leftOffset = (i * bleft) / width
                    val rightOffset = (i * bright) / width
                    val x1 = startX + leftOffset
                    val x2 = (startX + totalWidth) - rightOffset - 1
                    i += drawBorderSlice(
                        borderStyle,
                        darkColor,
                        lightColor,
                        width,
                        widthBy2,
                        widthBy3,
                        i,
                        x1,
                        y,
                        x2,
                        y
                    )
                    i++
                }
            }
        }

        private fun drawBorderSlice(
            borderStyle: Int, darkColor: Color?, lightColor: Color?, width: Int, widthBy2: Int,
            widthBy3: Int, i: Int, x: Int, y: Int, x2: Int, y2: Int
        ): Int {
            var skipAmount = 0

            if (borderStyle == HtmlValues.BORDER_STYLE_DASHED) {
                GUITasks.drawDashed(g, x, y, x2, y2, 10 + width, 6)
            } else {
                if (i == widthBy2) {
                    if (borderStyle == HtmlValues.BORDER_STYLE_GROOVE) {
                        g.color = darkColor
                    } else if (borderStyle == HtmlValues.BORDER_STYLE_RIDGE) {
                        g.color = lightColor
                    }
                } else if (i == (widthBy3 - 1)) {
                    if (borderStyle == HtmlValues.BORDER_STYLE_DOUBLE) {
                        skipAmount = widthBy3
                    }
                }
                g.drawLine(x, y, x2, y2)
            }
            return skipAmount
        }

        companion object {
            private fun getInitialBorderColor(
                borderStyle: Int,
                normalColor: Color?,
                lightColor: Color?,
                darkColor: Color?
            ): Color? {
                if (borderStyle == HtmlValues.BORDER_STYLE_INSET) {
                    return lightColor
                } else if (borderStyle == HtmlValues.BORDER_STYLE_OUTSET) {
                    return darkColor
                } else if (borderStyle == HtmlValues.BORDER_STYLE_GROOVE) {
                    return lightColor
                } else if (borderStyle == HtmlValues.BORDER_STYLE_RIDGE) {
                    return darkColor
                } else {
                    return normalColor
                }
            }
        }
    }

    companion object {
        protected val INVALID_SIZE: Int = Int.Companion.MIN_VALUE
        protected const val SCROLL_BAR_THICKNESS: Int = 16
        private fun getImageY(totalHeight: Int, binfo: BackgroundInfo?, h: Int): Int {
            if (binfo == null) {
                return 0
            } else {
                if (binfo.backgroundYPositionAbsolute) {
                    return binfo.backgroundYPosition
                } else {
                    return (binfo.backgroundYPosition * (totalHeight - h)) / 100
                }
            }
        }

        private fun getImageX(totalWidth: Int, binfo: BackgroundInfo?, w: Int): Int {
            if (binfo == null) {
                return 0
            } else {
                if (binfo.backgroundXPositionAbsolute) {
                    return binfo.backgroundXPosition
                } else {
                    return (binfo.backgroundXPosition * (totalWidth - w)) / 100
                }
            }
        }
    }
}