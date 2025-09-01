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
package io.github.remmerw.thor.style

import io.github.remmerw.thor.ColorFactory
import io.github.remmerw.thor.dom.HTMLElementImpl
import io.github.remmerw.thor.dom.HTMLTableElementImpl
import java.net.MalformedURLException

class TableRenderState(prevRenderState: RenderState?, element: HTMLElementImpl) :
    StyleSheetRenderState(prevRenderState, element) {
    private var backgroundInfo: BackgroundInfo? = INVALID_BACKGROUND_INFO

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
                val bgc = ColorFactory.instance!!.getColor(bgColor)
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
                    binfo.backgroundImage = this.document!!.getFullURL(background)
                } catch (mfe: MalformedURLException) {
                    throw IllegalArgumentException(mfe)
                }
            }
        }
        this.backgroundInfo = binfo
        return binfo
    }
}
