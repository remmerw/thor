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

import java.util.EventObject
import java.util.LinkedList

/**
 * @author J. H. S.
 */
class EventDispatch {
    private var listeners: MutableCollection<GenericEventListener?>? = null

    fun createListenerCollection(): MutableCollection<GenericEventListener?> {
        return LinkedList<GenericEventListener?>()
    }

    fun addListener(listener: GenericEventListener?) {
        synchronized(this) {
            if (this.listeners == null) {
                this.listeners = this.createListenerCollection()
            }
            this.listeners!!.add(listener)
        }
    }

    fun removeListener(listener: GenericEventListener?) {
        synchronized(this) {
            if (this.listeners != null) {
                this.listeners!!.remove(listener)
            }
        }
    }

    fun fireEvent(event: EventObject?) {
        var larray: Array<GenericEventListener>? = null
        synchronized(this) {
            if (this.listeners != null) {
                larray =
                    this.listeners.toArray<GenericEventListener?>(GenericEventListener.Companion.EMPTY_ARRAY)
            }
        }
        if (larray != null) {
            for (element in larray) {
                // Call holding no locks
                element.processEvent(event)
            }
        }
    }
}
