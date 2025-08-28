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
package io.github.remmerw.thor.cobra.util

import java.io.IOException
import java.io.InputStream

/**
 * @author J. H. S.
 */
class MonitoredInputStream @JvmOverloads constructor(
    private val delegate: InputStream,
    minProgressEventGap: Int = 200
) : InputStream() {
    val evtProgress: EventDispatch = EventDispatch()
    private val minProgressEventGap: Long
    private var progress = 0
    private var lastEvenPosted: Long = 0

    init {
        this.minProgressEventGap = minProgressEventGap.toLong()
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
        return false
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#read()
     */
    @Throws(IOException::class)
    override fun read(): Int {
        val b = this.delegate.read()
        if (b != -1) {
            this.progress++
        }
        return b
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Throws(IOException::class)
    override fun read(arg0: ByteArray, arg1: Int, arg2: Int): Int {
        val numRead = this.delegate.read(arg0, arg1, arg2)
        if (numRead != -1) {
            this.progress += numRead
            val currentTime = System.currentTimeMillis()
            if ((currentTime - this.lastEvenPosted) > this.minProgressEventGap) {
                this.evtProgress.fireEvent(InputProgressEvent(this, this.progress))
                this.lastEvenPosted = currentTime
            }
        }
        return numRead
    }
}
