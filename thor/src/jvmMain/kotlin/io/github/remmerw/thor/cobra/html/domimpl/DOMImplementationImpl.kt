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
 * Created on Oct 15, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.parser.HtmlParser
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import org.unbescape.xml.XmlEscape
import org.w3c.dom.DOMException
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.DocumentType
import org.xml.sax.SAXException
import java.io.ByteArrayInputStream
import java.io.IOException

class DOMImplementationImpl(private val context: UserAgentContext?) : DOMImplementation {
    override fun hasFeature(feature: String?, version: String): Boolean {
        return "HTML" == feature && ("2.0".compareTo(version) <= 0)
    }

    @Throws(DOMException::class)
    override fun createDocumentType(
        qualifiedName: String?,
        publicId: String?,
        systemId: String?
    ): DocumentType {
        return DocumentTypeImpl(qualifiedName, publicId, systemId)
    }

    // TODO: Use default parameter values instead of replicating function. GH #126
    @Throws(DOMException::class)
    fun createDocument(namespaceURI: String?, qualifiedName: String?): Document {
        return createDocument(namespaceURI, qualifiedName, null)
    }

    @Throws(DOMException::class)
    override fun createDocument(
        namespaceURI: String?,
        qualifiedName: String?,
        doctype: DocumentType?
    ): Document {
        return HTMLDocumentImpl(this.context)
    }

    override fun getFeature(feature: String?, version: String): Any? {
        if ("HTML" == feature && ("2.0".compareTo(version) <= 0)) {
            return this
        } else {
            return null
        }
    }

    @Throws(DOMException::class)
    fun createHTMLDocument(title: String?): Document {
        // TODO: Should a new context / null context be used?
        val doc = HTMLDocumentImpl(this.context)
        val parser = HtmlParser(context, doc)
        val escapedTitle = XmlEscape.escapeXml11(title)
        val initString = "<html><head><title>" + escapedTitle + "</title><body></body></html>"
        try {
            parser.parse(ByteArrayInputStream(initString.toByteArray()))
        } catch (e: IOException) {
            throw RuntimeException("Couldn't create HTML Document", e)
        } catch (e: SAXException) {
            throw RuntimeException("Couldn't create HTML Document", e)
        }
        return doc
    }
}
