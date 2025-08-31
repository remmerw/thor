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
package io.github.remmerw.thor.cobra.html.renderer

import cz.vutbr.web.css.CSSProperty
import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.style.BlockRenderState
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.html.style.RenderThreadState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.util.CollectionUtilities
import org.w3c.dom.html.HTMLHtmlElement
import java.awt.Adjustable
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import java.awt.event.MouseEvent
import javax.swing.JScrollBar
import kotlin.math.max
import kotlin.math.min


/**
 * Represents a HTML block in a rendered document, typically a DIV. The root
 * renderer node is of this type as well.
 *
 *
 * Immediately below an `RBlock` you will find a node of type
 * [RBlockViewport].
 */
open class RBlock(
    modelNode: NodeImpl?,
    protected val listNesting: Int,
    pcontext: UserAgentContext?,
    rcontext: HtmlRendererContext?,
    protected var frameContext: FrameContext?,
    parentContainer: RenderableContainer?
) : BaseBlockyRenderable(parentContainer, modelNode, pcontext) {
    // protected final HtmlRendererContext rendererContext;
    val rBlockViewport: RBlockViewport

    // Used for relative positioning
    // private int relativeOffsetX = 0;
    // private int relativeOffsetY = 0;
    // protected final Map<LayoutKey, LayoutValue> cachedLayout = new Hashtable<>(5);
    protected var startSelection: RenderableSpot? = null
    protected var endSelection: RenderableSpot? = null
    protected var vScrollBar: JScrollBar? = null
    protected var hScrollBar: JScrollBar? = null
    var hasHScrollBar: Boolean = false
    var hasVScrollBar: Boolean = false

    // Validation-dependent variables...
    // private Dimension layoutSize = null;
    var defaultOverflowX: Int = RenderState.OVERFLOW_NONE
    var defaultOverflowY: Int = RenderState.OVERFLOW_NONE

    // private LayoutValue lastLayoutValue = null;
    // private LayoutKey lastLayoutKey = null;
    private var resettingScrollBars = false
    private var armedRenderable: BoundableRenderable? = null
    private var collapseTopMargin = false
    private var collapseBottomMargin = false
    var marginTopOriginal: Int? = null
        private set
    var marginBottomOriginal: Int? = null
        private set

    init {
        this.frameContext = frameContext
        // this.rendererContext = rcontext;
        val bl = RBlockViewport(
            modelNode, this, this.getViewportListNesting(
                listNesting
            ), pcontext!!, rcontext!!,
            frameContext,
            this
        )
        this.rBlockViewport = bl
        bl.setOriginalParent(this)
        // Initialize origin of RBlockViewport to be as far top-left as possible.
        // This will be corrected on first layout.
        bl.setX(Short.Companion.MAX_VALUE.toInt())
        bl.setY(Short.Companion.MAX_VALUE.toInt())
    }

    val vScrollBarWidth: Int
        /**
         * Gets the width the vertical scrollbar has when shown.
         */
        get() = SCROLL_BAR_THICKNESS

    fun ensureVisible(point: Point) {
        val bodyLayout = this.rBlockViewport
        val hscroll = this.hasHScrollBar
        val vscroll = this.hasVScrollBar
        val origX = bodyLayout.x()
        val origY = bodyLayout.y()
        val insets = this.getInsetsMarginBorder(hscroll, vscroll)
        if (hscroll) {
            if (point.x < insets.left) {
                bodyLayout.setX(bodyLayout.x() + (insets.left - point.x))
            } else if (point.x > (this.width() - insets.right)) {
                bodyLayout.setX((bodyLayout.x()) -(point.x - this.width()) + insets.right)
            }
        }
        if (vscroll) {
            if (point.y < insets.top) {
                bodyLayout.setY(bodyLayout.y() +(insets.top - point.y))
            } else if (point.y > (this.height() - insets.bottom)) {
                bodyLayout.setY(bodyLayout.y() -(point.y - this.height()) + insets.bottom)
            }
        }
        if (hscroll || vscroll) {
            this.correctViewportOrigin(insets, this.width(), this.height())
            if ((origX != bodyLayout.x()) || (origY != bodyLayout.y())) {
                this.resetScrollBars(null)
                // TODO: This could be paintImmediately.
                this.repaint()
            }
        }
    }

    private fun getHScrollBar(): JScrollBar {
        var sb = this.hScrollBar
        if (sb == null) {
            // Should never go back to null
            sb = JScrollBar(Adjustable.HORIZONTAL)
            sb.addAdjustmentListener(LocalAdjustmentListener(Adjustable.HORIZONTAL))
            this.hScrollBar = sb
        }
        return sb
    }

    private fun getVScrollBar(): JScrollBar {
        var sb = this.vScrollBar
        if (sb == null) {
            // Should never go back to null
            sb = JScrollBar(Adjustable.VERTICAL)
            sb.addAdjustmentListener(LocalAdjustmentListener(Adjustable.VERTICAL))
            this.vScrollBar = sb
        }
        return sb
    }

    val isOverflowVisibleX: Boolean
        // public final boolean couldBeScrollable() {
        get() {
            val overflow = this.overflowX
            return (overflow == RenderState.OVERFLOW_NONE) || (overflow == RenderState.OVERFLOW_VISIBLE)
        }

    val isOverflowVisibleY: Boolean
        get() {
            val overflow = this.overflowY
            return (overflow == RenderState.OVERFLOW_NONE) || (overflow == RenderState.OVERFLOW_VISIBLE)
        }

    val firstLineHeight: Int
        get() = this.rBlockViewport.firstLineHeight

    val firstBaselineOffset: Int
        get() = this.rBlockViewport.firstBaselineOffset

    fun setSelectionEnd(rpoint: RenderableSpot?) {
        this.endSelection = rpoint
    }

    fun setSelectionStart(rpoint: RenderableSpot?) {
        this.startSelection = rpoint
    }

    open fun getViewportListNesting(blockNesting: Int): Int {
        return blockNesting
    }

    override fun clipBounds(): Rectangle? {
        val insets = this.getInsetsMarginBorder(this.hasHScrollBar, this.hasVScrollBar)
        // final Insets insets = this.getInsetsPadding(this.hasHScrollBar, this.hasVScrollBar);
        // final Insets insets = this.getInsets(this.hasHScrollBar, this.hasVScrollBar);
        val hInset = insets.left + insets.right
        val vInset = insets.top + insets.bottom
        // if (((overflowX == RenderState.OVERFLOW_NONE) || (overflowX == RenderState.OVERFLOW_VISIBLE))
        // && ((overflowY == RenderState.OVERFLOW_NONE) || (overflowY == RenderState.OVERFLOW_VISIBLE))
        if (!(this.hasHScrollBar || this.hasVScrollBar)) {
            // return new Rectangle(insets.left - relativeOffsetX, insets.top - relativeOffsetY, this.getVisualWidth() - hInset, this.getVisualHeight() - vInset);
            // return new Rectangle(insets.left - relativeOffsetX, insets.top - relativeOffsetY, this.width - hInset, this.height - vInset);
            return null
            // return new Rectangle(0, 0, 100, 100);
        } else {
            // return new Rectangle(insets.left - relativeOffsetX, insets.top - relativeOffsetY, this.width - hInset, this.height - vInset);
            return Rectangle(
                -relativeOffsetX,
                -relativeOffsetY,
                this.width() - hInset,
                this.height() - vInset
            )
        }
    }

    override fun clipBoundsWithoutInsets(): Rectangle? {
        val hInset =
            if (this.hasVScrollBar) SCROLL_BAR_THICKNESS else 0
        val vInset =
            if (this.hasHScrollBar) SCROLL_BAR_THICKNESS else 0
        if (!(this.hasHScrollBar || this.hasVScrollBar)) {
            return null
        } else {
            // return new Rectangle( - relativeOffsetX,  - relativeOffsetY, this.width, this.height);
            return Rectangle(
                -relativeOffsetX,
                -relativeOffsetY,
                this.width() - hInset,
                this.height()- vInset
            )
        }
    }

    public override fun paintShifted(g: Graphics) {
        // TODO: Move this to common logic in BaseElementEenderable.pain();
        val rs = this.modelNode()!!.renderState()
        if ((rs != null) && (rs.visibility != RenderState.VISIBILITY_VISIBLE)) {
            // Just don't paint it.
            return
        }

        this.prePaint(g)

        val insets = this.getInsetsMarginBorder(this.hasHScrollBar, this.hasVScrollBar)
        val bodyLayout = this.rBlockViewport
        val overflowX = this.overflowX
        val overflowY = this.overflowY
        val isHtmlElem = (this.modelNode() is HTMLHtmlElement)
        val xVisible =
            (overflowX == RenderState.OVERFLOW_NONE) || (overflowX == RenderState.OVERFLOW_VISIBLE)
        val yVisible =
            (overflowY == RenderState.OVERFLOW_NONE) || (overflowY == RenderState.OVERFLOW_VISIBLE)
        val noScrolls = !(this.hasHScrollBar || this.hasVScrollBar)
        if (isHtmlElem || (xVisible && yVisible && noScrolls)) {
            bodyLayout.paint(g)
        } else {
            // Clip when there potential scrolling or hidden overflow  was requested.
            val newG = g.create(
                insets.left,
                insets.top,
                this.width() - insets.left - insets.right,
                (this.height() - insets.top
                        - insets.bottom)
            )
            try {
                // Second, translate
                newG.translate(-insets.left, -insets.top)
                // Third, paint in clipped + translated region.
                bodyLayout.paint(newG, g)
            } finally {
                newG.dispose()
            }
        }

        // Paint FrameContext selection.
        // This is only done by root RBlock.
        val start = this.startSelection
        val end = this.endSelection
        val inSelection = false
        if ((start != null) && (end != null) && (start != end)) {
            this.paintSelection(g, inSelection, start, end)
        }
        // Must paint scrollbars too.
        val hsb = this.hScrollBar
        if (hsb != null) {
            val sbg = g.create(
                insets.left, this.height() - insets.bottom, this.width() - insets.left - insets.right,
                SCROLL_BAR_THICKNESS
            )
            try {
                hsb.paint(sbg)
            } finally {
                sbg.dispose()
            }
        }
        val vsb = this.vScrollBar
        if (vsb != null) {
            val sbg = g
                .create(
                    this.width() - insets.right,
                    insets.top,
                    SCROLL_BAR_THICKNESS,
                    this.height() - insets.top - insets.bottom
                )
            try {
                vsb.paint(sbg)
            } finally {
                sbg.dispose()
            }
        }
    }

    // /**
    // * Adjustment step which must be done after layout. This will expand blocks
    // * that need to be expanded and relayout blocks with relative sizes
    // * accordingly.
    // *
    // * @param availWidth
    // * @param availHeight
    // * @param expandWidth
    // * @param expandHeight
    // */
    // public void adjust(int availWidth, int availHeight, boolean expandWidth,
    // boolean expandHeight, FloatingBoundsSource floatBoundsSource, boolean
    // useDeclaredSize) {
    // RenderState renderState = this.modelNode.getRenderState();
    // Font font = renderState == null ? null : renderState.getFont();
    // int whiteSpace = renderState == null ? RenderState.WS_NORMAL :
    // renderState.getWhiteSpace();
    // int tentativeWidth;
    // if(useDeclaredSize && floatBoundsSource != null) {
    // Integer declaredWidth = this.getDeclaredWidth(renderState, availWidth);
    // Insets insets = this.getInsets(this.hasHScrollBar, this.hasVScrollBar);
    // Insets paddingInsets = this.paddingInsets;
    // int hinsets = insets.left + insets.right + (paddingInsets == null ? 0 :
    // paddingInsets.left + paddingInsets.right);
    // tentativeWidth = declaredWidth == null ? availWidth :
    // declaredWidth.intValue() + hinsets;
    // }
    // else {
    // // Assuming that we don't care about this if
    // // floatBoundsSource == null.
    // tentativeWidth = availWidth;
    // }
    // FloatingBounds blockFloatBounds = floatBoundsSource == null ? null :
    // floatBoundsSource.getChildBlockFloatingBounds(tentativeWidth);
    // LayoutKey layoutKey = new LayoutKey(availWidth, availHeight,
    // blockFloatBounds, this.defaultOverflowX, this.defaultOverflowY, whiteSpace,
    // font, expandWidth, expandHeight, useDeclaredSize);
    // LayoutValue layoutValue = (LayoutValue) this.cachedAdjust.get(layoutKey);
    // // Expected to be invoked in the GUI thread.
    // if (layoutValue == null) {
    // layoutValue = this.forceAdjust(renderState, availWidth, availHeight,
    // expandWidth, expandHeight, blockFloatBounds, this.defaultOverflowX,
    // this.defaultOverflowY, useDeclaredSize);
    // this.cachedAdjust.put(layoutKey, layoutValue);
    // }
    //
    // // We send GUI components up in adjust() in case new ones were added.
    // this.sendGUIComponentsToParent();
    // // No sending delayed pairs here.
    // this.width = layoutValue.width;
    // this.height = layoutValue.height;
    // this.hasHScrollBar = layoutValue.hasHScrollBar;
    // this.hasVScrollBar = layoutValue.hasVScrollBar;
    // }
    //
    // /**
    // * This adjustment step needs to be performed after layout. In this case,
    // * the dimensions previously obtained in the layout are assumed to be the
    // * desired dimensions of the block.
    // * <p>
    // * When we first layout a block, we don't know its final width and height.
    // * It could be wider or narrower than originally assumed.
    // * Consider elements embedded in the block that have widths and heights
    // * specified by a percentage.
    // */
    // public void adjust() {
    // // Expected to be invoked in the GUI thread.
    // this.adjust(this.width, this.height, true, true, null, false);
    // }
    //
    // /**
    // *
    // * @param renderState
    // * @param tentativeWidth
    // * The tentative or max width that will be tried.
    // * @param tentativeHeight
    // * The tentative or max height that will be tried.
    // * @param adjDeclaredWidth
    // * The declared width plus margins.
    // * @param adjDeclaredHeight
    // * The declared height plus margins.
    // * @param floatBounds
    // * Float bounds that need to be passed to the viewport.
    // * @param defaultOverflow
    // */
    // private final LayoutValue forceAdjust(RenderState renderState, int
    // availWidth, int availHeight,
    // boolean expandWidth, boolean expandHeight,
    // FloatingBounds blockFloatBounds, int defaultOverflowX, int
    // defaultOverflowY, boolean useDeclaredSize) {
    // // Expected to be invoked in the GUI thread.
    // RenderState rs = renderState;
    // if (rs == null) {
    // rs = new BlockRenderState(null);
    // }
    // RBlockViewport bodyLayout = this.bodyLayout;
    // NodeImpl node = (NodeImpl) this.modelNode;
    // if (node == null || bodyLayout == null) {
    // Insets insets = this.getInsets(false, false);
    // return new LayoutValue(insets.left + insets.right, insets.bottom +
    // insets.top, false, false);
    // }
    //
    // // No clearing of GUI components here
    //
    // int overflowX = this.overflowX;
    // if (overflowX == RenderState.OVERFLOW_NONE) {
    // overflowX = defaultOverflowX;
    // }
    // int overflowY = this.overflowY;
    // if (overflowY == RenderState.OVERFLOW_NONE) {
    // overflowY = defaultOverflowY;
    // }
    // boolean autoY = overflowY == RenderState.OVERFLOW_AUTO;
    // boolean hscroll = overflowX == RenderState.OVERFLOW_SCROLL;
    // boolean hauto = overflowX == RenderState.OVERFLOW_AUTO;
    // boolean vscroll = overflowY == RenderState.OVERFLOW_SCROLL;
    // Insets paddingInsets = this.paddingInsets;
    // if (paddingInsets == null) {
    // paddingInsets = RBlockViewport.ZERO_INSETS;
    // }
    // Insets borderInsets = this.borderInsets;
    // if(borderInsets == null) {
    // borderInsets = RBlockViewport.ZERO_INSETS;
    // }
    // Insets marginInsets = this.marginInsets;
    // if(marginInsets == null) {
    // marginInsets = RBlockViewport.ZERO_INSETS;
    // }
    //
    // // Calculate presumed size of block.
    // int tentativeWidth;
    // int tentativeHeight;
    // int declaredWidth = -1;
    // int declaredHeight = -1;
    // if(useDeclaredSize) {
    // Integer dw = this.getDeclaredWidth(renderState, availWidth);
    // Integer dh = this.getDeclaredHeight(renderState, availHeight);
    // if (dw != null) {
    // declaredWidth = dw.intValue();
    // }
    // if (dh != null) {
    // declaredHeight = dh.intValue();
    // }
    // }
    // if(declaredWidth == -1) {
    // tentativeWidth = availWidth;
    // }
    // else {
    // tentativeWidth = declaredWidth + paddingInsets.left + paddingInsets.right +
    // borderInsets.left + borderInsets.right + marginInsets.left +
    // marginInsets.right;
    // }
    // if(declaredHeight == -1) {
    // tentativeHeight = availHeight;
    // }
    // else {
    // tentativeHeight = declaredHeight + paddingInsets.top + paddingInsets.bottom
    // + borderInsets.top + borderInsets.bottom + marginInsets.top +
    // marginInsets.bottom;
    // }
    // Insets insets = null;
    // for (int tries = (autoY ? 0 : 1); tries < 2; tries++) {
    // try {
    // insets = this.getInsets(hscroll, vscroll);
    // int desiredViewportWidth = tentativeWidth - insets.left
    // - insets.right;
    // int desiredViewportHeight = tentativeHeight - insets.top
    // - insets.bottom;
    // FloatingBounds viewportFloatBounds = null;
    // if (blockFloatBounds != null) {
    // viewportFloatBounds = new ShiftedFloatingBounds(
    // blockFloatBounds, -insets.left, -insets.right,
    // -insets.top);
    // }
    // bodyLayout.adjust(desiredViewportWidth, desiredViewportHeight,
    // paddingInsets, viewportFloatBounds);
    // break;
    // } catch (SizeExceededException hee) {
    // if (tries != 0) {
    // throw new IllegalStateException("tries=" + tries + ",autoY="
    // + autoY);
    // }
    // vscroll = true;
    // }
    // }
    // // Dimension size = bodyLayout.getSize();
    // // Dimension rblockSize = new Dimension(size.width + insets.left +
    // // insets.right, size.height + insets.top + insets.bottom);
    // int rblockWidth = bodyLayout.width + insets.left + insets.right;
    // int adjDeclaredWidth = declaredWidth == -1 ? -1 : declaredWidth +
    // insets.left + insets.right + paddingInsets.left + paddingInsets.right;
    // int adjDeclaredHeight = declaredHeight == -1 ? -1 : declaredHeight +
    // insets.top + insets.bottom + paddingInsets.top + paddingInsets.bottom;
    // if (hauto
    // && !hscroll
    // && ((adjDeclaredWidth != -1 && rblockWidth > adjDeclaredWidth) ||
    // (rblockWidth > tentativeWidth))) {
    // hscroll = true;
    // insets = this.getInsets(hscroll, vscroll);
    // rblockWidth = bodyLayout.width + insets.left + insets.right;
    // }
    // // Calculate resulting width.
    // boolean visibleX = overflowX == RenderState.OVERFLOW_VISIBLE || overflowX
    // == RenderState.OVERFLOW_NONE;
    // boolean visibleY = overflowY == RenderState.OVERFLOW_VISIBLE || overflowY
    // == RenderState.OVERFLOW_NONE;
    // int resultingWidth;
    // if (adjDeclaredWidth == -1) {
    // resultingWidth = rblockWidth;
    // if (hscroll && resultingWidth > tentativeWidth) {
    // resultingWidth = Math.max(tentativeWidth, SCROLL_BAR_THICKNESS);
    // } else if (expandWidth && resultingWidth < tentativeWidth) {
    // resultingWidth = tentativeWidth;
    // }
    // } else {
    // resultingWidth = visibleX ? Math.max(rblockWidth, adjDeclaredWidth)
    // : adjDeclaredWidth;
    // }
    // // Align horizontally now. This may change canvas height.
    // int alignmentXPercent = rs.getAlignXPercent();
    // if (alignmentXPercent > 0) {
    // // TODO: OPTIMIZATION: alignment should not be done in table cell
    // // sizing determination.
    // int canvasWidth = Math.max(bodyLayout.width, resultingWidth
    // - insets.left - insets.right);
    // // Alignment is done afterwards because canvas dimensions might have
    // // changed.
    // bodyLayout.alignX(alignmentXPercent, canvasWidth, paddingInsets);
    // }
    //
    // int resultingHeight;
    // int rblockHeight = bodyLayout.height + insets.top + insets.bottom;
    // if (autoY
    // && !vscroll
    // && ((adjDeclaredHeight != -1 && rblockHeight > adjDeclaredHeight) ||
    // (rblockHeight > tentativeHeight))) {
    // vscroll = true;
    // insets = this.getInsets(hscroll, vscroll);
    // rblockHeight = bodyLayout.height + insets.top + insets.bottom;
    // }
    // if (adjDeclaredHeight == -1) {
    // resultingHeight = rblockHeight;
    // if (vscroll && resultingHeight > tentativeHeight) {
    // resultingHeight = Math.max(tentativeHeight,
    // SCROLL_BAR_THICKNESS);
    // } else if (expandHeight && resultingHeight < tentativeHeight) {
    // resultingHeight = tentativeHeight;
    // }
    // } else {
    // resultingHeight = visibleY ? Math.max(rblockHeight,
    // adjDeclaredHeight) : adjDeclaredHeight;
    // }
    //
    // // Align vertically now
    // int alignmentYPercent = rs.getAlignYPercent();
    // if (alignmentYPercent > 0) {
    // // TODO: OPTIMIZATION: alignment should not be done in table cell
    // // sizing determination.
    // int canvasHeight = Math.max(bodyLayout.height, resultingHeight
    // - insets.top - insets.bottom);
    // // Alignment is done afterwards because canvas dimensions might have
    // // changed.
    // bodyLayout.alignY(alignmentYPercent, canvasHeight, paddingInsets);
    // }
    //
    // if (vscroll) {
    // JScrollBar sb = this.getVScrollBar();
    // this.addComponent(sb);
    // // Bounds set by updateWidgetBounds
    // }
    // if (hscroll) {
    // JScrollBar sb = this.getHScrollBar();
    // this.addComponent(sb);
    // // Bounds set by updateWidgetBounds
    // }
    //
    // if (hscroll || vscroll) {
    // // In this case, viewport origin should not be changed.
    // // We don't want to cause the document to scroll back
    // // up while rendering.
    // this.correctViewportOrigin(insets, resultingWidth, resultingHeight);
    // // Depends on width, height and origin
    // this.resetScrollBars(rs);
    // } else {
    // bodyLayout.x = insets.left;
    // bodyLayout.y = insets.top;
    // }
    // return new LayoutValue(resultingWidth, resultingHeight, hscroll, vscroll);
    // }
    fun layout(
        availWidth: Int, availHeight: Int, expandWidth: Boolean, expandHeight: Boolean,
        defaultOverflowX: Int,
        defaultOverflowY: Int, sizeOnly: Boolean
    ) {
        this.layout(
            availWidth,
            availHeight,
            expandWidth,
            expandHeight,
            null,
            defaultOverflowX,
            defaultOverflowY,
            sizeOnly
        )
    }

    override fun layout(
        availWidth: Int, availHeight: Int, expandWidth: Boolean, expandHeight: Boolean,
        floatBoundsSource: FloatingBoundsSource?, sizeOnly: Boolean
    ) {
        this.layout(
            availWidth,
            availHeight,
            expandWidth,
            expandHeight,
            floatBoundsSource,
            this.defaultOverflowX,
            this.defaultOverflowY,
            sizeOnly
        )
    }

    fun layout(
        availWidth: Int,
        availHeight: Int,
        expandWidth: Boolean,
        expandHeight: Boolean,
        floatBoundsSource: FloatingBoundsSource?,
        defaultOverflowX: Int,
        defaultOverflowY: Int,
        sizeOnly: Boolean
    ) {
        try {
            this.doLayout(
                availWidth,
                availHeight,
                expandWidth,
                expandHeight,
                floatBoundsSource,
                defaultOverflowX,
                defaultOverflowY,
                sizeOnly
            )
        } finally {
            this.layoutUpTreeCanBeInvalidated = true
            this.layoutDeepCanBeInvalidated = true
            // this.renderStyleCanBeInvalidated = true;
        }
    }

    public override fun doLayout(availWidth: Int, availHeight: Int, sizeOnly: Boolean) {
        // This is an override of an abstract method.
        this.doLayout(
            availWidth,
            availHeight,
            true,
            false,
            null,
            this.defaultOverflowX,
            this.defaultOverflowY,
            sizeOnly
        )
    }

    open fun doLayout(
        availWidth: Int, availHeight: Int, expandWidth: Boolean, expandHeight: Boolean,
        floatBoundsSource: FloatingBoundsSource?,
        defaultOverflowX: Int, defaultOverflowY: Int, sizeOnly: Boolean
    ) {
        this.doLayout(
            availWidth,
            availHeight,
            expandWidth,
            expandHeight,
            floatBoundsSource,
            defaultOverflowX,
            defaultOverflowY,
            sizeOnly,
            true
        )
    }

    /**
     * Lays out and sets dimensions only if RBlock is invalid (or never before
     * layed out), if the parameters passed differ from the last layout, or if the
     * current font differs from the font for the last layout.
     *
     * @param availWidth
     * @param availHeight
     * @param useCache    For testing. Should always be true.
     */
    fun doLayout(
        availWidth: Int, availHeight: Int, expandWidth: Boolean, expandHeight: Boolean,
        floatBoundsSource: FloatingBoundsSource?,
        defaultOverflowX: Int, defaultOverflowY: Int, sizeOnly: Boolean, useCache: Boolean
    ) {
        // Expected to be invoked in the GUI thread.
        val renderState: RenderState = this.modelNode()!!.renderState()!!
        /*
    final Font font = renderState == null ? null : renderState.getFont();
    final int whiteSpace = renderState == null ? RenderState.WS_NORMAL : renderState.getWhiteSpace();
    // Having whiteSpace == NOWRAP and having a NOWRAP override
    // are not exactly the same thing.
    final boolean overrideNoWrap = RenderThreadState.getState().overrideNoWrap;
    final LayoutKey key = new LayoutKey(availWidth, availHeight, expandWidth, expandHeight, floatBoundsSource, defaultOverflowX,
        defaultOverflowY, whiteSpace, font, overrideNoWrap);
    final Map<LayoutKey, LayoutValue> cachedLayout = this.cachedLayout;
    */
        var value: LayoutValue?
        /*
    if (sizeOnly) {
      value = useCache ? cachedLayout.get(key) : null;
    } else {
      if (Objects.equals(key, this.lastLayoutKey)) {
        value = this.lastLayoutValue;
      } else {
        value = null;
      }
    }*/
        value = null
        if (value == null) {
            value = this.forceLayout(
                renderState,
                availWidth,
                availHeight,
                expandWidth,
                expandHeight,
                floatBoundsSource,
                defaultOverflowX,
                defaultOverflowY,
                sizeOnly
            )
            if (sizeOnly) {
                // this.lastLayoutKey = null;
                // this.lastLayoutValue = null;

                /*
        if (cachedLayout.size() > MAX_CACHE_SIZE) {
          // Unlikely, but we should keep it bounded.
          cachedLayout.clear();
        }
        cachedLayout.put(key, value);
        */
            } else {
                // this.lastLayoutKey = key;
                // this.lastLayoutValue = value;
            }
        } else {
            /*
      System.out.println("Cached layout for " + this);
      final FloatingInfo finfo = getExportableFloatingInfo();
      if (finfo != null) {
        for ( ExportableFloat fi : finfo.floats) {
          fi.pendingPlacement = true;
        }
      }*/
        }
        this.setWidth(value.width)
        this.setHeight(value.height)
        this.hasHScrollBar = value.hasHScrollBar
        this.hasVScrollBar = value.hasVScrollBar

        rBlockViewport.positionDelayed()

        // Even if we didn't do layout, the parent is
        // expected to have removed its GUI components.
        this.sendGUIComponentsToParent()

        // Even if we didn't do layout, the parent is
        // expected to have removed its delayed pairs.
        this.sendDelayedPairsToParent()
    }

    private fun correctViewportOrigin(insets: Insets, blockWidth: Int, blockHeight: Int): Boolean {
        val bodyLayout = this.rBlockViewport
        val viewPortX = bodyLayout.x()
        val viewPortY = bodyLayout.y()
        var corrected = false
        if (viewPortX > insets.left) {
            bodyLayout.setX(insets.left)
            corrected = true
        } else if (viewPortX < (blockWidth - insets.right - bodyLayout.width())) {
            bodyLayout.setX(min(insets.left, blockWidth - insets.right - bodyLayout.width()))
            corrected = true
        }
        if (viewPortY > insets.top) {
            bodyLayout.setY(insets.top)
            corrected = true
        } else if (viewPortY < (blockHeight - insets.bottom - bodyLayout.visualHeight())) {
            bodyLayout.setY(
                min(insets.top, blockHeight - insets.bottom - bodyLayout.visualHeight()))
            corrected = true
        }
        return corrected
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMouseClick(java.awt.event
     * .MouseEvent, int, int)
     */
    /*
  public boolean onMouseClick(final MouseEvent event, final int x, final int y) {
    final RBlockViewport bodyLayout = this.bodyLayout;
    if (bodyLayout != null) {
      if (!bodyLayout.onMouseClick(event, x - bodyLayout.x, y - bodyLayout.y)) {
        return false;
      }
    }

    // changed for issue #106
    // the following was joined above with else, but it is now separated, so that the RBlock can continue processing even if bodyLayout didn't capture the click
    // this happens with, for example, a div which has large width and height, but tiny or empty contents.
    if (!HtmlController.getInstance().onMouseClick(this.modelNode, event, x, y)) {
      return false;
    }

    if (this.backgroundColor != null) {
      return false;
    }
    return true;
  }
  */
    /**
     * Lays out the block without checking for prior dimensions.
     *
     * @param declaredWidth   The declared width of the block.
     * @param declaredHeight  The declared height of the block.
     * @param tentativeWidth  Presumed width of the whole block (with margins).
     * @param tentativeHeight
     * @return
     */
    private fun forceLayout(
        renderState: RenderState, availWidth: Int, availHeight: Int,
        expandWidth: Boolean,
        expandHeight: Boolean, blockFloatBoundsSource: FloatingBoundsSource?, defaultOverflowX: Int,
        defaultOverflowY: Int, sizeOnly: Boolean
    ): LayoutValue {
        // Expected to be invoked in the GUI thread.
        // TODO: Not necessary to do full layout if only expandWidth or
        // expandHeight change (specifically in tables).
        var rs: RenderState? = renderState
        if (rs == null) {
            rs = BlockRenderState(null)
        }

        // // Clear adjust() cache.
        // this.cachedAdjust.clear();

        // We reprocess the rendering state.
        // Probably doesn't need to be done in its entirety every time.
        this.applyStyle(availWidth, availHeight)

        val bodyLayout = this.rBlockViewport
        val node = this.modelNode() as NodeImpl?
        if (node == null) {
            val insets = this.getInsetsMarginBorder(false, false)
            return LayoutValue(insets.left + insets.right, insets.bottom + insets.top, false, false)
        }

        var paddingInsets = this.paddingInsets
        if (paddingInsets == null) {
            paddingInsets = RBlockViewport.Companion.ZERO_INSETS
        }
        var borderInsets: Insets? = this.borderInsets
        if (borderInsets == null) {
            borderInsets = RBlockViewport.Companion.ZERO_INSETS
        }
        var marginInsets = this.marginInsets
        if (marginInsets == null) {
            marginInsets = RBlockViewport.Companion.ZERO_INSETS

            // This causes a single trivial regression. Keeping it disabled for now, but worth checking out later.
            // this.marginInsets = marginInsets;
        }
        val paddingTotalWidth = paddingInsets!!.left + paddingInsets.right
        val paddingTotalHeight = paddingInsets.top + paddingInsets.bottom

        var overflowX = this.overflowX
        if (overflowX == RenderState.OVERFLOW_NONE) {
            overflowX = defaultOverflowX
        }
        var overflowY = this.overflowY
        if (overflowY == RenderState.OVERFLOW_NONE) {
            overflowY = defaultOverflowY
        }
        val vauto = overflowY == RenderState.OVERFLOW_AUTO
        var hscroll = overflowX == RenderState.OVERFLOW_SCROLL
        val hauto = overflowX == RenderState.OVERFLOW_AUTO
        var vscroll = overflowY == RenderState.OVERFLOW_SCROLL

        var insets = this.getInsetsMarginBorder(hscroll, vscroll)
        var insetsTotalWidth = insets.left + insets.right
        var insetsTotalHeight = insets.top + insets.bottom
        var tentativeAvailWidth = availWidth - paddingTotalWidth - insetsTotalWidth
        val tentativeAvailHeight = availHeight - paddingTotalHeight - insetsTotalHeight

        val declaredMaxWidth = getDeclaredMaxWidth(renderState, tentativeAvailWidth)
        val declaredMaxHeight = getDeclaredMaxHeight(renderState, tentativeAvailHeight)
        if (declaredMaxWidth != null) {
            tentativeAvailWidth = min(tentativeAvailWidth, declaredMaxWidth)
        }

        /* Has no effect apparently, but worth checking out again, later.
    if (declaredMaxHeight != null) {
      tentativeAvailHeight = Math.min(tentativeAvailHeight, declaredMaxHeight);
    }
    */
        val isHtmlElem = modelNode() is HTMLHtmlElement
        var actualAvailWidth = tentativeAvailWidth

        val actualAvailHeight = tentativeAvailHeight
        val dw = this.getDeclaredWidth(renderState, actualAvailWidth)
        // final Integer dw = isHtmlElem ? (Integer) actualAvailWidth : this.getDeclaredWidth(renderState, actualAvailWidth);
        val dh = this.getDeclaredHeight(renderState, actualAvailHeight)
        // final Integer dh = isHtmlElem ? (Integer) actualAvailHeight : this.getDeclaredHeight(renderState, actualAvailHeight);
        var declaredWidth = if (dw == null) -1 else dw
        var declaredHeight = if (dh == null) -1 else dh

        val declaredMinWidth = getDeclaredMinWidth(renderState, tentativeAvailWidth)
        if ((declaredMinWidth != null) && declaredMinWidth > 0) {
            declaredWidth =
                if (dw == null) declaredMinWidth else max(declaredWidth, declaredMinWidth)
        }

        val declaredMinHeight = getDeclaredMinHeight(renderState, tentativeAvailHeight)
        if ((declaredMinHeight != null) && declaredMinHeight > 0) {
            declaredHeight =
                if (dh == null) declaredMinHeight else max(declaredHeight, declaredMinHeight)
        }


        // Remove all GUI components previously added by descendents
        // The RBlockViewport.layout() method is expected to add all of them
        // back.
        this.clearGUIComponents()

        var tentativeWidth: Int
        var tentativeHeight: Int

        // Step # 1: If there's no declared width and no width
        // expansion has been requested, do a preliminary layout
        // assuming that the scrollable region has width=0 and
        // there's no wrapping.
        tentativeWidth =
            if (declaredWidth == -1) availWidth else declaredWidth + insetsTotalWidth + paddingTotalWidth
        tentativeHeight =
            if (declaredHeight == -1) availHeight else declaredHeight + insetsTotalHeight + paddingTotalHeight

        if ((declaredWidth == -1) && !expandWidth && (availWidth > (insetsTotalWidth + paddingTotalWidth))) {
            val state = RenderThreadState.state
            val prevOverrideNoWrap = state.overrideNoWrap
            if (!prevOverrideNoWrap) {
                state.overrideNoWrap = true
                try {
                    val desiredViewportWidth = paddingTotalWidth
                    val desiredViewportHeight = paddingTotalHeight
                    bodyLayout.layout(
                        desiredViewportWidth,
                        desiredViewportHeight,
                        paddingInsets,
                        -1,
                        null,
                        true
                    )
                    // If we find that the viewport is not as wide as we
                    // presumed, then we'll use that as a new tentative width.
                    if ((bodyLayout.width() + insetsTotalWidth) < tentativeWidth) {
                        tentativeWidth = bodyLayout.width() + insetsTotalWidth
                        tentativeHeight = bodyLayout.height() + insetsTotalHeight
                    }
                } finally {
                    state.overrideNoWrap = false
                }
            }
        }

        // Step # 2: Do a layout with the tentativeWidth (adjusted if Step # 1 was done),
        // but in case overflow-y is "auto", then we check for possible overflow.
        if (declaredMinWidth != null) {
            tentativeWidth = max(tentativeWidth, declaredMinWidth)
        }

        if (declaredMinHeight != null) {
            tentativeHeight = max(tentativeHeight, declaredMinHeight)
        }

        var viewportFloatBounds: FloatingBounds? = null
        var blockFloatBounds: FloatingBounds? = null
        if (blockFloatBoundsSource != null) {
            blockFloatBounds = blockFloatBoundsSource.getChildBlockFloatingBounds(tentativeWidth)
            viewportFloatBounds =
                ShiftedFloatingBounds(blockFloatBounds!!, -insets.left, -insets.right, -insets.top)
        }
        if (declaredMaxWidth != null) {
            tentativeWidth =
                min(tentativeWidth, declaredMaxWidth + insetsTotalWidth + paddingTotalWidth)
        }
        var desiredViewportWidth = tentativeWidth - insetsTotalWidth
        val desiredViewportHeight = tentativeHeight - insets.top - insets.bottom
        val maxY =
            if (vauto) (if (declaredHeight == -1) availHeight else declaredHeight + paddingInsets.top) else -1
        try {
            bodyLayout.layout(
                desiredViewportWidth,
                desiredViewportHeight,
                paddingInsets,
                maxY,
                viewportFloatBounds,
                sizeOnly
            )
        } catch (see: SizeExceededException) {
            // Getting this exception means that we need to add a vertical scrollbar.
            // We need to relayout and adjust insets and widths for scrollbar.
            vscroll = true
            insets = this.getInsetsMarginBorder(hscroll, vscroll)
            insetsTotalWidth = insets.left + insets.right
            actualAvailWidth = availWidth - paddingTotalWidth - insetsTotalWidth
            val dwNew = this.getDeclaredWidth(renderState, actualAvailWidth)
            declaredWidth = if (dwNew == null) -1 else dwNew
            desiredViewportWidth = tentativeWidth - insetsTotalWidth
            if (blockFloatBounds != null) {
                viewportFloatBounds = ShiftedFloatingBounds(
                    blockFloatBounds,
                    -insets.left,
                    -insets.right,
                    -insets.top
                )
            }
            bodyLayout.layout(
                desiredViewportWidth,
                desiredViewportHeight,
                paddingInsets,
                -1,
                viewportFloatBounds,
                sizeOnly
            )
        }

        if (marginInsets !== this.marginInsets) {
            // Can happen because of margin top being absorbed from child
            insets = this.getInsetsMarginBorder(hscroll, vscroll)
            insetsTotalHeight = insets.top + insets.bottom
        }

        val bodyWidth = bodyLayout.width()
        val bodyHeight = bodyLayout.height()

        if ((declaredHeight == -1) && (bodyHeight == 0) && !(collapseTopMargin || collapseBottomMargin)) {
            if ((paddingInsets.top == 0) && (paddingInsets.bottom == 0) && (borderInsets!!.top == 0) && (borderInsets.bottom == 0)) {
                val mi = this.marginInsets
                if (mi != null) {
                    mi.top = max(mi.top, mi.bottom)
                    mi.bottom = 0
                    insets = this.getInsetsMarginBorder(hscroll, vscroll)
                    insetsTotalHeight = insets.top + insets.bottom
                }
            }
        }

        val prelimBlockWidth = bodyWidth + insetsTotalWidth
        var prelimBlockHeight = bodyHeight + insetsTotalHeight
        val adjDeclaredWidth =
            if (declaredWidth == -1) -1 else (declaredWidth + insets.left + insets.right + paddingInsets.left
                    + paddingInsets.right)
        val adjDeclaredHeight =
            if (declaredHeight == -1) -1 else (declaredHeight + insets.top + insets.bottom + paddingInsets.top
                    + paddingInsets.bottom)

        // Adjust insets and other dimensions base on overflow-y=auto.
        if (hauto && (((adjDeclaredWidth != -1) && (prelimBlockWidth > adjDeclaredWidth)) || (prelimBlockWidth > tentativeWidth))) {
            hscroll = true
            insets = this.getInsetsMarginBorder(hscroll, vscroll)
            insetsTotalHeight = insets.top + insets.bottom
            prelimBlockHeight = bodyHeight + insetsTotalHeight
        }

        if ((vauto || vscroll) && ((prelimBlockHeight - insetsTotalHeight) < bodyLayout.visualHeight())) {
            if (isHtmlElem) {
                prelimBlockHeight = bodyLayout.visualHeight() + insetsTotalHeight
            } else {
                vscroll = true
                insets = this.getInsetsMarginBorder(hscroll, vscroll)
                insetsTotalWidth = insets.left + insets.right
            }
        }

        // final boolean visibleX = (overflowX == RenderState.OVERFLOW_VISIBLE) || (overflowX == RenderState.OVERFLOW_NONE);
        // final boolean visibleY = (overflowY == RenderState.OVERFLOW_VISIBLE) || (overflowY == RenderState.OVERFLOW_NONE);
        var resultingWidth: Int
        var resultingHeight: Int
        if (adjDeclaredWidth == -1) {
            resultingWidth =
                if (expandWidth) max(prelimBlockWidth, tentativeWidth) else prelimBlockWidth
            if ((tentativeWidth > 0) && hscroll && (resultingWidth > tentativeWidth)) {
                resultingWidth =
                    max(tentativeWidth, SCROLL_BAR_THICKNESS)
            }
        } else {
            // resultingWidth = visibleX ? Math.max(prelimBlockWidth, adjDeclaredWidth) : adjDeclaredWidth;
            resultingWidth = adjDeclaredWidth
        }
        if (!sizeOnly) {
            // Align horizontally now. This may change canvas height.
            val alignmentXPercent = rs.alignXPercent
            if (alignmentXPercent > 0) {
                // TODO: OPTIMIZATION: alignment should not be done in table cell
                // sizing determination.
                val canvasWidth = max(bodyLayout.width(), resultingWidth - insets.left - insets.right)
                // Alignment is done afterwards because canvas dimensions might have
                // changed.
                bodyLayout.alignX(alignmentXPercent, canvasWidth, paddingInsets)
            }
        }

        if (adjDeclaredHeight == -1) {
            resultingHeight =
                if (expandHeight) max(prelimBlockHeight, tentativeHeight) else prelimBlockHeight
            if (vscroll && (resultingHeight > tentativeHeight)) {
                resultingHeight =
                    max(tentativeHeight, SCROLL_BAR_THICKNESS)
            }
        } else {
            // resultingHeight = visibleY ? Math.max(prelimBlockHeight, adjDeclaredHeight) : adjDeclaredHeight;
            resultingHeight = adjDeclaredHeight
        }
        if (!sizeOnly) {
            // Align vertically now
            val alignmentYPercent = rs.alignYPercent
            if (alignmentYPercent > 0) {
                // TODO: OPTIMIZATION: alignment should not be done in table cell
                // sizing determination.
                val canvasHeight =
                    max(bodyLayout.height(), resultingHeight - insets.top - insets.bottom)
                // Alignment is done afterwards because canvas dimensions might have
                // changed.
                bodyLayout.alignY(alignmentYPercent, canvasHeight, paddingInsets)
            }
        }

        val scrollWidth = if (vscroll) SCROLL_BAR_THICKNESS else 0
        if (declaredWidth >= 0) {
            resultingWidth = min(
                resultingWidth,
                declaredWidth + paddingTotalWidth + insetsTotalWidth - scrollWidth
            )
        }

        if (declaredMaxWidth != null) {
            resultingWidth = min(
                resultingWidth,
                declaredMaxWidth + paddingTotalWidth + insetsTotalWidth - scrollWidth
            )
        }

        val scrollHeight = if (hscroll) SCROLL_BAR_THICKNESS else 0
        if (declaredHeight >= 0) {
            resultingHeight = min(
                resultingHeight,
                declaredHeight + paddingTotalHeight + insetsTotalHeight - scrollHeight
            )
        }

        if (declaredMaxHeight != null) {
            resultingHeight = min(
                resultingHeight,
                declaredMaxHeight + paddingTotalHeight + insetsTotalHeight - scrollHeight
            )
        }

        if (renderState.position == RenderState.POSITION_STATIC || renderState.position == RenderState.POSITION_RELATIVE) {
            val changes =
                this.applyAutoStyles(availWidth - resultingWidth, availHeight - resultingHeight)
            if (changes != null) {
                resultingWidth += changes.width
                resultingHeight += changes.height
            }
        }

        insets = getInsetsMarginBorder(hscroll, vscroll)

        if (vscroll) {
            val sb = this.getVScrollBar()
            this.addComponent(sb)
            // Bounds set by updateWidgetBounds
        } else {
            this.vScrollBar = null
        }
        if (hscroll) {
            val sb = this.getHScrollBar()
            this.addComponent(sb)
            // Bounds set by updateWidgetBounds
        } else {
            this.hScrollBar = null
        }

        if (hscroll || vscroll) {
            // In this case, viewport origin should not be reset.
            // We don't want to cause the document to scroll back
            // up while rendering.
            this.correctViewportOrigin(insets, resultingWidth, resultingHeight)
            // Now reset the scrollbar state. Depends
            // on block width and height.
            this.setWidth(resultingWidth)
            this.setHeight(resultingHeight)
            this.resetScrollBars(rs)
        } else {
            bodyLayout.setX(insets.left)
            bodyLayout.setY(insets.top)
            this.setWidth(resultingWidth)
            this.setHeight(resultingHeight)
        }

        // setupRelativePosition(rs, availWidth);
        return LayoutValue(resultingWidth, resultingHeight, hscroll, vscroll)
    }

    override fun visualWidth(): Int {
        if (hasHScrollBar) {
            return super.visualWidth()
        } else {
            return max(super.visualWidth(), rBlockViewport.visualWidth())
        }
    }

    override fun visualHeight(): Int {
        if (hasVScrollBar) {
            return super.visualHeight()
        } else {
            return max(super.visualHeight(), rBlockViewport.visualHeight())
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMousePressed(java.awt.event
     * .MouseEvent, int, int)
     */
    /*
  public boolean onMousePressed(final MouseEvent event, final int x, final int y) {
    final RBlockViewport bodyLayout = this.bodyLayout;
    if (bodyLayout != null) {
      final int newX = x - bodyLayout.x;
      final int newY = y - bodyLayout.y;
      if (bodyLayout.contains(newX, newY)) {
        this.armedRenderable = bodyLayout;
        if (!bodyLayout.onMousePressed(event, newX, newY)) {
          return false;
        }
      } else {
        this.armedRenderable = null;
      }
    } else {
      this.armedRenderable = null;
    }
    if (!HtmlController.getInstance().onMouseDown(this.modelNode, event, x, y)) {
      return false;
    }
    if (this.backgroundColor != null) {
      return false;
    }
    return true;
  }
  */
    /**
     * Changes scroll bar state to match viewport origin.
     */
    private fun resetScrollBars(renderState: RenderState?) {
        // Expected to be called only in the GUI thread.
        this.resettingScrollBars = true
        try {
            val bodyLayout = this.rBlockViewport
            val insets = this.getInsetsMarginBorder(this.hasHScrollBar, this.hasVScrollBar)
            val vsb = this.vScrollBar
            if (vsb != null) {
                val newValue = insets.top - bodyLayout.y()
                val newExtent = this.height() - insets.top - insets.bottom
                val newMin = 0
                val newMax = bodyLayout.visualHeight()
                vsb.setValues(newValue, newExtent, newMin, newMax)
                vsb.setUnitIncrement(getVUnitIncrement(renderState))
                vsb.setBlockIncrement(newExtent)
            }
            val hsb = this.hScrollBar
            if (hsb != null) {
                val newValue = insets.left - bodyLayout.x()
                val newExtent = this.width() - insets.left - insets.right
                val newMin = 0
                val newMax = bodyLayout.visualWidth()
                hsb.setValues(newValue, newExtent, newMin, newMax)
            }
        } finally {
            this.resettingScrollBars = false
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.UIControl#paintSelection(java.awt.Graphics,
     * boolean, org.xamjwg.html.renderer.RenderablePoint,
     * org.xamjwg.html.renderer.RenderablePoint)
     */
    override fun paintSelection(
        g: Graphics,
        inSelection: Boolean,
        startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean {
        val newG = g.create()
        try {
            val insets = this.getInsetsMarginBorder(this.hasHScrollBar, this.hasVScrollBar)
            // Just clip, don't translate.
            newG.clipRect(
                insets.left,
                insets.top,
                this.width() - insets.left - insets.right,
                this.height() - insets.top - insets.bottom
            )
            return super.paintSelection(newG, inSelection, startPoint, endPoint)
        } finally {
            newG.dispose()
        }
        // boolean endSelectionLater = false;
        // if(inSelection) {
        // if(startPoint.renderable == this || endPoint.renderable == this) {
        // return false;
        // }
        // }
        // else {
        // if(startPoint.renderable == this || endPoint.renderable == this) {
        // // This can only occur if the selection point
        // // is on the margin or border or the block.
        // inSelection = true;
        // if(startPoint.renderable == this && endPoint.renderable == this) {
        // // Start and end selection points on margin or border.
        // endSelectionLater = true;
        // }
        // }
        // }
        // RBlockViewport bodyLayout = this.bodyLayout;
        // if(bodyLayout != null) {
        // Insets insets = this.getInsets(this.hasHScrollBar,
        // this.hasVScrollBar);
        // Graphics newG = g.create(insets.left, insets.top, this.width -
        // insets.left - insets.right, this.height - insets.top -
        // insets.bottom);
        // try {
        // newG.translate(bodyLayout.x - insets.left, bodyLayout.y -
        // insets.top);
        // boolean newInSelection = bodyLayout.paintSelection(newG, inSelection,
        // startPoint, endPoint);
        // if(endSelectionLater) {
        // return false;
        // }
        // return newInSelection;
        // } finally {
        // newG.dispose();
        // }
        // }
        // else {
        // return inSelection;
        // }
    }



    override var originalParent: RCollection?
        get() = TODO("Not yet implemented")
        set(value) {}
    override val originalOrCurrentParent: RCollection?
        get() = TODO("Not yet implemented")
    override val visualX: Int
        get() = TODO("Not yet implemented")
    override val visualY: Int
        get() = TODO("Not yet implemented")


    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.BoundableRenderable#getRenderablePoint(int,
     * int)
     */
    override fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot? {
        val bodyLayout = this.rBlockViewport
        val insets = this.getInsetsMarginBorder(this.hasHScrollBar, this.hasVScrollBar)
        if ((x - relativeOffsetX > insets.left) && (x - relativeOffsetX < (this.width() - insets.right)) && (y - relativeOffsetY > insets.top)
            && (y - relativeOffsetY < (this.height() - insets.bottom))
        ) {
            return bodyLayout.getLowestRenderableSpot(
                x - relativeOffsetX - bodyLayout.x(),
                y - relativeOffsetY - bodyLayout.y()
            )
        } else {
            return RenderableSpot(this, x - relativeOffsetX, y - relativeOffsetY)
        }
    }

    /**
     * RBlocks should only be invalidated if one of their properties change, or if
     * a descendent changes, or if a style property of an ancestor is such that it
     * could produce layout changes in this RBlock.
     */
    override fun invalidateLayoutLocal() {
        // Threads.dumpStack(4);

        super.invalidateLayoutLocal()
        // this.cachedLayout.clear();
        // this.lastLayoutKey = null;
        // this.lastLayoutValue = null;
        val hScrollBar = this.hScrollBar
        if (hScrollBar != null) {
            // Necessary
            hScrollBar.invalidate()
        }
        val vScrollBar = this.vScrollBar
        if (vScrollBar != null) {
            // Necessary
            vScrollBar.invalidate()
        }
    }

    // public boolean extractSelectionText(StringBuffer buffer, boolean
    // inSelection, RenderableSpot startPoint, RenderableSpot endPoint) {
    // RBlockViewport bodyLayout = this.bodyLayout;
    // if(bodyLayout != null) {
    // inSelection = inSelection ? endPoint.renderable != this :
    // startPoint.renderable == this;
    // return bodyLayout.extractSelectionText(buffer, inSelection, startPoint,
    // endPoint);
    // }
    // else {
    // return inSelection;
    // }
    // }
    override fun clearStyle(isRootBlock: Boolean) {
        super.clearStyle(isRootBlock)

        this.overflowX = this.defaultOverflowX
        this.overflowY = this.defaultOverflowY
    }

    override fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val bodyLayout = this.rBlockViewport
        if (!bodyLayout.onDoubleClick(event, x - bodyLayout.x(), y - bodyLayout.y())) {
            return false
        }
        return this.backgroundColor == null
    }

    override val isContainedByNode: Boolean
        get() = TODO("Not yet implemented")

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMouseDisarmed(java.awt.event
     * .MouseEvent)
     */
    override fun onMouseDisarmed(event: MouseEvent?): Boolean {
        val br = this.armedRenderable
        if (br != null) {
            try {
                return br.onMouseDisarmed(event)
            } finally {
                this.armedRenderable = null
            }
        } else {
            return true
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMouseReleased(java.awt.event
     * .MouseEvent, int, int)
     */
    override fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean {
        val bodyLayout = this.rBlockViewport
        val newX = x - bodyLayout.x()
        val newY = y - bodyLayout.y()
        if (bodyLayout.contains(newX, newY)) {
            this.armedRenderable = null
            if (!bodyLayout.onMouseReleased(event, newX, newY)) {
                return false
            }
        } else {
            val br = this.armedRenderable
            if (br != null) {
                br.onMouseDisarmed(event)
            }
        }
        if (!HtmlController.Companion.instance.onMouseUp(this.modelNode()!!, event, x, y)) {
            return false
        }
        return this.backgroundColor == null
    }

    override fun paintedBackgroundColor(): Color? {
        return this.backgroundColor
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.RCollection#getRenderables()
     */
    override fun getRenderables(topFirst: Boolean): MutableIterator<Renderable?> {
        return CollectionUtilities.singletonIterator<Renderable?>(this.rBlockViewport)
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.domimpl.ContainingBlockContext#repaint(org.xamjwg.html.
     * domimpl.RenderableContext)
     */
    override fun repaint(modelNode: ModelNode?) {
        // this.invalidateRenderStyle();
        this.repaint()
    }

    override fun updateWidgetBounds(guiX: Int, guiY: Int) {
        super.updateWidgetBounds(guiX, guiY)
        val hscroll = this.hasHScrollBar
        val vscroll = this.hasVScrollBar
        if (hscroll || vscroll) {
            val insets = this.getInsetsMarginBorder(hscroll, vscroll)
            if (hscroll) {
                val hsb = this.hScrollBar
                if (hsb != null) {
                    hsb.setBounds(
                        guiX + insets.left,
                        (guiY + this.height()) - insets.bottom,
                        this.width() - insets.left - insets.right,
                        SCROLL_BAR_THICKNESS
                    )
                }
            }
            if (vscroll) {
                val vsb = this.vScrollBar
                if (vsb != null) {
                    vsb.setBounds(
                        (guiX + this.width()) - insets.right,
                        guiY + insets.top,
                        SCROLL_BAR_THICKNESS,
                        (this.height() - insets.top
                                - insets.bottom)
                    )
                }
            }
        }
    }



    fun scrollHorizontalTo(newX: Int): Boolean {
        val bodyLayout = this.rBlockViewport
        // if (this.overflowX == RenderState.OVERFLOW_SCROLL || this.overflowX == RenderState.OVERFLOW_AUTO) {
        if (hasHScrollBar) {
            val insets = this.getInsetsMarginBorder(this.hasHScrollBar, this.hasVScrollBar)
            val viewPortX = newX
            val prevX = bodyLayout.x()
            if (viewPortX > insets.left) {
                bodyLayout.setX( insets.left)
            } else if (viewPortX < (this.width() - insets.right - bodyLayout.visualWidth())) {
                bodyLayout.setX(
                    min(insets.left, this.width() - insets.right - bodyLayout.visualWidth()))
            } else {
                bodyLayout.setX(viewPortX)
            }
            val diff = bodyLayout.x() - prevX
            bodyLayout.scrollX += diff
            this.resetScrollBars(null)
            this.updateWidgetBounds()
            this.repaint()

            return diff != 0
        }

        return false
    }

    fun scrollVerticalTo(newY: Int): Boolean {
        val bodyLayout = this.rBlockViewport
        // if (this.overflowY == RenderState.OVERFLOW_SCROLL || this.overflowY == RenderState.OVERFLOW_AUTO) {
        if (hasVScrollBar) {
            val insets = this.getInsetsMarginBorder(this.hasHScrollBar, this.hasVScrollBar)
            val viewPortY = newY
            val prevY = bodyLayout.y()
            if (viewPortY > insets.top) {
                bodyLayout.setY(insets.top)
            } else if (viewPortY < (this.height() - insets.bottom - bodyLayout.visualHeight())) {
                bodyLayout.setY(
                    min(insets.top, this.height() - insets.bottom - bodyLayout.visualHeight()))
            } else {
                bodyLayout.setY(viewPortY)
            }
            val diff = bodyLayout.y() - prevY
            bodyLayout.scrollY += diff
            this.resetScrollBars(null)
            this.updateWidgetBounds()
            this.repaint()
            return diff != 0
        }

        return false
    }

    // public FloatingBounds getExportableFloatingBounds() {
    // RBlockViewport viewport = this.bodyLayout;
    // FloatingBounds viewportBounds = viewport.getExportableFloatingBounds();
    // if (viewportBounds == null) {
    // return null;
    // }
    // Insets insets = this.getInsets(this.hasHScrollBar, this.hasVScrollBar);
    // return new ShiftedFloatingBounds(viewportBounds, insets.left,
    // insets.right, viewport.y);
    // }
    fun scrollByUnits(orientation: Int, units: Int): Boolean {
        val offset =
            if (orientation == Adjustable.VERTICAL) getVUnitIncrement(null) * units else units
        return this.scrollBy(orientation, offset)
    }

    fun scrollBy(orientation: Int, offset: Int): Boolean {
        val bodyLayout = this.rBlockViewport
        when (orientation) {
            Adjustable.HORIZONTAL -> return this.scrollHorizontalTo(bodyLayout.x() - offset)
            Adjustable.VERTICAL -> return this.scrollVerticalTo(bodyLayout.y() - offset)
        }

        return false
    }

    /*
  private static class BodyFilter implements NodeFilter {
    public boolean accept(final Node node) {
      return node instanceof HTMLBodyElement;
    }
  }*/
    /**
     * Scrolls the viewport's origin to the given location, or as close to it as
     * possible.
     *
     *
     * This method should be invoked in the GUI thread.
     *
     * @param bounds    The bounds of the scrollable area that should become visible.
     * @param xIfNeeded If this parameter is `true` the x coordinate is changed
     * only if the horizontal bounds are not currently visible.
     * @param yIfNeeded If this parameter is `true` the y coordinate is changed
     * only if the vertical bounds are not currently visible.
     */
    fun scrollTo(bounds: Rectangle, xIfNeeded: Boolean, yIfNeeded: Boolean) {
        val hscroll = this.hasHScrollBar
        val vscroll = this.hasVScrollBar
        if (hscroll || vscroll) {
            val bv = this.rBlockViewport
            val insets = this.getInsetsMarginBorder(hscroll, vscroll)
            val vpheight = this.height() - insets.top - insets.bottom
            val vpwidth = this.width() - insets.left - insets.right
            val tentativeX = insets.left - bounds.x
            val tentativeY = insets.top - bounds.y
            var needCorrection = false
            if (!(xIfNeeded && (tentativeX <= bv.x()) && ((-tentativeX + bv.x() + bounds.width) <= vpwidth))) {
                bv.setX(tentativeX)
                needCorrection = true
            }
            if (!(yIfNeeded && (tentativeY <= bv.y()) && ((-tentativeY + bv.y() + bounds.height) <= vpheight))) {
                bv.setY(tentativeY)
                needCorrection = true
            }
            if (needCorrection) {
                this.correctViewportOrigin(insets, this.width(), this.height())
                this.resetScrollBars(null)
            }
        }
    }

    private fun scrollToSBValue(orientation: Int, value: Int) {
        val insets = this.getInsetsMarginBorder(this.hasHScrollBar, this.hasVScrollBar)
        when (orientation) {
            Adjustable.HORIZONTAL -> {
                val xOrigin = insets.left - value
                this.scrollHorizontalTo(xOrigin)
            }

            Adjustable.VERTICAL -> {
                val yOrigin = insets.top - value
                this.scrollVerticalTo(yOrigin)
            }
        }
    }

    override fun extractSelectionText(
        buffer: StringBuffer, inSelection: Boolean, startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean {
        val result = super.extractSelectionText(buffer, inSelection, startPoint, endPoint)
        val br = System.getProperty("line.separator")
        if (inSelection) {
            buffer.insert(0, br)
        }
        if (result) {
            buffer.append(br)
        }
        return result
    }

    /*
  private static class LayoutKey {
    public final int availWidth;
    public final int availHeight;
    public final FloatingBoundsSource floatBoundsSource;
    public final int defaultOverflowX;
    public final int defaultOverflowY;
    public final int whitespace;
    public final Font font;
    public final boolean expandWidth;
    public final boolean expandHeight;
    public final boolean useDeclaredSize;
    public final boolean overrideNoWrap;

    public LayoutKey(final int availWidth, final int availHeight, final boolean expandWidth, final boolean expandHeight,
        final FloatingBoundsSource floatBoundsSource,
        final int defaultOverflowX, final int defaultOverflowY, final int whitespace, final Font font, final boolean overrideNoWrap) {
      super();
      this.availWidth = availWidth;
      this.availHeight = availHeight;
      this.floatBoundsSource = floatBoundsSource;
      this.defaultOverflowX = defaultOverflowX;
      this.defaultOverflowY = defaultOverflowY;
      this.whitespace = whitespace;
      this.font = font;
      this.expandWidth = expandWidth;
      this.expandHeight = expandHeight;
      this.useDeclaredSize = true;
      this.overrideNoWrap = overrideNoWrap;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof LayoutKey)) {
        return false;
      }
      final LayoutKey other = (LayoutKey) obj;
      return (other.availWidth == this.availWidth) && (other.availHeight == this.availHeight)
          && (other.defaultOverflowX == this.defaultOverflowX) && (other.defaultOverflowY == this.defaultOverflowY)
          && (other.whitespace == this.whitespace) && (other.expandWidth == this.expandWidth) && (other.expandHeight == this.expandHeight)
          && (other.useDeclaredSize == this.useDeclaredSize) && (other.overrideNoWrap == this.overrideNoWrap)
          && java.util.Objects.equals(other.font, this.font) && java.util.Objects.equals(other.floatBoundsSource, this.floatBoundsSource);
    }

    @Override
    public int hashCode() {
      final Font font = this.font;
      return ((this.availWidth * 1000) + this.availHeight) ^ (font == null ? 0 : font.hashCode()) ^ (this.expandWidth ? 2 : 0)
          ^ (this.expandHeight ? 1 : 0) ^ (this.whitespace << 2);
    }
  } */
    override fun toString(): String {
        return "RBlock[node=" + this.modelNode() + "]"
    }

    val exportableFloatingInfo: FloatingInfo?
        get() {
            val info = this.rBlockViewport.exportableFloatingInfo
            if (info == null) {
                return null
            }
            val insets =
                this.getInsetsMarginBorder(this.hasHScrollBar, this.hasVScrollBar)
            return FloatingInfo(info.shiftX + insets.left, info.shiftY + insets.top, info.floats)
        }

    override fun setInnerWidth(newWidth: Int) {
        val insets = getInsets(hasHScrollBar, hasVScrollBar)
        val hInset = insets.left + insets.right
        rBlockViewport.setWidth(newWidth)
        setWidth(newWidth + hInset)
    }

    override fun setInnerHeight(newHeight: Int) {
        val insets = getInsets(hasHScrollBar, hasVScrollBar)
        val vInset = insets.top + insets.bottom
        rBlockViewport.setHeight(newHeight)
        setHeight(newHeight + vInset)
    }


    fun setCollapseTop(set: Boolean) {
        collapseTopMargin = set
    }

    fun setCollapseBottom(set: Boolean) {
        collapseBottomMargin = set
    }

    override fun applyStyle(availWidth: Int, availHeight: Int, updateLayout: Boolean) {
        super.applyStyle(availWidth, availHeight, updateLayout)

        if (collapseTopMargin) {
            val mi = this.marginInsets!!
            this.marginTopOriginal = mi.top
            this.marginInsets = Insets(0, mi.left, mi.bottom, mi.right)
        }
        if (collapseBottomMargin) {
            val mi = this.marginInsets!!
            this.marginBottomOriginal = mi.bottom
            this.marginInsets = Insets(mi.top, mi.left, 0, mi.right)
        }
    }

    fun absorbMarginTopChild(marginTopChild: Int?) {
        if (marginTopChild != null) {
            // System.out.println("In: " + this);
            // System.out.println("  Absorbing: " + marginTopChild);
            val mi = this.marginInsets
            if (mi != null) {
                if (marginTopChild > mi.top) {
                    if (!collapseTopMargin) {
                        this.marginInsets = Insets(marginTopChild, mi.left, mi.bottom, mi.right)
                    }
                    this.marginTopOriginal = marginTopChild
                }
            } else {
                if (!collapseTopMargin) {
                    this.marginInsets = Insets(marginTopChild, 0, 0, 0)
                }
                this.marginTopOriginal = marginTopChild
            }
        }
    }

    fun absorbMarginBottomChild(marginBottomChild: Int?) {
        if (marginBottomChild != null) {
            val mi = this.marginInsets
            if (mi != null) {
                if (marginBottomChild > mi.bottom) {
                    if (!collapseBottomMargin) {
                        this.marginInsets = Insets(mi.top, mi.left, marginBottomChild, mi.right)
                    }
                    this.marginBottomOriginal = marginBottomChild
                }
            } else {
                if (!collapseBottomMargin) {
                    this.marginInsets = Insets(0, 0, marginBottomChild, 0)
                }
                this.marginBottomOriginal = marginBottomChild
            }
        }
    }

    fun getHorizontalScrollBarHeight(): Int {
        return if (hasHScrollBar) SCROLL_BAR_THICKNESS else 0
    }

    fun getVerticalScrollBarHeight(): Int {
        return if (hasVScrollBar) SCROLL_BAR_THICKNESS else 0
    }




    override fun vAlign(): CSSProperty.VerticalAlign? {
        TODO("Not yet implemented")
    }

    private class LayoutValue(
        val width: Int,
        val height: Int,
        val hasHScrollBar: Boolean,
        val hasVScrollBar: Boolean
    )

    private inner class LocalAdjustmentListener(private val orientation: Int) : AdjustmentListener {
        override fun adjustmentValueChanged(e: AdjustmentEvent) {
            if (this@RBlock.resettingScrollBars) {
                return
            }
            when (e.getAdjustmentType()) {
                AdjustmentEvent.UNIT_INCREMENT, AdjustmentEvent.UNIT_DECREMENT, AdjustmentEvent.BLOCK_INCREMENT, AdjustmentEvent.BLOCK_DECREMENT, AdjustmentEvent.TRACK -> {
                    val value = e.getValue()
                    this@RBlock.scrollToSBValue(this.orientation, value)
                }
            }
        }
    }

    companion object {
        private fun getVUnitIncrement(renderState: RenderState?): Int {
            if (renderState != null) {
                return renderState.fontMetrics!!.height
            } else {
                return BlockRenderState(null).getFontMetrics().height
            }
        }

        private fun isSimpleLine(r: Renderable?): Boolean {
            if (r is RLine) {
                val rends = r.renderabl
                rends.forEach { rend ->
                    if (!(rend is RWord || rend is RBlank || rend is RStyleChanger)) {
                        return false
                    }
                }

                return true
            }
            return false
        }

        fun dumpRndTree(indentStr: String?, isLast: Boolean, r: Renderable, condense: Boolean) {
            val nextIndentStr =
                indentStr + (if (r is RBlockViewport) "  " else (if (isLast) "  " else "│ "))
            val selfIndentStr = (if (isLast) "└ " else "├ ")
            if (isSimpleLine(r)) {
                println(indentStr + selfIndentStr + "─────")
            } else {
                if (r is RBlockViewport) {
                    // System.out.println(indentStr + "^RBV");
                } else {
                    val selfStr: String = makeSelfStr(r)
                    println(indentStr + selfIndentStr + selfStr)
                }
                if (r is RBlock) {
                    if ((!condense) || !r.isDelegated()) {
                        dumpRndTree(
                            nextIndentStr,
                            true,
                            r.rBlockViewport,
                            r.isDelegated() || condense
                        )
                    }
                } else {
                    if (r is RCollection) {
                        if ((!condense) || !r.isDelegated()) {
                            val rnds = r.renderables
                            if (rnds == null) {
                                println(indentStr + selfIndentStr + " [empty]")
                            } else {
                                val filteredRnds = CollectionUtilities.filter(
                                    rnds,
                                    { fr: Renderable -> !isSimpleLine(fr) } as CollectionUtilities.FilterFunction<Renderable?>)
                                while (filteredRnds.hasNext()) {
                                    val rnd = filteredRnds.next()
                                    dumpRndTree(
                                        nextIndentStr,
                                        !filteredRnds.hasNext(),
                                        rnd!!,
                                        condense
                                    )
                                }
                            }
                        }
                    } else if (r is PositionedRenderable) {
                        dumpRndTree(nextIndentStr, true, r.renderable, false)
                    }
                }
            }
        }

        private fun makeSelfStr(r: Renderable): String {
            if (r is PositionedRenderable) {
                return "Pos-Rend: " + (if (r.isFloat) " <float> " else "") + (if (r.isFixed()) " <fixed> " else "")
            } else if (r is TranslatedRenderable) {
                return "Trans-Rend"
            } else {
                val delgStr =
                    if (r is RCollection) (if (r.isDelegated()) "<deleg> " else "") else ""
                return delgStr + r.toString()
            }
        }
    }
}