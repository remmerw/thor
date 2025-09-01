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

import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.style.ListStyle
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import org.w3c.dom.html.HTMLElement
import java.awt.Graphics


internal class RListItem(
    modelNode: NodeImpl?,
    listNesting: Int,
    pcontext: UserAgentContext?,
    rcontext: HtmlRendererContext?,
    frameContext: FrameContext?,
    parentContainer: RenderableContainer?,
    parent: RCollection?
) : BaseRListElement(modelNode, listNesting, pcontext, rcontext, frameContext, parentContainer) {
    private var value: Int? = null
    private var count = 0

    override fun getViewportListNesting(blockNesting: Int): Int {
        return blockNesting + 1
    }

    override fun invalidateLayoutLocal() {
        super.invalidateLayoutLocal()
        this.value = null
    }

    private fun getValue(): Int {
        var value = this.value
        if (value == null) {
            val node = this.modelNode() as HTMLElement?
            val valueText = if (node == null) null else node.getAttribute("value")
            if (valueText == null) {
                value = UNSET
            } else {
                try {
                    value = valueText.toInt()
                } catch (nfe: NumberFormatException) {
                    value = UNSET
                }
            }
            this.value = value
        }
        return value
    }

    override fun doLayout(
        availWidth: Int, availHeight: Int, expandWidth: Boolean, expandHeight: Boolean,
        floatBoundsSource: FloatingBoundsSource?,
        defaultOverflowX: Int, defaultOverflowY: Int, sizeOnly: Boolean
    ) {
        super.doLayout(
            availWidth,
            availHeight,
            expandWidth,
            expandHeight,
            floatBoundsSource,
            defaultOverflowX,
            defaultOverflowY,
            sizeOnly
        )
        // Note: Count must be calculated even if layout is valid.
        val renderState: RenderState = this.modelNode()!!.renderState()!!
        val value = this.getValue()
        if (value === UNSET) {
            this.count = renderState.incrementCount(
                DEFAULT_COUNTER_NAME,
                this.listNesting
            )
        } else {
            val newCount = value
            this.count = newCount
            renderState.resetCount(
                DEFAULT_COUNTER_NAME,
                this.listNesting,
                newCount + 1
            )
        }
    }

    override fun paintShifted(g: Graphics) {
        super.paintShifted(g)
        val rs: RenderState = this.modelNode()!!.renderState()!!
        val marginInsets = this.marginInsets
        // TODO val layout = this.bodyLayout
        val listStyle = this.listStyle
        var bulletType = if (listStyle == null) ListStyle.TYPE_UNSET else listStyle.type
        if (bulletType != ListStyle.TYPE_NONE) {
            if (bulletType == ListStyle.TYPE_UNSET) {
                var parent = this.originalOrCurrentParent()
                if (parent !is RList) {
                    parent = parent?.originalOrCurrentParent()
                }
                if (parent is RList) {
                    val parentListStyle = parent.listStyle
                    bulletType =
                        if (parentListStyle == null) ListStyle.TYPE_DISC else parentListStyle.type
                } else {
                    bulletType = ListStyle.TYPE_DISC
                }
            }
            // Paint bullets
            val prevColor = g.color
            g.color = rs.getColor()
            try {
                val insets = this.getInsets(this.hasHScrollBar(), this.hasVScrollBar())
                val paddingInsets = this.paddingInsets
                val baselineOffset = 0 // TODO layout.getFirstBaselineOffset()
                val bulletRight: Int =
                    (if (marginInsets == null) 0 else marginInsets.left) - BULLET_RMARGIN
                val bulletBottom =
                    insets.top + baselineOffset + (if (paddingInsets == null) 0 else paddingInsets.top)
                val bulletTop: Int = bulletBottom - BULLET_HEIGHT
                val bulletLeft: Int = bulletRight - BULLET_WIDTH
                val bulletNumber = this.count
                var numberText: String? = null
                when (bulletType) {
                    ListStyle.TYPE_DECIMAL -> numberText = bulletNumber.toString() + "."
                    ListStyle.TYPE_LOWER_ALPHA -> numberText =
                        (('a'.code + bulletNumber).toChar()).toString() + "."

                    ListStyle.TYPE_UPPER_ALPHA -> numberText =
                        (('A'.code + bulletNumber).toChar()).toString() + "."

                    ListStyle.TYPE_DISC -> g.fillOval(
                        bulletLeft,
                        bulletTop,
                        BULLET_WIDTH,
                        BULLET_HEIGHT
                    )

                    ListStyle.TYPE_CIRCLE -> g.drawOval(
                        bulletLeft,
                        bulletTop,
                        BULLET_WIDTH,
                        BULLET_HEIGHT
                    )

                    ListStyle.TYPE_SQUARE -> g.fillRect(
                        bulletLeft,
                        bulletTop,
                        BULLET_WIDTH,
                        BULLET_HEIGHT
                    )
                }
                if (numberText != null) {
                    val fm = g.fontMetrics
                    val numberLeft = bulletRight - fm.stringWidth(numberText)
                    val numberY = bulletBottom
                    g.drawString(numberText, numberLeft, numberY)
                }
            } finally {
                g.color = prevColor
            }
        }
    }



    companion object {
        private const val BULLET_WIDTH = 5
        private const val BULLET_HEIGHT = 5
        private const val BULLET_RMARGIN = 5
        private const val BULLET_SPACE_WIDTH = 36
        private val UNSET = Int.Companion.MIN_VALUE
    }
}
