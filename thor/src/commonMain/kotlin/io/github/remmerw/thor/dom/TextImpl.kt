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
 * Created on Sep 4, 2005
 */
package io.github.remmerw.thor.dom

import io.github.remmerw.thor.Strings
import org.w3c.dom.DOMException
import org.w3c.dom.Node
import org.w3c.dom.Node.TEXT_NODE
import org.w3c.dom.Text

open class TextImpl(text: String = "") : CharacterDataImpl(text), Text {

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.html2.Text#isElementContentWhitespace()
     */
    override fun isElementContentWhitespace(): Boolean {
        val t = this.text
        return (t == null) || t.trim { it <= ' ' } == ""
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.html2.Text#replaceWholeText(java.lang.String)
     */
    @Throws(DOMException::class)
    override fun replaceWholeText(content: String?): Text? {
        val parent = this.nodeParent as NodeImpl
        if (parent == null) {
            throw DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Text node has no parent")
        }
        return parent.replaceAdjacentTextNodes(this, content)
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.html2.Text#splitText(int)
     */
    @Throws(DOMException::class)
    override fun splitText(offset: Int): Text? {
        val parent = this.nodeParent as NodeImpl
        if (parent == null) {
            throw DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Text node has no parent")
        }
        val t = this.text!!
        if ((offset < 0) || (offset > t.length)) {
            throw DOMException(DOMException.INDEX_SIZE_ERR, "Bad offset: " + offset)
        }
        val content1 = t.substring(0, offset)
        val content2 = t.substring(offset)
        this.text = content1
        val newNode = TextImpl(content2)
        newNode.setOwnerDocument(this.document)
        return parent.insertAfter(newNode, this) as Text?
    }


    override fun getWholeText(): String? {
        val parent = this.nodeParent as NodeImpl
        if (parent == null) {
            throw DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Text node has no parent")
        }
        return parent.getTextContent()
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#getlocalName()
     */
    override fun getLocalName(): String? {
        return null
    }


    override fun getNodeName(): String {
        return "#text"
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#getnodeType()
     */
    override fun getNodeType(): Short {
        return TEXT_NODE
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#getnodeValue()
     */
    @Throws(DOMException::class)
    override fun getNodeValue(): String {
        return this.text!!
    }

    override fun setNodeValue(nodeValue: String) {
        this.text = nodeValue
    }

    override fun setTextContent(textContent: String) {
        this.text = textContent
    }

    override fun createSimilarNode(): Node {
        return TextImpl(this.text!!)
    }

    override fun toString(): String {
        val text = this.text
        val textLength = if (text == null) 0 else text.length
        return "#text[length=" + textLength + ",value=\"" + Strings.truncate(text, 64)
    }
}
