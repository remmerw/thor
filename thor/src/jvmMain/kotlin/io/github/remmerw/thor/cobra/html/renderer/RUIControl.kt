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

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.domimpl.UINode
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.event.MouseEvent

/**
 * @author J. H. S.
 */
internal open class RUIControl(
    me: ModelNode?, widget: UIControl, container: RenderableContainer?, frameContext: FrameContext,
    ucontext: UserAgentContext?
) : BaseElementRenderable(container, me, ucontext) {
    val widget: UIControl
    private val frameContext: FrameContext
    private val cachedLayout: MutableMap<LayoutKey?, LayoutValue?> =
        HashMap<LayoutKey?, LayoutValue?>(5)
    private var declaredWidth = -1
    private var declaredHeight = -1
    private var lastLayoutKey: LayoutKey? = null
    private var lastLayoutValue: LayoutValue? = null
    protected var isWidthConstrained: Boolean = false
        private set
    protected var isHeightConstrained: Boolean = false
        private set

    init {
        this.widget = widget
        this.frameContext = frameContext
        widget.setRUIControl(this)
    }

    override fun focus() {
        super.focus()
        val c = this.widget.getComponent()
        c.requestFocus()
    }

    override fun invalidateLayoutLocal() {
        // Invalidate widget (some redundancy)
        super.invalidateLayoutLocal()
        this.widget.invalidate()
        // Invalidate cached values
        this.cachedLayout.clear()
        this.lastLayoutKey = null
        this.lastLayoutValue = null
    }

    override fun getVAlign(): VerticalAlign? {
        return this.widget.getVAlign()
    }

    fun hasBackground(): Boolean {
        return (this.backgroundColor != null) || (this.backgroundImage != null) || (this.lastBackgroundImageUri != null)
    }

    override fun paintShifted(g: Graphics) {
        val rs = this.modelNode.renderState
        if ((rs != null) && (rs.visibility != RenderState.VISIBILITY_VISIBLE)) {
            // Just don't paint it.
            return
        }
        // Prepaint borders, background images, etc.
        this.prePaint(g)
        // We need to paint the GUI component.
        // For various reasons, we need to do that
        // instead of letting AWT do it.
        val insets = this.getBorderInsets()
        g.translate(insets.left, insets.top)
        try {
            this.widget.paint(g)
        } finally {
            g.translate(-insets.left, -insets.top)
        }
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
     * org.xamjwg.html.renderer.BoundableRenderable#invalidateState(org.xamjwg
     * .html.renderer.RenderableContext)
     */
    override fun invalidateRenderStyle() {
        // NOP - No RenderStyle below this node.
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.domimpl.ContainingBlockContext#repaint(org.xamjwg.html.
     * renderer.RenderableContext)
     */
    override fun repaint(modelNode: ModelNode?) {
        val widget: Any? = this.widget
        if (widget is UINode) {
            widget.repaint(modelNode)
        } else {
            this.repaint()
        }
    }

    override fun updateWidgetBounds(guiX: Int, guiY: Int) {
        // Overrides
        super.updateWidgetBounds(guiX, guiY)
        val insets = this.getBorderInsets()
        this.widget.setBounds(
            guiX + insets.left,
            guiY + insets.top,
            this.width - insets.left - insets.right,
            (this.height - insets.top
                    - insets.bottom)
        )
    }

    override fun getBlockBackgroundColor(): Color? {
        return this.widget.getBackgroundColor()
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
        startPoint: RenderableSpot?,
        endPoint: RenderableSpot?
    ): Boolean {
        var inSelection = inSelection
        inSelection = super.paintSelection(g, inSelection, startPoint, endPoint)
        if (inSelection) {
            val over = Color(0, 0, 255, 50)
            val oldColor = g.color
            try {
                g.color = over
                g.fillRect(0, 0, this.width, this.height)
            } finally {
                g.color = oldColor
            }
        }
        return inSelection
    }

    override fun extractSelectionText(
        buffer: StringBuffer?, inSelection: Boolean, startPoint: RenderableSpot?,
        endPoint: RenderableSpot?
    ): Boolean {
        // No text here
        return inSelection
    }

    override fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot {
        // Nothing draggable - return self
        return RenderableSpot(this, x, y)
    }

    override fun doLayout(availWidth: Int, availHeight: Int, sizeOnly: Boolean) {
        val cachedLayout = this.cachedLayout
        val rs = this.modelNode.renderState
        val whitespace = if (rs == null) RenderState.WS_NORMAL else rs.whiteSpace
        val font = if (rs == null) null else rs.font
        val layoutKey = LayoutKey(availWidth, availHeight, whitespace, font)
        var layoutValue: LayoutValue?
        if (sizeOnly) {
            layoutValue = cachedLayout.get(layoutKey)
        } else {
            if (this.lastLayoutKey == layoutKey) {
                layoutValue = this.lastLayoutValue
            } else {
                layoutValue = null
            }
        }
        if (layoutValue == null) {
            this.applyStyle(availWidth, availHeight)

            val widget = this.widget
            widget.reset(availWidth, availHeight)

            val renderState = this.modelNode.renderState
            var paddingInsets = this.paddingInsets
            if (paddingInsets == null) {
                paddingInsets = RBlockViewport.Companion.ZERO_INSETS
            }
            var borderInsets = this.borderInsets
            if (borderInsets == null) {
                borderInsets = RBlockViewport.Companion.ZERO_INSETS
            }
            var marginInsets = this.marginInsets
            if (marginInsets == null) {
                marginInsets = RBlockViewport.Companion.ZERO_INSETS
            }

            val actualAvailWidth =
                (availWidth - paddingInsets!!.left - paddingInsets.right - borderInsets!!.left - borderInsets.right
                        - marginInsets!!.left - marginInsets.right)
            val actualAvailHeight =
                (availHeight - paddingInsets.top - paddingInsets.bottom - borderInsets.top - borderInsets.bottom
                        - marginInsets.top - marginInsets.bottom)
            val dw = this.getDeclaredWidth(renderState, actualAvailWidth)
            val dh = this.getDeclaredHeight(renderState, actualAvailHeight)
            val declaredWidth = if (dw == null) -1 else dw
            val declaredHeight = if (dh == null) -1 else dh
            this.declaredWidth = declaredWidth
            this.declaredHeight = declaredHeight

            this.isWidthConstrained = declaredWidth != -1
            this.isHeightConstrained = declaredHeight != -1

            val insets = this.getInsets(false, false)
            var finalWidth =
                if (declaredWidth == -1) -1 else declaredWidth + insets.left + insets.right
            var finalHeight =
                if (declaredHeight == -1) -1 else declaredHeight + insets.top + insets.bottom
            if ((finalWidth == -1) || (finalHeight == -1)) {
                val size = widget.getPreferredSize()
                if (finalWidth == -1) {
                    finalWidth = size.width + insets.left + insets.right
                }
                if (finalHeight == -1) {
                    finalHeight = size.height + insets.top + insets.bottom
                }
            }

            run {
                val maxWidth = getDeclaredMaxWidth(renderState, actualAvailWidth)
                if (maxWidth != null) {
                    if (finalWidth > maxWidth) {
                        finalWidth = maxWidth
                        this.isWidthConstrained = true
                    }
                }
            }
            run {
                val minWidth = getDeclaredMinWidth(renderState, actualAvailWidth)
                if (minWidth != null) {
                    if (finalWidth < minWidth) {
                        finalWidth = minWidth
                        this.isWidthConstrained = true
                    }
                }
            }

            run {
                val maxHeight = getDeclaredMaxHeight(renderState, actualAvailHeight)
                if (maxHeight != null) {
                    if (finalHeight > maxHeight) {
                        finalHeight = maxHeight
                        this.isHeightConstrained = true
                    }
                }
            }

            run {
                val minHeight = getDeclaredMinHeight(renderState, actualAvailHeight)
                if (minHeight != null) {
                    if (finalHeight < minHeight) {
                        finalHeight = minHeight
                        this.isHeightConstrained = true
                    }
                }
            }

            layoutValue = LayoutValue(finalWidth, finalHeight)
            if (sizeOnly) {
                if (cachedLayout.size > MAX_CACHE_SIZE) {
                    // Unlikely, but we should ensure it's bounded.
                    cachedLayout.clear()
                }
                cachedLayout.put(layoutKey, layoutValue)
                this.lastLayoutKey = null
                this.lastLayoutValue = null
            } else {
                this.lastLayoutKey = layoutKey
                this.lastLayoutValue = layoutValue
            }
        }
        this.width = layoutValue.width
        this.height = layoutValue.height
    }

    /**
     * May be called by controls when they wish to modifiy their preferred size
     * (e.g. an image after it's loaded). This method must be called in the GUI
     * thread.
     */
    fun preferredSizeInvalidated() {
        val dw = this@RUIControl.declaredWidth
        val dh = this@RUIControl.declaredHeight
        if ((dw == -1) || (dh == -1)) {
            this.frameContext.delayedRelayout(this.modelNode as NodeImpl?)
        } else {
            this@RUIControl.repaint()
        }
    }

    override fun getRenderables(topFirst: Boolean): MutableIterator<Renderable>? {
        // No children for GUI controls
        return null
    }

    override fun getPaintedBackgroundColor(): Color? {
        return this.container.getPaintedBackgroundColor()
    }

    val foregroundColor: Color?
        get() {
            val rs = this.modelNode.renderState
            return if (rs == null) null else rs.color
        }

    override fun setInnerWidth(newWidth: Int?) {
        super.setInnerWidth(newWidth)
        this.isWidthConstrained = true
    }

    override fun setInnerHeight(newHeight: Int?) {
        super.setInnerHeight(newHeight)
        this.isHeightConstrained = true
    }

    private class LayoutKey(availWidth: Int, availHeight: Int, whitespace: Int, font: Font?) {
        val availWidth: Int
        val availHeight: Int
        val whitespace: Int
        val font: Font?

        init {
            this.availWidth = availWidth
            this.availHeight = availHeight
            this.whitespace = whitespace
            this.font = font
        }

        override fun equals(obj: Any?): Boolean {
            if (obj === this) {
                return true
            }
            if (obj !is LayoutKey) {
                return false
            }
            return (obj.availWidth == this.availWidth) && (obj.availHeight == this.availHeight) && (obj.whitespace == this.whitespace)
                    && obj.font == this.font
        }

        override fun hashCode(): Int {
            val font = this.font
            return ((this.availWidth * 1000) + this.availHeight) xor (if (font == null) 0 else font.hashCode())
        }
    }

    private class LayoutValue(width: Int, height: Int) {
        val width: Int
        val height: Int

        init {
            this.width = width
            this.height = height
        }
    }

    companion object {
        private const val MAX_CACHE_SIZE = 10
    }
}
