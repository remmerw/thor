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
import io.github.remmerw.thor.cobra.html.dom.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.dom.NodeImpl
import io.github.remmerw.thor.cobra.html.style.ListStyle
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext

internal class RList(
    modelNode: NodeImpl?,
    listNesting: Int,
    pcontext: UserAgentContext?,
    rcontext: HtmlRendererContext?,
    frameContext: FrameContext?,
    parentContainer: RenderableContainer?,
    parent: RCollection?
) : BaseRListElement(modelNode, listNesting, pcontext, rcontext, frameContext, parentContainer) {
    override fun applyStyle(availWidth: Int, availHeight: Int, updateLayout: Boolean) {
        super.applyStyle(availWidth, availHeight, updateLayout)
        var listStyle = this.listStyle
        if ((listStyle == null) || (listStyle.type == ListStyle.TYPE_UNSET)) {
            val rootNode: Any? = this.modelNode()
            if (rootNode !is HTMLElementImpl) {
                return
            }
            if (listStyle == null) {
                listStyle = ListStyle()
                this.listStyle = listStyle
            }
            if ("ul".equals(rootNode.tagName, ignoreCase = true)) {
                val listNesting = this.listNesting
                if (listNesting == 0) {
                    listStyle.type = ListStyle.TYPE_DISC
                } else if (listNesting == 1) {
                    listStyle.type = ListStyle.TYPE_CIRCLE
                } else {
                    listStyle.type = ListStyle.TYPE_SQUARE
                }
            } else {
                listStyle.type = ListStyle.TYPE_DECIMAL
            }
        }
    }

    override fun doLayout(
        availWidth: Int, availHeight: Int, expandWidth: Boolean, expandHeight: Boolean,
        floatBoundsSource: FloatingBoundsSource?,
        defaultOverflowX: Int, defaultOverflowY: Int, sizeOnly: Boolean
    ) {
        val renderState: RenderState = this.modelNode()!!.renderState()!!
        var counterStart = 1
        val rootNode: Any = this.modelNode()!!
        if (rootNode !is HTMLElementImpl) {
            return
        }
        val startText = rootNode.getAttribute("start")
        if (startText != null) {
            try {
                counterStart = startText.toInt()
            } catch (nfe: NumberFormatException) {
                // ignore
            }
        }
        renderState.resetCount(
            DEFAULT_COUNTER_NAME,
            this.listNesting,
            counterStart
        )
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
    }
}