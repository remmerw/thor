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

/**
 * @author J. H. S.
 */
object ArrayUtilities {

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
