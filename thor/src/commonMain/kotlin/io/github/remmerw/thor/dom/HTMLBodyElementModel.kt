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
 * Created on Oct 8, 2005
 */
package io.github.remmerw.thor.dom

import org.w3c.dom.Document
import org.w3c.dom.html.HTMLBodyElement
import org.w3c.dom.html.HTMLDocument

class HTMLBodyElementModel(name: String) : HTMLElementModel(name), HTMLBodyElement {
    override fun setOwnerDocument(value: Document?, deep: Boolean) {
        super.setOwnerDocument(value, deep)
        if (value is HTMLDocument) {
            value.body = this
        }
    }

    override fun setOwnerDocument(value: Document?) {
        super.setOwnerDocument(value)
        if (value is HTMLDocument) {
            value.body = this
        }
    }

    override fun getALink(): String? {
        return this.getAttribute("alink")
    }

    override fun setALink(aLink: String?) {
        this.setAttribute("alink", aLink)
    }

    override fun getBackground(): String? {
        return this.getAttribute("background")
    }

    override fun setBackground(background: String?) {
        this.setAttribute("background", background)
    }

    override fun getBgColor(): String? {
        return this.getAttribute("bgcolor")
    }

    override fun setBgColor(bgColor: String?) {
        this.setAttribute("bgcolor", bgColor)
    }

    override fun getLink(): String? {
        return this.getAttribute("link")
    }

    override fun setLink(link: String?) {
        this.setAttribute("link", link)
    }

    override fun getText(): String? {
        return this.getAttribute("text")
    }

    override fun setText(text: String?) {
        this.setAttribute("text", text)
    }

    override fun getVLink(): String? {
        return this.getAttribute("vlink")
    }

    override fun setVLink(vLink: String?) {
        this.setAttribute("vlink", vLink)
    }

}
