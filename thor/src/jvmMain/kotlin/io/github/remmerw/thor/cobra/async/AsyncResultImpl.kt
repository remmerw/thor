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
 * Created on Mar 31, 2005
 */
package io.github.remmerw.thor.cobra.async

import io.github.remmerw.thor.cobra.util.EventDispatch
import io.github.remmerw.thor.cobra.util.GenericEventListener
import java.util.EventObject
import javax.swing.SwingUtilities

/**
 * @author J. H. S.
 */
class AsyncResultImpl<TResult> : AsyncResult<TResult?> {
    private val evtResult = EventDispatch()
    private var result: TResult? = null
    private var exception: Throwable? = null
    private var hasResult = false

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.dom.AsyncResult#addResultListener(org.xamjwg.dom.AsyncResultListener
     * )
     */
    override fun addResultListener(listener: AsyncResultListener<TResult?>) {
        synchronized(this) {
            if (this.hasResult) {
                if (this.exception != null) {
                    val exception = this.exception
                    SwingUtilities.invokeLater(Runnable {
                        // Invoke holding no locks
                        val are = AsyncResultEvent<Throwable?>(this@AsyncResultImpl, exception)
                        listener.exceptionReceived(are)
                    })
                } else {
                    val result = this.result
                    SwingUtilities.invokeLater(Runnable {
                        // Invoke holding no locks
                        val are = AsyncResultEvent<TResult?>(this@AsyncResultImpl, result)
                        listener.resultReceived(are)
                    })
                }
            }
            evtResult.addListener(EventListenerWrapper<TResult?>(listener))
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.clientlet.AsyncResult#removeResultListener(org.xamjwg.clientlet
     * .AsyncResultListener)
     */
    override fun removeResultListener(listener: AsyncResultListener<TResult?>) {
        this.evtResult.removeListener(EventListenerWrapper<TResult?>(listener))
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.clientlet.AsyncResult#signal()
     */
    override fun signal() {
        synchronized(this) {
            if (this.hasResult) {
                if (this.exception != null) {
                    val exception = this.exception
                    SwingUtilities.invokeLater(Runnable {
                        // Invoke holding no locks
                        val are = AsyncResultEvent<Throwable?>(this@AsyncResultImpl, exception)
                        evtResult.fireEvent(are)
                    })
                } else {
                    val result = this.result
                    SwingUtilities.invokeLater(Runnable {
                        // Invoke holding no locks
                        val are = AsyncResultEvent<TResult?>(this@AsyncResultImpl, result)
                        evtResult.fireEvent(are)
                    })
                }
            }
        }
    }

    fun setResult(result: TResult?) {
        synchronized(this) {
            this.result = result
            this.hasResult = true
            SwingUtilities.invokeLater(Runnable {
                evtResult.fireEvent(
                    AsyncResultEvent<TResult?>(
                        this@AsyncResultImpl,
                        result
                    )
                )
            })
        }
    }

    fun setException(exception: Throwable?) {
        synchronized(this) {
            this.exception = exception
            this.hasResult = true
            SwingUtilities.invokeLater(Runnable {
                evtResult.fireEvent(
                    AsyncResultEvent<Throwable?>(
                        this@AsyncResultImpl,
                        exception
                    )
                )
            })
        }
    }

    private class EventListenerWrapper<TR>
    /**
     * @param listener
     */(private val listener: AsyncResultListener<TR?>) : GenericEventListener {
        override fun processEvent(event: EventObject?) {
            // Invoke holding no locks
            val are = event as AsyncResultEvent<*>
            if (are.getResult() is Exception) {
                val areException = are as AsyncResultEvent<Throwable?>
                this.listener.exceptionReceived(areException)
            } else {
                val areResult = are as AsyncResultEvent<TR?>
                this.listener.resultReceived(areResult)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (other !is EventListenerWrapper<*>) {
                return false
            }
            return other.listener == this.listener
        }

        override fun hashCode(): Int {
            return this.listener.hashCode()
        }
    }
}
