package io.github.remmerw.thor.dom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.w3c.dom.DOMException

abstract class CharacterDataImpl() : NodeImpl(), CharacterDataModel {
    var text: String by mutableStateOf("")

    override fun text(): String {
        return text
    }

    override fun getTextContent(): String {
        return this.text
    }

    override fun setTextContent(textContent: String) {
        this.text = textContent
        if (!this.notificationsSuspended) {
            this.informInvalid()
        }
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
        if (someText.length > 32) {
            someText = someText.substring(0, 29) + "..."
        }
        val length = someText.length
        return this.nodeName + "[length=" + length + ",text=" + someText + "]"
    }
}
