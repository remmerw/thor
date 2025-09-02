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
package io.github.remmerw.thor.dom

import io.github.remmerw.thor.parser.HtmlParser
import org.w3c.dom.UserDataHandler
import java.lang.Boolean
import kotlin.Any
import kotlin.String

class HTMLBaseElementModel(name: String) : HTMLElementModel(name) {
    override fun setUserData(key: String, data: Any?, handler: UserDataHandler?): Any? {
        if (HtmlParser.MODIFYING_KEY == key && (data != Boolean.TRUE)) {
            this.processBaseTag()
        }
        return super.setUserData(key, data, handler)
    }

    private fun processBaseTag() {
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            doc.setBaseURI(this.getAttribute("href"))
        }
    }
}
