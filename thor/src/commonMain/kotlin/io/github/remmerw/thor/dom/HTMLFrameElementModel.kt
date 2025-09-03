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
/*
 * Created on Jan 28, 2006
 */
package io.github.remmerw.thor.dom

import org.w3c.dom.Document
import org.w3c.dom.html.HTMLFrameElement

class HTMLFrameElementModel(name: String) : HTMLElementModel(name), HTMLFrameElement {

    private var noResize = false


    override fun getFrameBorder(): String? {
        return this.getAttribute("frameBorder")
    }

    override fun setFrameBorder(frameBorder: String?) {
        this.setAttribute("frameBorder", frameBorder)
    }

    override fun getLongDesc(): String? {
        return this.getAttribute("longdesc")
    }

    override fun setLongDesc(longDesc: String?) {
        this.setAttribute("longdesc", longDesc)
    }

    override fun getMarginHeight(): String? {
        return this.getAttribute("marginHeight")
    }

    override fun setMarginHeight(marginHeight: String?) {
        this.setAttribute("marginHeight", marginHeight)
    }

    override fun getMarginWidth(): String? {
        return this.getAttribute("marginWidth")
    }

    override fun setMarginWidth(marginWidth: String?) {
        this.setAttribute("marginWidth", marginWidth)
    }

    override fun getName(): String? {
        return this.getAttribute("name")
    }

    override fun setName(name: String?) {
        this.setAttribute("name", name)
    }

    override fun getNoResize(): Boolean {
        return this.noResize
    }

    override fun setNoResize(noResize: Boolean) {
        this.noResize = noResize
    }

    override fun getScrolling(): String? {
        return this.getAttribute("scrolling")
    }

    override fun setScrolling(scrolling: String?) {
        this.setAttribute("scrolling", scrolling)
    }

    override fun getSrc(): String? {
        return this.getAttribute("src")
    }

    override fun setSrc(src: String?) {
        this.setAttribute("src", src)
    }

    override fun getContentDocument(): Document? {
        TODO("Not yet implemented")
    }


}
