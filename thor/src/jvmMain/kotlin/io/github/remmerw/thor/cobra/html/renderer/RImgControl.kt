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

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import java.awt.Color
import java.awt.Dimension
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle

class RImgControl(
    me: ModelNode?,
    widget: ImgControl,
    container: RenderableContainer?,
    frameContext: FrameContext,
    ucontext: UserAgentContext?
) : RUIControl(me, widget, container, frameContext, ucontext) {
    // TODO: This is a hack. RUIControl excludes border insets from the UI control. Images need to exclude padding as well.
    // Hence, we are returning getInsets() from getBorderInsets().
    // A better way would be to create two methods: one for excluded space and one for included space and implement as per convenience.
    // Yet another idea: check if RImgControl really needs to sub-class RUIControl or it can directly sub-class BaseElementRenderable.
    override fun getBorderInsets(): Insets {
        return getInsets(false, false)
    }

    override fun doLayout(availWidth: Int, availHeight: Int, sizeOnly: Boolean) {
        super.doLayout(availWidth, availHeight, sizeOnly)
        updateWidthHeight()
    }

    private fun updateWidthHeight() {
        val widthConstrained = isWidthConstrained
        val heightConstrained = isHeightConstrained
        if (!widthConstrained && heightConstrained) {
            val prefSize = widget.preferredSize()!!
            if (prefSize.height != 0) {
                this.width = (prefSize.width * innerMostHeight) / prefSize.height
            }
        } else if (!heightConstrained && widthConstrained) {
            val prefSize = widget.preferredSize()!!
            if (prefSize.width != 0) {
                this.height = (prefSize.height * innerMostWidth) / prefSize.width
            }
        }
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
    override val isContainedByNode: Boolean
        get() = TODO("Not yet implemented")

    override fun setInnerWidth(newWidth: Int) {
        super.setInnerWidth(newWidth)
        updateWidthHeight()
    }

    override fun setInnerHeight(newHeight: Int) {
        super.setInnerHeight(newHeight)
        updateWidthHeight()
    }


    override fun toString(): String {
        return "RImgControl : " + modelNode
    }


}
