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
 * Created on Sep 3, 2005
 */
package io.github.remmerw.thor.dom

import org.w3c.dom.Attr
import org.w3c.dom.DOMException
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node

class NamedNodeMapImpl(owner: Element?, attribs: MutableMap<String, String>) :
    NamedNodeMap {
    // Note: class must be public for reflection to work.
    private val attributes: MutableMap<String, Node> = HashMap()
    private val attributeList = ArrayList<Node>()

    init {
        attribs.forEach { (name: String, value: String?) ->
            // TODO: "specified" attributes
            val attr: Attr = AttrImpl(name, value, true, owner, "ID" == name)
            this.attributes.put(name, attr)
            this.attributeList.add(attr)
        }
    }

    override fun getLength(): Int {
        return this.attributeList.size
    }

    override fun getNamedItem(name: String?): Node? {
        return this.attributes.get(name)
    }

    @Throws(DOMException::class)
    override fun getNamedItemNS(namespaceURI: String?, localName: String?): Node? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "No namespace support")
    }

    override fun item(index: Int): Node? {
        return try {
            this.attributeList[index]
        } catch (_: Throwable) {
            null
        }
    }

    @Throws(DOMException::class)
    override fun removeNamedItem(name: String?): Node? {
        return this.attributes.remove(name)
    }

    @Throws(DOMException::class)
    override fun removeNamedItemNS(namespaceURI: String?, localName: String?): Node? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "No namespace support")
    }

    @Throws(DOMException::class)
    override fun setNamedItem(arg: Node): Node {
        val prevValue: Any? = this.attributes.put(arg.nodeName, arg)
        if (prevValue != null) {
            this.attributeList.remove(prevValue)
        }
        this.attributeList.add(arg)
        return arg
    }

    @Throws(DOMException::class)
    override fun setNamedItemNS(arg: Node?): Node? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "No namespace support")
    }
}
