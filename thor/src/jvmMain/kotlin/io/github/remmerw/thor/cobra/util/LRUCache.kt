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
package io.github.remmerw.thor.cobra.util

import java.io.IOException
import java.io.ObjectInputStream
import java.io.Serializable
import java.util.EventListener
import java.util.EventObject
import java.util.TreeSet
import kotlin.concurrent.Volatile

/**
 * A cache with least-recently-used policy. Note that this class is not thread
 * safe by itself.
 */
class LRUCache(var approxMaxSize: Int) : Serializable {
    private val cacheMap: MutableMap<Any?, OrderedValue?> = HashMap<Any?, OrderedValue?>()

    /**
     * Ascending timestamp order. First is least recently used.
     */
    private val timedSet: TreeSet<OrderedValue?> = TreeSet<OrderedValue?>()

    @Volatile
    @Transient
    private var removalEvent: EventDispatch2
    var approxSize: Int = 0
        private set

    init {
        this.removalEvent = RemovalDispatch()
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) {
        `in`.defaultReadObject()
        // Need to initialize transient fields here.
        this.removalEvent = RemovalDispatch()
    }

    fun put(key: Any?, value: Any?, approxSize: Int) {
        if (approxSize > this.approxMaxSize) {
            // Can't be inserted.
            return
        }
        var ordVal = this.cacheMap.get(key)
        if (ordVal != null) {
            if (ordVal.value !== value) {
                this.removalEvent.fireEvent(RemovalEvent(this, ordVal.value))
            }
            this.approxSize += (approxSize - ordVal.approximateSize)
            this.timedSet.remove(ordVal)
            ordVal.approximateSize = approxSize
            ordVal.value = value
            ordVal.touch()
            this.timedSet.add(ordVal)
        } else {
            ordVal = OrderedValue(key, value, approxSize)
            this.cacheMap.put(key, ordVal)
            this.timedSet.add(ordVal)
            this.approxSize += approxSize
        }
        while (this.approxSize > this.approxMaxSize) {
            this.removeLRU()
        }
    }

    private fun removeLRU() {
        val ordVal = this.timedSet.first()
        if (ordVal != null) {
            this.removalEvent.fireEvent(RemovalEvent(this, ordVal.value))
            if (this.timedSet.remove(ordVal)) {
                this.cacheMap.remove(ordVal.key)
                this.approxSize -= ordVal.approximateSize
            } else {
                throw IllegalStateException("Could not remove existing tree node.")
            }
        } else {
            throw IllegalStateException("Cannot remove LRU since the cache is empty.")
        }
    }

    fun get(key: Any?): Any? {
        val ordVal = this.cacheMap.get(key)
        if (ordVal != null) {
            this.timedSet.remove(ordVal)
            ordVal.touch()
            this.timedSet.add(ordVal)
            return ordVal.value
        } else {
            return null
        }
    }

    fun remove(key: Any?): Any? {
        val ordVal = this.cacheMap.get(key)
        if (ordVal != null) {
            this.removalEvent.fireEvent(RemovalEvent(this, ordVal.value))
            this.approxSize -= ordVal.approximateSize
            this.timedSet.remove(ordVal)
            return ordVal.value
        } else {
            return null
        }
    }

    fun addRemovalListener(listener: RemovalListener?) {
        this.removalEvent.addListener(listener)
    }

    fun removeRemovalListener(listener: RemovalListener?) {
        this.removalEvent.removeListener(listener)
    }

    val numEntries: Int
        get() = this.cacheMap.size

    val entryInfoList: MutableList<EntryInfo?>
        get() {
            val list: MutableList<EntryInfo?> =
                ArrayList<EntryInfo?>()
            val i =
                this.cacheMap.values.iterator()
            while (i.hasNext()) {
                val ov = i.next()
                val value = ov.value
                val vc: Class<out Any?>? =
                    if (value == null) null else value.javaClass
                list.add(EntryInfo(vc, ov.approximateSize))
            }
            return list
        }

    class EntryInfo(val valueClass: Class<out Any?>?, val approximateSize: Int) {
        override fun toString(): String {
            val vc = this.valueClass
            val vcName = if (vc == null) "<none>" else vc.name
            return "[class=" + vcName + ",approx-size=" + this.approximateSize + "]"
        }
    }

    private inner class OrderedValue(
        private val key: Any?,
        private var value: Any?,
        private var approximateSize: Int
    ) : Comparable<OrderedValue?>, Serializable {
        private var timestamp: Long = 0

        init {
            this.touch()
        }

        fun touch() {
            this.timestamp = System.currentTimeMillis()
        }

        override fun compareTo(arg0: OrderedValue): Int {
            if (this === arg0) {
                return 0
            }
            val other = arg0
            val diff = this.timestamp - other.timestamp
            if (diff > 0) {
                return +1
            } else if (diff < 0) {
                return -1
            }
            var hc1 = System.identityHashCode(this)
            var hc2 = System.identityHashCode(other)
            if (hc1 == hc2) {
                hc1 = System.identityHashCode(this.value)
                hc2 = System.identityHashCode(other.value)
            }
            return hc1 - hc2
        }

        companion object {
            private const val serialVersionUID = 340227625744215821L
        }
    }

    private inner class RemovalDispatch : EventDispatch2() {
        override fun dispatchEvent(listener: EventListener, event: EventObject?) {
            (listener as RemovalListener).removed(event as RemovalEvent?)
        }
    }

    companion object {
        private const val serialVersionUID = 940427225784212823L
    }
}
