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

import java.util.EventListener

/**
 * Listener of asynchronous results.
 *
 * @author J. H. S.
 * @see AsyncResult
 */
interface AsyncResultListener<TResult> : EventListener {
    /**
     * Receives an asynchronous result. This method is invoked in the event
     * dispatch thread.
     *
     * @param event Event containing asynchronous result.
     */
    fun resultReceived(event: AsyncResultEvent<TResult?>?)

    /**
     * Called when an exception has occurred trying to obtain an asynchronous
     * result. This method is invoked in the event dispatch thread.
     *
     * @param event Event containing the exception.
     */
    fun exceptionReceived(event: AsyncResultEvent<Throwable?>?)
}
