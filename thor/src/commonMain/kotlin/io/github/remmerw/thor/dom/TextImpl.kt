package io.github.remmerw.thor.dom

import io.github.remmerw.thor.core.Strings
import org.w3c.dom.DOMException
import org.w3c.dom.Node.TEXT_NODE
import org.w3c.dom.Text

open class TextImpl(text: String = "") : CharacterDataImpl(), TextModel {
    init {
        textContent = text
    }

    override fun isElementContentWhitespace(): Boolean {
        val t = this.text
        return (t == null) || t.trim { it <= ' ' } == ""
    }


    @Throws(DOMException::class)
    override fun replaceWholeText(content: String?): Text? {
        val parent = this.nodeParent as NodeImpl
        if (parent == null) {
            throw DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Text node has no parent")
        }
        return parent.replaceAdjacentTextNodes(this, content)
    }


    @Throws(DOMException::class)
    override fun splitText(offset: Int): Text? {
        val parent = this.nodeParent as NodeImpl
        if (parent == null) {
            throw DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Text node has no parent")
        }
        val t = this.text
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


    override fun getLocalName(): String? {
        return null
    }

    override fun getNodeName(): String {
        return "CHARS"
    }

    override fun getNodeType(): Short {
        return TEXT_NODE
    }

    @Throws(DOMException::class)
    override fun getNodeValue(): String {
        return this.text
    }

    override fun setNodeValue(nodeValue: String) {
        this.text = nodeValue
    }

    override fun setTextContent(textContent: String) {
        this.text = textContent
    }


    override fun toString(): String {
        val text = this.text
        val textLength = text.length
        return "#text[length=" + textLength + ",value=\"" + Strings.truncate(text, 64)
    }
}
