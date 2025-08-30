/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The XAMJ Project

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

import io.github.remmerw.thor.cobra.html.style.HtmlValues
import io.github.remmerw.thor.cobra.html.style.RenderState

class DelayedPair(
    private val immediateContainingBlock: RenderableContainer,
    val containingBlock: RenderableContainer,
    val child: BoundableRenderable,
    private val left: String?,
    private val right: String?,
    private val top: String?,
    private val bottom: String?,
    private val width: String?,
    private val height: String?,
    private val rs: RenderState?,
    private val initX: Int,
    private val initY: Int,
    position: Int
) {
    val isFixed: Boolean
    val isRelative: Boolean

    @get:Synchronized
    var isAdded: Boolean = false
        private set

    init {
        this.isFixed = position == RenderState.POSITION_FIXED
        this.isRelative = position == RenderState.POSITION_RELATIVE
    }

    private fun getLeft(): Int? {
        return helperGetPixelSize(left, rs, 0, containingBlock.innerWidth)
    }

    private fun getWidth(): Int? {
        return helperGetPixelSize(width, rs, 0, containingBlock.innerWidth)
    }

    private fun getHeight(): Int? {
        return helperGetPixelSize(height, rs, 0, containingBlock.innerHeight)
    }

    private fun getRight(): Int? {
        return helperGetPixelSize(right, rs, 0, containingBlock.innerWidth)
    }

    private fun getTop(): Int? {
        return helperGetPixelSize(top, rs, 0, containingBlock.innerHeight)
    }

    private fun getBottom(): Int? {
        return helperGetPixelSize(bottom, rs, 0, containingBlock.innerHeight)
    }

    fun positionPairChild(): BoundableRenderable {
        val parent = this.containingBlock
        if (isRelative) {
            val rChild = this.child as RElement
            rChild.setupRelativePosition(this.immediateContainingBlock)
            val tr = TranslatedRenderable(rChild)
            // tr.setX(rChild.getX() + tp.x);
            // tr.setY(rChild.getY() + tp.y);
            rChild.setDelegator(tr)
            return tr
        }

        val child = this.child

        /*
    System.out.println("DP: " + this);
    System.out.println("  child block           : " + child);
    System.out.println("  containing block: " + this.containingBlock);
    System.out.println("  imm cntng  block: " + this.immediateContainingBlock);
    */

        // final java.awt.Point tp = parent.translateDescendentPoint((BoundableRenderable)(immediateContainingBlock), initX, initY);
        // final java.awt.Point tp = immediateContainingBlock.getOriginRelativeTo(((RBlock)parent).bodyLayout);
        val tp = immediateContainingBlock.getOriginRelativeToAbs(parent as RCollection?)
        tp?.translate(initX, initY)

        if (this.immediateContainingBlock !== parent) {
            val immediateInsets = this.immediateContainingBlock.getInsetsMarginBorder(false, false)
            tp?.translate(immediateInsets!!.left, immediateInsets!!.top)
        }

        var x = this.getLeft()
        var y = this.getTop()

        val width = getWidth()
        val height = getHeight()
        val right = this.getRight()
        val bottom = this.getBottom()
        val childVerticalScrollBarHeight = child.verticalScrollBarHeight
        if (right != null) {
            if (x != null) {
                // width = parent.getInnerWidth() - (x + right);
                child.setInnerWidth(parent.innerWidth - (x + right) - childVerticalScrollBarHeight)
            } else {
                if (width != null) {
                    child.setInnerWidth(width - childVerticalScrollBarHeight)
                }
                val childWidth = child.width
                x = parent.innerWidth - (childWidth + right - childVerticalScrollBarHeight)
            }
        } else {
            if (width != null) {
                child.setInnerWidth(width - childVerticalScrollBarHeight)
            }
        }

        val childHorizontalScrollBarHeight = child.horizontalScrollBarHeight
        if (bottom != null) {
            if (y != null) {
                // height = parent.getInnerHeight() - (y + bottom);
                child.setInnerHeight(parent.innerHeight - (y + bottom) - childHorizontalScrollBarHeight)
            } else {
                if (height != null) {
                    child.setInnerHeight(height - childHorizontalScrollBarHeight)
                }
                // final int childHeight = height == null? child.getHeight() : height;
                val childHeight = child.height
                y =
                    parent.innerHeight - (childHeight + bottom - childHorizontalScrollBarHeight)
            }
        } else {
            if (height != null) {
                child.setInnerHeight(height - childHorizontalScrollBarHeight)
            }
        }

        child.setX ((if (x == null) tp!!.x else x))
        child.setY ((if (y == null) tp!!.y else y))

        return child
    }

    override fun toString(): String {
        return "DP " + child + " containing block: " + containingBlock
    }

    @Synchronized
    fun markAdded() {
        isAdded = true
    }

    companion object {
        private fun helperGetPixelSize(
            spec: String?,
            rs: RenderState?,
            errorValue: Int,
            avail: Int
        ): Int? {
            if (spec != null) {
                return if ("auto" == spec) null else HtmlValues.getPixelSize(
                    spec,
                    rs,
                    errorValue,
                    avail
                )
            } else {
                return null
            }
        }
    }
}
