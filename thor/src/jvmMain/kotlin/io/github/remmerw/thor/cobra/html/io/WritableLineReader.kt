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
 * Created on Nov 13, 2005
 */
package io.github.remmerw.thor.cobra.html.io

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
