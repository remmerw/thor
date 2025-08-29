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
 * Created on Apr 17, 2005
 */
package io.github.remmerw.thor.cobra.util

import java.lang.reflect.Array
import java.util.function.Consumer

/**
 * @author J. H. S.
 */
object ArrayUtilities {
    fun <T> copy(collection: MutableCollection<T>, clazz: Class<T?>?): Array<T?> {
        val castedArray = Array.newInstance(clazz, collection.size) as kotlin.Array<T?>
        return collection.toArray<T?>(castedArray)
    }

    fun <T> copySynched(
        collection: MutableCollection<T>,
        syncObj: Any,
        clazz: Class<T?>?
    ): kotlin.Array<T?> {
        synchronized(syncObj) {
            return copy<T?>(collection, clazz)
        }
    }

    /**
     * For each element of collection, the supplied function is called. The
     * collection is copied in a synchronized block, to avoid concurrent
     * modifications.
     *
     * @param syncObj The object to synchronize upon.
     * @param func    The function to call on each element.
     */

    fun <T> forEachSynched(
        collection: MutableCollection<T>, syncObj: Any,
        consumer: Consumer<T?>
    ) {
        if (collection.size > 0) {
            val clazz = collection.iterator().next()!!.javaClass as Class<T?>
            val copy = copySynched<T>(collection, syncObj, clazz)
            for (element in copy) {
                consumer.accept(element)
            }
        }
    }

    fun <T> iterator(array: kotlin.Array<T?>, offset: Int, length: Int): MutableIterator<T?> {
        return ArrayIterator<T?>(array, offset, length)
    }

    fun <T> contains(ts: kotlin.Array<T?>, t: T?): Boolean {
        for (e in ts) {
            if (e == t) {
                return true
            }
        }
        return false
    }

    private class ArrayIterator<T>(
        private val array: kotlin.Array<T?>,
        private var offset: Int,
        length: Int
    ) : MutableIterator<T?> {
        private val top: Int

        init {
            this.top = offset + length
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#hasNext()
         */
        override fun hasNext(): Boolean {
            return this.offset < this.top
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#next()
         */
        override fun next(): T? {
            return this.array[this.offset++]
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#remove()
         */
        override fun remove() {
            throw UnsupportedOperationException()
        }
    }
}
