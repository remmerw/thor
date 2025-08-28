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
package io.github.remmerw.thor.cobra.html.style

import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import java.awt.Color

class ImageRenderState(prevRenderState: RenderState?, element: HTMLElementImpl) :
    StyleSheetRenderState(prevRenderState, element) {
    // TODO: if this logic can be moved to attr2Styles, then this render state could be chopped off.
    override fun getMarginInsets(): HtmlInsets? {
        var mi = this.marginInsets
        if (mi !== INVALID_INSETS) {
            return mi
        }
        val props = this.getCssProperties()
        if (props == null) {
            mi = null
        } else {
            mi = HtmlValues.getMarginInsets(props, this)
        }
        if (mi == null) {
            var hspace = 0
            var vspace = 0
            var createNew = false
            val hspaceText = this.element.getAttribute("hspace")
            if ((hspaceText != null) && (hspaceText.length != 0)) {
                createNew = true
                try {
                    hspace = hspaceText.toInt()
                } catch (nfe: NumberFormatException) {
                    // TODO: Percentages?
                }
            }
            val vspaceText = this.element.getAttribute("vspace")
            if ((vspaceText != null) && (vspaceText.length != 0)) {
                createNew = true
                try {
                    vspace = vspaceText.toInt()
                } catch (nfe: NumberFormatException) {
                    // TODO: Percentages?
                }
            }
            if (createNew) {
                mi = HtmlInsets()
                mi.top = vspace
                mi.topType = HtmlInsets.Companion.TYPE_PIXELS
                mi.bottom = vspace
                mi.bottomType = HtmlInsets.Companion.TYPE_PIXELS
                mi.left = hspace
                mi.leftType = HtmlInsets.Companion.TYPE_PIXELS
                mi.right = hspace
                mi.rightType = HtmlInsets.Companion.TYPE_PIXELS
            }
        }
        this.marginInsets = mi
        return mi
    }

    override fun getBorderInfo(): BorderInfo? {
        var binfo = this.borderInfo
        if (binfo !== INVALID_BORDER_INFO) {
            return binfo
        }
        binfo = super.getBorderInfo()
        if ((binfo == null)
            || ((binfo.topStyle == HtmlValues.BORDER_STYLE_NONE) && (binfo.bottomStyle == HtmlValues.BORDER_STYLE_NONE)
                    && (binfo.leftStyle == HtmlValues.BORDER_STYLE_NONE) && (binfo.rightStyle == HtmlValues.BORDER_STYLE_NONE))
        ) {
            if (binfo == null) {
                binfo = BorderInfo()
            }
            val element = this.element
            if (element != null) {
                var border = element.getAttribute("border")
                if (border != null) {
                    border = border.trim { it <= ' ' }
                    var value: Int
                    val valueType: Int
                    if (border.endsWith("%")) {
                        valueType = HtmlInsets.Companion.TYPE_PERCENT
                        try {
                            value = border.substring(0, border.length - 1).toInt()
                        } catch (nfe: NumberFormatException) {
                            value = 0
                        }
                    } else {
                        valueType = HtmlInsets.Companion.TYPE_PIXELS
                        try {
                            value = border.toInt()
                        } catch (nfe: NumberFormatException) {
                            value = 0
                        }
                    }
                    val borderInsets = HtmlInsets()
                    borderInsets.bottom = value
                    borderInsets.right = borderInsets.bottom
                    borderInsets.left = borderInsets.right
                    borderInsets.top = borderInsets.left
                    borderInsets.bottomType = valueType
                    borderInsets.rightType = borderInsets.bottomType
                    borderInsets.leftType = borderInsets.rightType
                    borderInsets.topType = borderInsets.leftType
                    binfo.insets = borderInsets
                    if (binfo.topColor == null) {
                        binfo.topColor = Color.BLACK
                    }
                    if (binfo.leftColor == null) {
                        binfo.leftColor = Color.BLACK
                    }
                    if (binfo.rightColor == null) {
                        binfo.rightColor = Color.BLACK
                    }
                    if (binfo.bottomColor == null) {
                        binfo.bottomColor = Color.BLACK
                    }
                    if (value != 0) {
                        binfo.bottomStyle = HtmlValues.BORDER_STYLE_SOLID
                        binfo.rightStyle = binfo.bottomStyle
                        binfo.leftStyle = binfo.rightStyle
                        binfo.topStyle = binfo.leftStyle
                    }
                }
            }
        }
        this.borderInfo = binfo
        return binfo
    }
}
