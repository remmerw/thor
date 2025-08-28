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
 * Created on Apr 15, 2005
 */
package io.github.remmerw.thor.cobra.util.io

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import kotlin.math.min

/**
 * Wraps an InputStream and records all of the bytes read. This stream supports
 * mark() and reset().
 *
 *
 * Note: Buffered streams should wrap this class as opposed to the other way
 * around.
 *
 * @author J. H. S.
 */
class RecordedInputStream
/**
 *
 */(private val delegate: InputStream, private val maxBufferSize: Int) : InputStream() {
    private val store = ByteArrayOutputStream()
    private var hasReachedEOF = false
    private var hasReachedMaxBufferSize = false
    private var markPosition = -1
    private var readPosition = -1
    private var resetBuffer: ByteArray? = null

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#read()
     */
    @Throws(IOException::class)
    override fun read(): Int {
        if ((this.readPosition != -1) && (this.readPosition < this.resetBuffer!!.size)) {
            val b = this.resetBuffer!![this.readPosition].toInt()
            this.readPosition++
            return b
        } else {
            val b = this.delegate.read()
            if (b != -1) {
                if (!this.hasReachedMaxBufferSize) {
                    this.store.write(b)
                    if (this.store.size() > this.maxBufferSize) {
                        this.hasReachedMaxBufferSize = true
                    }
                }
            } else {
                this.hasReachedEOF = true
            }
            return b
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#available()
     */
    @Throws(IOException::class)
    override fun available(): Int {
        return this.delegate.available()
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#close()
     */
    @Throws(IOException::class)
    override fun close() {
        this.delegate.close()
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#markSupported()
     */
    override fun markSupported(): Boolean {
        return true
    }

    @Synchronized
    override fun mark(readlimit: Int) {
        check(!this.hasReachedMaxBufferSize) { "Maximum buffer size was already reached." }
        this.markPosition = this.store.size()
    }

    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        check(!this.hasReachedMaxBufferSize) { "Maximum buffer size was already reached." }
        val mp = this.markPosition
        val wholeBuffer = this.store.toByteArray()
        val resetBuffer = ByteArray(wholeBuffer.size - mp)
        System.arraycopy(wholeBuffer, mp, resetBuffer, 0, resetBuffer.size)
        this.resetBuffer = resetBuffer
        this.readPosition = 0
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if ((this.readPosition != -1) && (this.readPosition < this.resetBuffer!!.size)) {
            val minLength = min(this.resetBuffer!!.size - this.readPosition, length)
            System.arraycopy(this.resetBuffer, this.readPosition, buffer, offset, minLength)
            this.readPosition += minLength
            return minLength
        } else {
            val numRead = this.delegate.read(buffer, offset, length)
            if (numRead != -1) {
                if (!this.hasReachedMaxBufferSize) {
                    this.store.write(buffer, offset, numRead)
                    if (this.store.size() > this.maxBufferSize) {
                        this.hasReachedMaxBufferSize = true
                    }
                }
            } else {
                this.hasReachedEOF = true
            }
            return numRead
        }
    }

    @Throws(IOException::class)
    fun consumeToEOF() {
        val buffer = ByteArray(8192)
        while (this.read(buffer) != -1) {
            // EMPTY LOOP
        }
    }

    @get:Throws(BufferExceededException::class)
    val bytesRead: ByteArray
        get() {
            if (this.hasReachedMaxBufferSize) {
                throw BufferExceededException()
            }
            return this.store.toByteArray()
        }

    @Throws(UnsupportedEncodingException::class, BufferExceededException::class)
    fun getString(encoding: String?): String? {
        if (this.hasReachedMaxBufferSize) {
            throw BufferExceededException()
        }
        return this.store.toString(encoding)
    }

    fun hasReachedEOF(): Boolean {
        return this.hasReachedEOF
    }
}
