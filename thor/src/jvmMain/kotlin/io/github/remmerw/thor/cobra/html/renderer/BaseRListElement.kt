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
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.style.HtmlValues
import io.github.remmerw.thor.cobra.html.style.ListStyle
import io.github.remmerw.thor.cobra.ua.UserAgentContext

internal abstract class BaseRListElement(
    modelNode: NodeImpl?, listNesting: Int, pcontext: UserAgentContext?,
    rcontext: HtmlRendererContext?,
    frameContext: FrameContext?, parentContainer: RenderableContainer?
) : RBlock(modelNode, listNesting, pcontext, rcontext, frameContext, parentContainer) {
    var listStyle: ListStyle? = null

    override fun applyStyle(availWidth: Int, availHeight: Int, updateLayout: Boolean) {
        this.listStyle = null
        super.applyStyle(availWidth, availHeight, updateLayout)
        val rootNode: Any? = this.modelNode()
        if (rootNode !is HTMLElementImpl) {
            return
        }
        val props = rootNode.getCurrentStyle()
        var listStyle: ListStyle? = null
        val listStyleText = props.getListStyle()
        if (listStyleText != null) {
            listStyle = HtmlValues.getListStyle(listStyleText)
        }
        val listStyleTypeText = props.listStyleType
        if (listStyleTypeText != null) {
            val listType = HtmlValues.getListStyleType(listStyleTypeText)
            if (listType != ListStyle.TYPE_UNSET) {
                if (listStyle == null) {
                    listStyle = ListStyle()
                }
                listStyle.type = listType
            }
        }
        if ((listStyle == null) || (listStyle.type == ListStyle.TYPE_UNSET)) {
            val typeAttributeText = rootNode.getAttribute("type")
            if (typeAttributeText != null) {
                val newStyleType = HtmlValues.getListStyleTypeDeprecated(typeAttributeText)
                if (newStyleType != ListStyle.TYPE_UNSET) {
                    if (listStyle == null) {
                        listStyle = ListStyle()
                        this.listStyle = listStyle
                    }
                    listStyle.type = newStyleType
                }
            }
        }
        this.listStyle = listStyle
    }

    override fun toString(): String {
        return "BaseRListElement[node=" + this.modelNode() + "]"
    }

    companion object {
        protected const val DEFAULT_COUNTER_NAME: String = "\$cobra.counter"
    }
}
