package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.Node.TEXT_NODE
import org.w3c.dom.Text

open class TextImpl(document: Document, uid: Long, text: String = "") :
    CharacterDataImpl(document, uid, "#text"), Text {
    init {
        textContent = text
    }

    override fun isElementContentWhitespace(): Boolean {
        val t = this.textContent
        return t.trim { it <= ' ' } == ""
    }


    @Throws(DOMException::class)
    override fun replaceWholeText(content: String?): Text? {
        val parent = this.parentNode as NodeImpl
        return parent.replaceAdjacentTextNodes(this, content)
    }


    @Throws(DOMException::class)
    override fun splitText(offset: Int): Text? {
        val parent = this.parentNode as NodeImpl
        val t = this.textContent
        if ((offset < 0) || (offset > t.length)) {
            throw DOMException(DOMException.INDEX_SIZE_ERR, "Bad offset: $offset")
        }
        val content1 = t.substring(0, offset)
        val content2 = t.substring(offset)
        this.textContent = content1
        val impl = ownerDocument!! as DocumentImpl
        val newNode = TextImpl(ownerDocument!!, impl.nextUid(), content2)
        return parent.insertAfter(newNode, this) as Text?
    }


    override fun getWholeText(): String? {
        val parent = this.parentNode as NodeImpl
        return parent.getTextContent()
    }


    override fun getLocalName(): String? {
        return null
    }


    override fun getNodeType(): Short {
        return TEXT_NODE
    }

    @Throws(DOMException::class)
    override fun getNodeValue(): String {
        return this.textContent
    }

    override fun setNodeValue(nodeValue: String) {
        this.textContent = nodeValue
    }

}
