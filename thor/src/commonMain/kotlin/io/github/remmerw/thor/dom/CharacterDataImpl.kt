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

import org.w3c.dom.CharacterData
import org.w3c.dom.DOMException
import org.w3c.dom.Node

abstract class CharacterDataImpl(var text: String) : NodeImpl(), CharacterData {


    val className: String
        get() = "HTMLCharacterData"


    override fun getTextContent(): String {
        return this.text
    }


    override fun setTextContent(textContent: String) {
        this.text = textContent
        if (!this.notificationsSuspended) {
            this.informInvalid()
        }
    }

    override fun cloneNode(deep: Boolean): Node {
        val newNode = super.cloneNode(deep) as CharacterDataImpl
        newNode.setData(this.data)
        return newNode
    }

    @Throws(DOMException::class)
    override fun appendData(arg: String) {
        this.text += arg
        if (!this.notificationsSuspended) {
            this.informInvalid()
        }
    }

    @Throws(DOMException::class)
    override fun deleteData(offset: Int, count: Int) {
        val buffer = StringBuffer(this.text)
        val result = buffer.delete(offset, offset + count)
        this.text = result.toString()
        if (!this.notificationsSuspended) {
            this.informInvalid()
        }
    }

    @Throws(DOMException::class)
    override fun getData(): String {
        return this.text
    }

    @Throws(DOMException::class)
    override fun setData(data: String) {
        this.text = data
        if (!this.notificationsSuspended) {
            this.informInvalid()
        }
    }

    override fun getLength(): Int {
        return this.text.length
    }

    @Throws(DOMException::class)
    override fun insertData(offset: Int, arg: String?) {
        val buffer = StringBuffer(this.text)
        val result = buffer.insert(offset, arg)
        this.text = result.toString()
        if (!this.notificationsSuspended) {
            this.informInvalid()
        }
    }

    @Throws(DOMException::class)
    override fun replaceData(offset: Int, count: Int, arg: String) {
        val buffer = StringBuffer(this.text)
        val result = buffer.replace(offset, offset + count, arg)
        this.text = result.toString()
        if (!this.notificationsSuspended) {
            this.informInvalid()
        }
    }

    @Throws(DOMException::class)
    override fun substringData(offset: Int, count: Int): String {
        return this.text.substring(offset, offset + count)
    }

    override fun toString(): String {
        var someText = this.text
        if ((someText != null) && (someText.length > 32)) {
            someText = someText.substring(0, 29) + "..."
        }
        val length = if (someText == null) 0 else someText.length
        return this.nodeName + "[length=" + length + ",text=" + someText + "]"
    }
}
