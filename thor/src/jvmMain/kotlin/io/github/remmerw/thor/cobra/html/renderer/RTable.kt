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
package io.github.remmerw.thor.cobra.html.renderer

import cz.vutbr.web.css.CSSProperty
import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.html.style.RenderThreadState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.util.Collections
import java.util.LinkedList
import java.util.SortedSet
import java.util.TreeSet

internal class RTable(
    modelNode: HTMLElementImpl,
    pcontext: UserAgentContext?,
    rcontext: HtmlRendererContext?,
    frameContext: FrameContext?,
    container: RenderableContainer?
) : BaseBlockyRenderable(container, modelNode, pcontext) {
    private val cachedLayout: MutableMap<LayoutKey?, LayoutValue?> =
        HashMap<LayoutKey?, LayoutValue?>(5)
    private val tableMatrix: TableMatrix
    private var positionedRenderables: SortedSet<PositionedRenderable>? = null
    private var otherOrdinal = 0
    private var lastLayoutKey: LayoutKey? = null
    private var lastLayoutValue: LayoutValue? = null

    init {
        this.tableMatrix = TableMatrix(modelNode, pcontext, rcontext, frameContext, this, this)
    }

    override fun paintShifted(g: Graphics) {
        val rs = this.modelNode?.renderState()
        if ((rs != null) && (rs.visibility != RenderState.VISIBILITY_VISIBLE)) {
            // Just don't paint it.
            return
        }

        this.prePaint(g)
        val size = this.size()
        // TODO: No scrollbars
        val tm = this.tableMatrix
        tm.paint(g, size)
        val prs: MutableCollection<PositionedRenderable>? = this.positionedRenderables
        if (prs != null) {
            val i = prs.iterator()
            while (i.hasNext()) {
                val pr = i.next()
                pr.paint(g)
                /*
        final BoundableRenderable r = pr.renderable;
        r.paintTranslated(g);
        */
            }
        }
    }

    override fun doLayout(availWidth: Int, availHeight: Int, sizeOnly: Boolean) {
        val cachedLayout = this.cachedLayout
        val rs = this.modelNode!!.renderState()
        val whitespace = if (rs == null) RenderState.WS_NORMAL else rs.whiteSpace
        val font = if (rs == null) null else rs.font
        // Having whiteSpace == NOWRAP and having a NOWRAP override
        // are not exactly the same thing.
        val overrideNoWrap = RenderThreadState.state.overrideNoWrap
        val layoutKey = LayoutKey(availWidth, availHeight, whitespace, font, overrideNoWrap)
        var layoutValue: LayoutValue?
        if (sizeOnly) {
            layoutValue = cachedLayout.get(layoutKey)
        } else {
            if (layoutKey == this.lastLayoutKey) {
                layoutValue = this.lastLayoutValue
            } else {
                layoutValue = null
            }
        }
        if (layoutValue == null) {
            val prs: MutableCollection<PositionedRenderable>? = this.positionedRenderables
            if (prs != null) {
                prs.clear()
            }
            this.otherOrdinal = 0
            this.clearGUIComponents()
            this.clearDelayedPairs()
            this.applyStyle(availWidth, availHeight)
            val tm = this.tableMatrix
            val insets = this.getInsets(false, false)
            tm.reset(insets, availWidth, availHeight)
            // TODO: No scrollbars
            tm.build(availWidth, availHeight, sizeOnly)
            tm.doLayout(insets)

            // Import applicable delayed pairs.
            // Only needs to be done if layout was forced. Otherwise, they should've been imported already.
            val pairs = this.delayedPairs
            if (pairs != null) {
                val i: MutableIterator<DelayedPair?> = pairs.iterator()
                while (i.hasNext()) {
                    val pair = i.next()!!
                    if (pair.containingBlock === this) {
                        this.importDelayedPair(pair)
                    }
                }
            }
            layoutValue = LayoutValue(tm.tableWidth, tm.tableHeight)
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
        this.sendGUIComponentsToParent()
        this.sendDelayedPairsToParent()
    }

    override fun invalidateLayoutLocal() {
        super.invalidateLayoutLocal()
        this.cachedLayout.clear()
        this.lastLayoutKey = null
        this.lastLayoutValue = null
    }



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

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.BoundableRenderable#getRenderablePoint(int,
     * int)
     */
    override fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot {
        val prs: MutableCollection<PositionedRenderable>? = this.positionedRenderables
        if (prs != null) {
            val i = prs.iterator()
            while (i.hasNext()) {
                val pr = i.next()
                val r = pr.renderable
                val childX = x - r.visualX
                val childY = y - r.visualY
                val rs = r.getLowestRenderableSpot(childX, childY)
                if (rs != null) {
                    return rs
                }
            }
        }
        val rs = this.tableMatrix.getLowestRenderableSpot(x, y)
        if (rs != null) {
            return rs
        }
        return RenderableSpot(this, x, y)
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMouseClick(java.awt.event
     * .MouseEvent, int, int)
     */
    override fun onMouseClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val prs: MutableCollection<PositionedRenderable>? = this.positionedRenderables
        if (prs != null) {
            val i = prs.iterator()
            while (i.hasNext()) {
                val pr = i.next()
                val r = pr.renderable
                val bounds = r.visualBounds()!!
                if (bounds.contains(x, y)) {
                    val childX = x - r.visualX
                    val childY = y - r.visualY
                    if (!r.onMouseClick(event, childX, childY)) {
                        return false
                    }
                }
            }
        }
        return this.tableMatrix.onMouseClick(event, x, y)
    }

    override fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val prs: MutableCollection<PositionedRenderable>? = this.positionedRenderables
        if (prs != null) {
            val i = prs.iterator()
            while (i.hasNext()) {
                val pr = i.next()
                val r = pr.renderable
                val bounds = r.visualBounds()!!
                if (bounds.contains(x, y)) {
                    val childX = x - r.visualX
                    val childY = y - r.visualY
                    if (!r.onDoubleClick(event, childX, childY)) {
                        return false
                    }
                }
            }
        }
        return this.tableMatrix.onDoubleClick(event, x, y)
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
        return this.tableMatrix.onMouseDisarmed(event)
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMousePressed(java.awt.event
     * .MouseEvent, int, int)
     */
    override fun onMousePressed(event: MouseEvent?, x: Int, y: Int): Boolean {
        val prs: MutableCollection<PositionedRenderable>? = this.positionedRenderables
        if (prs != null) {
            val i = prs.iterator()
            while (i.hasNext()) {
                val pr = i.next()
                val r = pr.renderable
                val bounds = r.visualBounds()!!
                if (bounds.contains(x, y)) {
                    val childX = x - r.visualX
                    val childY = y - r.visualY
                    if (!r.onMousePressed(event, childX, childY)) {
                        return false
                    }
                }
            }
        }
        return this.tableMatrix.onMousePressed(event, x, y)
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.BoundableRenderable#onMouseReleased(java.awt.event
     * .MouseEvent, int, int)
     */
    override fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean {
        val prs: MutableCollection<PositionedRenderable>? = this.positionedRenderables
        if (prs != null) {
            val i = prs.iterator()
            while (i.hasNext()) {
                val pr = i.next()
                val r = pr.renderable
                val bounds = r.visualBounds()!!
                if (bounds.contains(x, y)) {
                    val childX = x - r.visualX
                    val childY = y - r.visualY
                    if (!r.onMouseReleased(event, childX, childY)) {
                        return false
                    }
                }
            }
        }
        return this.tableMatrix.onMouseReleased(event, x, y)
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.RCollection#getRenderables()
     */
    override fun getRenderables(topFirst: Boolean): MutableIterator<out Renderable> {
        val prs: MutableCollection<PositionedRenderable>? = this.positionedRenderables
        if (prs != null) {
            val c: MutableList<Renderable> = LinkedList<Renderable>()
            val i = prs.iterator()
            while (i.hasNext()) {
                val pr = i.next()
                val r = pr.renderable
                c.add(r)
            }
            val i2 = this.tableMatrix.cells
            while (i2.hasNext()) {
                c.add(i2.next())
            }

            val i3 = this.tableMatrix.rowGroups
            while (i3.hasNext()) {
                c.add(i3.next())
            }

            if (topFirst) {
                Collections.reverse(c)
            }

            return c.iterator()
        } else {
            val rs: MutableList<Renderable> = mutableListOf()
            tableMatrix.cells.forEach { i ->
                rs.add(i)

            }
            tableMatrix.rowGroups.forEach { i ->
                rs.add(i)
            }


            return rs.iterator()
        }
    }



    override fun repaint(modelNode: ModelNode?) {
        // NOP
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.RenderableContainer#getBackground()
     */
    override fun paintedBackgroundColor(): Color? {
        return this.container!!.paintedBackgroundColor()
    }

    private fun addPositionedRenderable(
        renderable: BoundableRenderable,
        verticalAlignable: Boolean,
        isFloat: Boolean,
        isFixed: Boolean
    ) {
        // Expected to be called only in GUI thread.
        var others = this.positionedRenderables
        if (others == null) {
            others = TreeSet<PositionedRenderable>(ZIndexComparator())
            this.positionedRenderables = others
        }
        others.add(
            PositionedRenderable(
                renderable,
                verticalAlignable,
                this.otherOrdinal++,
                isFloat,
                isFixed,
                false
            )
        )
        renderable.parent = (this)
        if (renderable is RUIControl) {
            this.container!!.addComponent(renderable.widget.component!!)
        }
    }

    private fun importDelayedPair(pair: DelayedPair) {
        val r = pair.positionPairChild()
        // final BoundableRenderable r = pair.child;
        this.addPositionedRenderable(r, false, false, pair.isFixed)
    }

    override fun toString(): String {
        return "RTable[this=" + System.identityHashCode(this) + ",node=" + this.modelNode + "]"
    }

    override fun layout(
        availWidth: Int,
        availHeight: Int,
        b: Boolean,
        c: Boolean,
        source: FloatingBoundsSource?,
        sizeOnly: Boolean
    ) {
        this.doLayout(availWidth, availHeight, sizeOnly)
    }



    override val parentContainer: RenderableContainer?
        get() = TODO("Not yet implemented")


    override fun vAlign(): CSSProperty.VerticalAlign? {
        TODO("Not yet implemented")
    }

    private class LayoutKey(
        availWidth: Int,
        availHeight: Int,
        whitespace: Int,
        font: Font?,
        overrideNoWrap: Boolean
    ) {
        val availWidth: Int
        val availHeight: Int
        val whitespace: Int
        val font: Font?
        val overrideNoWrap: Boolean

        init {
            this.availWidth = availWidth
            this.availHeight = availHeight
            this.whitespace = whitespace
            this.font = font
            this.overrideNoWrap = overrideNoWrap
        }

        override fun equals(obj: Any?): Boolean {
            if (obj === this) {
                return true
            }
            if (obj !is LayoutKey) {
                return false
            }
            return (obj.availWidth == this.availWidth) && (obj.availHeight == this.availHeight) && (obj.whitespace == this.whitespace)
                    && (obj.overrideNoWrap == this.overrideNoWrap) && obj.font == this.font
        }

        override fun hashCode(): Int {
            val font = this.font
            return ((this.availWidth * 1000) + this.availHeight) xor (if (font == null) 0 else font.hashCode()) xor this.whitespace
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
