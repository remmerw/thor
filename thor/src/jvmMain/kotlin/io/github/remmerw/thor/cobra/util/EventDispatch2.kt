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
 * Created on Mar 19, 2005
 */
package io.github.remmerw.thor.cobra.util

import java.util.EventListener
import java.util.EventObject

/**
 * @author J. H. S.
 */
abstract class EventDispatch2 {
    private var listeners: MutableCollection<EventListener?>? = null

    fun createListenerCollection(): MutableCollection<EventListener?> {
        return ArrayList<EventListener?>()
    }

    fun addListener(listener: EventListener?) {
        synchronized(this) {
            if (this.listeners == null) {
                this.listeners = this.createListenerCollection()
            }
            this.listeners!!.add(listener)
        }
    }

    fun removeListener(listener: EventListener?) {
        synchronized(this) {
            if (this.listeners != null) {
                this.listeners!!.remove(listener)
            }
        }
    }

    fun fireEvent(event: EventObject?): Boolean {
        val larray: Array<EventListener?>?
        synchronized(this) {
            val listeners = this.listeners
            if ((listeners == null) || (listeners.size == 0)) {
                return false
            }
            larray = this.listeners.toArray<EventListener?>(EMPTY_ARRAY)
        }
        val length = larray!!.size
        for (i in 0..<length) {
            // Call holding no locks
            this.dispatchEvent(larray[i], event)
        }
        return true
    }

    protected abstract fun dispatchEvent(listener: EventListener?, event: EventObject?)

    companion object {
        private val EMPTY_ARRAY = arrayOfNulls<EventListener>(0)
    }
}
