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
import io.github.remmerw.thor.cobra.html.domimpl.HTMLTableElementImpl
import io.github.remmerw.thor.cobra.util.gui.ColorFactory
import java.net.MalformedURLException

class TableRenderState(prevRenderState: RenderState?, element: HTMLElementImpl) :
    StyleSheetRenderState(prevRenderState, element) {
    private var backgroundInfo: BackgroundInfo? =
        INVALID_BACKGROUND_INFO

    override fun getDefaultDisplay(): Int {
        return RenderState.Companion.DISPLAY_TABLE
    }

    override fun invalidate() {
        super.invalidate()
        this.backgroundInfo = INVALID_BACKGROUND_INFO
    }

    // TODO: This could be removed after #158 is implemented
    override fun getBackgroundInfo(): BackgroundInfo? {
        var binfo = this.backgroundInfo
        if (binfo !== INVALID_BACKGROUND_INFO) {
            return binfo
        }
        // Apply style based on deprecated attributes.
        binfo = super.getBackgroundInfo()
        val element = this.element as HTMLTableElementImpl
        if ((binfo == null) || (binfo.backgroundColor == null)) {
            val bgColor = element.bgColor
            if ((bgColor != null) && "" != bgColor) {
                val bgc = ColorFactory.getInstance().getColor(bgColor)
                if (binfo == null) {
                    binfo = BackgroundInfo()
                }
                binfo.backgroundColor = bgc
            }
        }
        if ((binfo == null) || (binfo.backgroundImage == null)) {
            val background = element.getAttribute("background")
            if ((background != null) && "" != background) {
                if (binfo == null) {
                    binfo = BackgroundInfo()
                }
                try {
                    binfo.backgroundImage = this.document.getFullURL(background)
                } catch (mfe: MalformedURLException) {
                    throw IllegalArgumentException(mfe)
                }
            }
        }
        this.backgroundInfo = binfo
        return binfo
    } /* This is being handled by attribute to style mechanism, but keeping the method here for future reference, in case that mechanism is not complete
  public BorderInfo getBorderInfo() {
    BorderInfo binfo = this.borderInfo;
    if (binfo != INVALID_BORDER_INFO) {
      return binfo;
    }
    binfo = super.getBorderInfo();
    if (binfo == null
        || (binfo.topStyle == HtmlValues.BORDER_STYLE_NONE && binfo.bottomStyle == HtmlValues.BORDER_STYLE_NONE
            && binfo.leftStyle == HtmlValues.BORDER_STYLE_NONE && binfo.rightStyle == HtmlValues.BORDER_STYLE_NONE)) {
      if (binfo == null) {
        binfo = new BorderInfo();
      }
      final HTMLElementImpl element = this.element;
      if (element != null) {
        String border = element.getAttribute("border");
        if (border != null) {
          border = border.trim();
          int value;
          int valueType;
          if (border.endsWith("%")) {
            valueType = HtmlInsets.TYPE_PERCENT;
            try {
              value = Integer.parseInt(border.substring(0, border.length() - 1));
            } catch (final NumberFormatException nfe) {
              value = 0;
            }
          } else {
            valueType = HtmlInsets.TYPE_PIXELS;
            try {
              value = Integer.parseInt(border);
            } catch (final NumberFormatException nfe) {
              value = 0;
            }
          }
          final HtmlInsets borderInsets = new HtmlInsets();
          borderInsets.top = borderInsets.left = borderInsets.right = borderInsets.bottom = value;
          borderInsets.topType = borderInsets.leftType = borderInsets.rightType = borderInsets.bottomType = valueType;
          binfo.insets = borderInsets;
          if (binfo.topColor == null) {
            binfo.topColor = Color.LIGHT_GRAY;
          }
          if (binfo.leftColor == null) {
            binfo.leftColor = Color.LIGHT_GRAY;
          }
          if (binfo.rightColor == null) {
            binfo.rightColor = Color.GRAY;
          }
          if (binfo.bottomColor == null) {
            binfo.bottomColor = Color.GRAY;
          }
          if (value != 0) {
            binfo.topStyle = binfo.leftStyle = binfo.rightStyle = binfo.bottomStyle = HtmlValues.BORDER_STYLE_SOLID;
          }
        }
      }
    }
    this.borderInfo = binfo;
    return binfo;
  }*/
}
