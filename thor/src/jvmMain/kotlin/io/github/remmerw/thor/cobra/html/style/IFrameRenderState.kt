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
import java.util.Locale

class IFrameRenderState(prevRenderState: RenderState?, element: HTMLElementImpl) :
    StyleSheetRenderState(prevRenderState, element) {
    // TODO: if this logic can be moved to attr2Styles, then this render state could be chopped off.
    override fun getOverflowX(): Int {
        var overflow = this.getOverflowX()
        if (overflow != -1) {
            return overflow
        }
        overflow = super.getOverflowX()
        if (overflow == RenderState.Companion.OVERFLOW_NONE) {
            val element = this.element
            if (element != null) {
                var scrolling = element.getAttribute("scrolling")
                if (scrolling != null) {
                    scrolling = scrolling.trim { it <= ' ' }.lowercase(Locale.getDefault())
                    if ("no" == scrolling) {
                        overflow = RenderState.Companion.OVERFLOW_HIDDEN
                    } else if ("yes" == scrolling) {
                        overflow = RenderState.Companion.OVERFLOW_SCROLL
                    } else if ("auto" == scrolling) {
                        overflow = RenderState.Companion.OVERFLOW_AUTO
                    }
                }
            }
        }
        this.overflowX(overflow)
        return overflow
    }

    override fun getOverflowY(): Int {
        var overflow = this.getOverflowY()
        if (overflow != -1) {
            return overflow
        }
        overflow = super.getOverflowY()
        if (overflow == RenderState.Companion.OVERFLOW_NONE) {
            val element = this.element
            if (element != null) {
                var scrolling = element.getAttribute("scrolling")
                if (scrolling != null) {
                    scrolling = scrolling.trim { it <= ' ' }.lowercase(Locale.getDefault())
                    if ("no" == scrolling) {
                        overflow = RenderState.Companion.OVERFLOW_HIDDEN
                    } else if ("yes" == scrolling) {
                        overflow = RenderState.Companion.OVERFLOW_SCROLL
                    } else if ("auto" == scrolling) {
                        overflow = RenderState.Companion.OVERFLOW_AUTO
                    }
                }
            }
        }
        this.overflowY (overflow)
        return overflow
    }

    override fun getBorderInfo(): BorderInfo? {
        var binfo = this.getBorderInfo()
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
                var border = element.getAttribute("frameborder")
                if (border != null) {
                    border = border.trim { it <= ' ' }
                }
                var value: Int
                if (border != null) {
                    try {
                        value = border.toInt()
                    } catch (nfe: NumberFormatException) {
                        value = 0
                    }
                } else {
                    value = 1
                }
                val borderInsets = HtmlInsets()
                borderInsets.bottom = (if (value != 0) 1 else 0)
                borderInsets.right = borderInsets.bottom
                borderInsets.left = borderInsets.right
                borderInsets.top = borderInsets.left
                borderInsets.bottomType = HtmlInsets.Companion.TYPE_PIXELS
                borderInsets.rightType = borderInsets.bottomType
                borderInsets.leftType = borderInsets.rightType
                borderInsets.topType = borderInsets.leftType
                binfo.insets = borderInsets
                if (binfo.topColor == null) {
                    binfo.topColor = Color.DARK_GRAY
                }
                if (binfo.leftColor == null) {
                    binfo.leftColor = Color.DARK_GRAY
                }
                if (binfo.rightColor == null) {
                    binfo.rightColor = Color.LIGHT_GRAY
                }
                if (binfo.bottomColor == null) {
                    binfo.bottomColor = Color.LIGHT_GRAY
                }
                if (value != 0) {
                    binfo.bottomStyle = HtmlValues.BORDER_STYLE_SOLID
                    binfo.rightStyle = binfo.bottomStyle
                    binfo.leftStyle = binfo.rightStyle
                    binfo.topStyle = binfo.leftStyle
                }
            }
        }
        this.borderInfo(binfo)
        return binfo
    }
}
