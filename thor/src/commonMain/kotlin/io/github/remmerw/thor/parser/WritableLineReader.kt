package io.github.remmerw.thor.parser

import java.io.IOException
import java.io.LineNumberReader
import java.io.Reader
import kotlin.math.min

open class WritableLineReader(reader: Reader) : LineNumberReader(reader) {

    private var writeBuffer: StringBuffer? = null

    @Throws(IOException::class)
    override fun read(): Int {
        val sb = this.writeBuffer
        if ((sb != null) && (sb.length > 0)) {
            val ch = sb.get(0)
            sb.deleteCharAt(0)
            if (sb.length == 0) {
                this.writeBuffer = null
            }
            return ch.code
        }
        return super.read()
    }


    @Throws(IOException::class)
    override fun read(b: CharArray, off: Int, len: Int): Int {
        val sb = this.writeBuffer
        if ((sb != null) && (sb.length > 0)) {
            val srcEnd = min(sb.length, len)
            sb.getChars(0, srcEnd, b, off)
            sb.delete(0, srcEnd)
            if (sb.length == 0) {
                this.writeBuffer = null
            }
            return srcEnd
        }
        return super.read(b, off, len)
    }

    @Throws(IOException::class)
    override fun ready(): Boolean {
        val sb = this.writeBuffer
        if ((sb != null) && (sb.length > 0)) {
            return true
        }
        return super.ready()
    }


    @Throws(IOException::class)
    override fun close() {
        this.writeBuffer = null
        super.close()
    }


    @Throws(IOException::class)
    open fun write(text: String?) {
        // Document overrides this to know that new data is coming.
        var sb = this.writeBuffer
        if (sb == null) {
            sb = StringBuffer()
            this.writeBuffer = sb
        }
        sb.append(text)
    }
}