package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.MutableStateFlow

abstract class CharacterData(
    document: Document,
    uid: Long,
    name: String,
    type: Short,
    var text: String = ""
) :
    Node(document, uid, name, type) {

    val wurst = MutableStateFlow(text)

    override fun getTextContent(): String {
        return this.text
    }

    override fun setTextContent(textContent: String) {
        this.text = textContent
    }


    @Throws(DOMException::class)
    fun appendData(arg: String) {
        this.text += arg
    }

    @Throws(DOMException::class)
    fun deleteData(offset: Int, count: Int) {
        val buffer = StringBuffer(this.text)
        val result = buffer.delete(offset, offset + count)
        this.text = result.toString()

    }

    @Throws(DOMException::class)
    fun getData(): String {
        return this.text
    }

    @Throws(DOMException::class)
    fun setData(data: String) {
        this.text = data

    }

    fun getLength(): Int {
        return this.text.length
    }

    @Throws(DOMException::class)
    fun insertData(offset: Int, arg: String?) {
        val buffer = StringBuffer(this.text)
        val result = buffer.insert(offset, arg)
        this.text = result.toString()
    }

    @Throws(DOMException::class)
    fun replaceData(offset: Int, count: Int, arg: String) {
        val buffer = StringBuffer(this.text)
        val result = buffer.replace(offset, offset + count, arg)
        this.text = result.toString()
    }

    @Throws(DOMException::class)
    fun substringData(offset: Int, count: Int): String {
        return this.text.substring(offset, offset + count)
    }

    override fun toString(): String {
        var someText = this.text
        if (someText.length > 32) {
            someText = someText.substring(0, 29) + "..."
        }
        val length = someText.length
        return this.getNodeName() + "[length=" + length + ",text=" + someText + "]"
    }
}
