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
 * Created on Sep 10, 2005
 */
package io.github.remmerw.thor.dom

import org.w3c.dom.Attr
import org.w3c.dom.DOMException
import org.w3c.dom.Element
import org.w3c.dom.Node.ATTRIBUTE_NODE
import org.w3c.dom.TypeInfo

class AttrImpl : NodeImpl, Attr {
    private val name: String
    private val specified: Boolean
    private val ownerElement: Element?
    private var value: String?
    private var isId: Boolean

    /**
     * @param name
     * @param value
     */
    constructor(
        name: String,
        value: String?,
        specified: Boolean,
        owner: Element?,
        isId: Boolean
    ) : super() {
        this.name = name
        this.value = value
        this.specified = specified
        this.ownerElement = owner
        this.isId = isId
    }

    /**
     * @param name
     */
    constructor(name: String) : super() {
        this.name = name
        this.value = ""
        this.specified = false
        this.ownerElement = null
        this.isId = false
    }

    override fun getLocalName(): String {
        return this.name
    }

    override fun getNodeName(): String {
        return this.name
    }

    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        return this.value
    }

    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
        this.value = nodeValue
    }

    override fun getNodeType(): Short {
        return ATTRIBUTE_NODE
    }

    override fun getName(): String {
        return this.name
    }

    override fun getSpecified(): Boolean {
        return this.specified
    }

    override fun getValue(): String? {
        return this.value
    }

    @Throws(DOMException::class)
    override fun setValue(value: String?) {
        this.value = value
    }

    override fun getOwnerElement(): Element? {
        return this.ownerElement
    }

    override fun getSchemaTypeInfo(): TypeInfo? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    override fun isId(): Boolean {
        return this.isId
    }

}
