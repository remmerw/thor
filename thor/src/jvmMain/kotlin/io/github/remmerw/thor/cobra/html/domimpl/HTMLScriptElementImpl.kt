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
package io.github.remmerw.thor.cobra.html.domimpl

import org.w3c.dom.html.HTMLScriptElement

class HTMLScriptElementImpl(name: String) : HTMLElementImpl(name), HTMLScriptElement {
    private var text: String? = null
    private var defer = false


    override fun getText(): String? {
        val t = this.text
        if (t == null) {
            return this.getRawInnerText(true)
        } else {
            return t
        }
    }

    override fun setText(text: String?) {
        this.text = text
    }

    override fun getHtmlFor(): String? {
        return this.getAttribute("htmlFor")
    }

    override fun setHtmlFor(htmlFor: String?) {
        this.setAttribute("htmlFor", htmlFor)
    }

    override fun getEvent(): String? {
        return this.getAttribute("event")
    }

    override fun setEvent(event: String?) {
        this.setAttribute("event", event)
    }

    override fun getDefer(): Boolean {
        return this.defer
    }

    override fun setDefer(defer: Boolean) {
        this.defer = defer
    }

    override fun getSrc(): String? {
        return this.getAttribute("src")
    }

    override fun setSrc(src: String?) {
        this.setAttribute("src", src)
    }

    override fun getType(): String? {
        return this.getAttribute("type")
    }

    override fun setType(type: String?) {
        this.setAttribute("type", type)
    }


    override fun appendInnerTextImpl(buffer: StringBuffer) {
        // nop
    }

}
