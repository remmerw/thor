/*
    GNU GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    verion 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Mar 4, 2006
 */
package io.github.remmerw.thor.cobra.async

import io.github.remmerw.thor.cobra.util.ArrayUtilities
import java.util.LinkedList
import java.util.function.Consumer

/**
 * Internal class.
 *
 * @author J. H. S.
 */
internal class AsyncResultWrapper<TResult>(private var ar: AsyncResult<TResult?>?) :
    AsyncResult<TResult?>, AsyncResultListener<TResult?> {
    private val listeners: MutableCollection<AsyncResultListener<TResult?>?> =
        LinkedList<AsyncResultListener<TResult?>?>()

    var asyncResult: AsyncResult<TResult?>?
        get() = this.ar
        /**
         * @param ar The ar to set.
         */
        set(ar) {
            val oldResult = this.ar
            if (oldResult != null) {
                oldResult.removeResultListener(this)
            }
            if (ar != null) {
                ar.addResultListener(this)
            }
            this.ar = ar
        }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.clientlet.AsyncResult#addResultListener(org.xamjwg.clientlet
     * .AsyncResultListener)
     */
    override fun addResultListener(listener: AsyncResultListener<TResult?>?) {
        synchronized(this) {
            this.listeners.add(listener)
        }
        val ar = this.ar
        if (ar != null) {
            ar.signal()
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.clientlet.AsyncResult#removeResultListener(org.xamjwg.clientlet
     * .AsyncResultListener)
     */
    override fun removeResultListener(listener: AsyncResultListener<TResult?>?) {
        synchronized(this) {
            this.listeners.remove(listener)
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.clientlet.AsyncResultListener#exceptionReceived(org.xamjwg.clientlet.AsyncResultEvent)
     */
    override fun exceptionReceived(event: AsyncResultEvent<Throwable?>?) {
        ArrayUtilities.forEachSynched<AsyncResultListener<TResult?>?, RuntimeException?>(
            this.listeners,
            this,
            Consumer { l: AsyncResultListener<TResult?>? -> l!!.exceptionReceived(event) })
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.clientlet.AsyncResultListener#resultReceived(org.xamjwg.clientlet.AsyncResultEvent)
     */
    override fun resultReceived(event: AsyncResultEvent<TResult?>?) {
        ArrayUtilities.forEachSynched<AsyncResultListener<TResult?>?, RuntimeException?>(
            this.listeners,
            this,
            Consumer { l: AsyncResultListener<TResult?>? -> l!!.resultReceived(event) })
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.clientlet.AsyncResult#signal()
     */
    override fun signal() {
        val ar = this.ar
        if (ar != null) {
            ar.signal()
        }
    }
}
