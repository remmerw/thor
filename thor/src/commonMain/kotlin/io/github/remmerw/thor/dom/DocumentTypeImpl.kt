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
package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.DocumentType
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node.DOCUMENT_TYPE_NODE

class DocumentTypeImpl(
    private val qualifiedName: String,
    private val publicId: String?,
    private val systemId: String?
) : NodeImpl(), DocumentType {
    override fun getLocalName(): String? {
        return null
    }

    override fun getNodeName(): String {
        return this.name
    }

    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        return null
    }

    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
        // nop
    }

    override fun getNodeType(): Short {
        return DOCUMENT_TYPE_NODE
    }

    override fun getName(): String {
        return this.qualifiedName
    }

    override fun getEntities(): NamedNodeMap? {
        // TODO: DOCTYPE declared entities
        return null
    }

    override fun getNotations(): NamedNodeMap? {
        // TODO: DOCTYPE notations
        return null
    }

    override fun getPublicId(): String? {
        return this.publicId
    }

    override fun getSystemId(): String? {
        return this.systemId
    }

    override fun getInternalSubset(): String? {
        // TODO: DOCTYPE internal subset
        return null
    }

}
