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

import io.github.remmerw.thor.cobra.html.parser.HtmlParser
import org.w3c.dom.UserDataHandler
import java.lang.Boolean
import kotlin.Any
import kotlin.String

class HTMLTitleElementImpl(name: String) : HTMLElementImpl(name) {
    override fun setUserData(key: String, data: Any?, handler: UserDataHandler?): Any? {
        if (HtmlParser.MODIFYING_KEY == key && (data == Boolean.FALSE)) {
            val document = this.document
            if (document is HTMLDocumentImpl) {
                val textContent = this.getTextContent()
                val title = if (textContent == null) null else textContent.trim { it <= ' ' }
                document.title = title
            }
        }
        return super.setUserData(key, data, handler)
    }
}
