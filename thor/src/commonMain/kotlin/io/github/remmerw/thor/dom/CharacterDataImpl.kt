package io.github.remmerw.thor.dom

import org.w3c.dom.CharacterData
import org.w3c.dom.DOMException
import org.w3c.dom.Document

abstract class CharacterDataImpl(document: Document, uid: Long, name: String) :
    NodeImpl(document, uid, name),
    CharacterData {
    private var text: String = ""


    override fun getTextContent(): String {
        return this.text
    }

    override fun setTextContent(textContent: String) {
        this.text = textContent
    }


    @Throws(DOMException::class)
    override fun appendData(arg: String) {
        this.text += arg
    }

    @Throws(DOMException::class)
    override fun deleteData(offset: Int, count: Int) {
        val buffer = StringBuffer(this.text)
        val result = buffer.delete(offset, offset + count)
        this.text = result.toString()

    }

    @Throws(DOMException::class)
    override fun getData(): String {
        return this.text
    }

    @Throws(DOMException::class)
    override fun setData(data: String) {
        this.text = data

    }

    override fun getLength(): Int {
        return this.text.length
    }

    @Throws(DOMException::class)
    override fun insertData(offset: Int, arg: String?) {
        val buffer = StringBuffer(this.text)
        val result = buffer.insert(offset, arg)
        this.text = result.toString()
    }

    @Throws(DOMException::class)
    override fun replaceData(offset: Int, count: Int, arg: String) {
        val buffer = StringBuffer(this.text)
        val result = buffer.replace(offset, offset + count, arg)
        this.text = result.toString()
    }

    @Throws(DOMException::class)
    override fun substringData(offset: Int, count: Int): String {
        return this.text.substring(offset, offset + count)
    }

    override fun toString(): String {
        var someText = this.text
        if (someText.length > 32) {
            someText = someText.substring(0, 29) + "..."
        }
        val length = someText.length
        return this.nodeName + "[length=" + length + ",text=" + someText + "]"
    }
}
