package io.github.remmerw.thor.parser

import java.io.IOException
import java.io.LineNumberReader
import java.io.Reader
import kotlin.math.min

open class WritableLineReader : LineNumberReader {
    private val delegate: Reader
    private var writeBuffer: StringBuffer? = null

    constructor(reader: Reader, bufferSize: Int) : super(reader, bufferSize) {
        this.delegate = reader
    }

    constructor(reader: Reader) : super(reader) {
        this.delegate = reader
    }

    /*
     * Note: Not implicitly thread safe.
     */
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

    /*
     * (non-Javadoc) Note: Not implicitly thread safe.
     *
     * @see java.io.Reader#read(byte[], int, int)
     */
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

    /*
     * (non-Javadoc) Note: Not implicitly thread safe.
     *
     * @see java.io.Reader#close()
     */
    @Throws(IOException::class)
    override fun close() {
        this.writeBuffer = null
        super.close()
    }

    /**
     * Note: Not implicitly thread safe.
     *
     * @param text
     * @throws IOException
     */
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