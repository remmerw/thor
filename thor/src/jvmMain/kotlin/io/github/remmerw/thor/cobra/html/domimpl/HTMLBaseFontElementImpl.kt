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
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.style.BaseFontRenderState
import io.github.remmerw.thor.cobra.html.style.HtmlValues
import io.github.remmerw.thor.cobra.html.style.RenderState
import org.w3c.dom.html.HTMLBaseFontElement

class HTMLBaseFontElementImpl(name: String?) : HTMLAbstractUIElement(name), HTMLBaseFontElement {
    override fun getColor(): String? {
        return this.getAttribute("color")
    }

    override fun setColor(color: String?) {
        this.setAttribute("color", color)
    }

    override fun getFace(): String? {
        return this.getAttribute("face")
    }

    override fun setFace(face: String?) {
        this.setAttribute("face", face)
    }

    override fun getSize(): String? {
        return this.getAttribute("size")
    }

    override fun setSize(size: String?) {
        this.setAttribute("size", size)
    }

    override fun createRenderState(prevRenderState: RenderState?): RenderState {
        var prevRenderState = prevRenderState
        val size = this.getAttribute("size")
        if (size != null) {
            val fontNumber = HtmlValues.getFontNumberOldStyle(size, prevRenderState!!)
            // TODO: Check why the following call is not used.
            // final float fontSize = HtmlValues.getFontSize(fontNumber);
            prevRenderState = BaseFontRenderState(prevRenderState, fontNumber)
        }
        return super.createRenderState(prevRenderState)
    }
}
