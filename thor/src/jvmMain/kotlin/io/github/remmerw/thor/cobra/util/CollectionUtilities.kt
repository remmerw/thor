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
 * Created on Jun 9, 2005
 */
package io.github.remmerw.thor.cobra.util

import java.util.Enumeration
import java.util.LinkedList

/**
 * @author J. H. S.
 */
object CollectionUtilities {
    private val emptyIterator: MutableIterator<Any?> = object : MutableIterator<Any?> {
        override fun hasNext(): Boolean {
            return false
        }

        override fun next(): Any? {
            throw NoSuchElementException()
        }

        override fun remove() {
            throw NoSuchElementException()
        }
    }

    fun <T> getIteratorEnumeration(i: MutableIterator<T?>): Enumeration<T?> {
        return object : Enumeration<T?> {
            override fun hasMoreElements(): Boolean {
                return i.hasNext()
            }

            override fun nextElement(): T? {
                return i.next()
            }
        }
    }

    fun <T> getEmptyEnumeration(): Enumeration<T?> {
        return object : Enumeration<T?> {
            override fun hasMoreElements(): Boolean {
                return false
            }

            override fun nextElement(): T? {
                throw NoSuchElementException("Trying to get element of an empty enumeration")
            }
        }
    }

    fun <T> iteratorUnion(iterators: Array<MutableIterator<T?>?>): MutableIterator<T?> {
        return object : MutableIterator<T?> {
            private var iteratorIndex = 0
            private var current = if (iterators.size > 0) iterators[0] else null

            override fun hasNext(): Boolean {
                while (true) {
                    if (current == null) {
                        return false
                    }
                    if (current!!.hasNext()) {
                        return true
                    }
                    iteratorIndex++
                    current =
                        if (iteratorIndex >= iterators.size) null else iterators[iteratorIndex]
                }
            }

            override fun next(): T? {
                while (true) {
                    if (this.current == null) {
                        throw NoSuchElementException()
                    }
                    try {
                        return this.current!!.next()
                    } catch (nse: NoSuchElementException) {
                        this.iteratorIndex++
                        this.current =
                            if (this.iteratorIndex >= iterators.size) null else iterators[this.iteratorIndex]
                    }
                }
            }

            override fun remove() {
                if (this.current == null) {
                    throw NoSuchElementException()
                }
                this.current!!.remove()
            }
        }
    }

    fun <T> reverse(collection: MutableCollection<T?>): MutableCollection<T?> {
        val newCollection = LinkedList<T?>()
        val i = collection.iterator()
        while (i.hasNext()) {
            newCollection.addFirst(i.next())
        }
        return newCollection
    }

    fun <T> singletonIterator(item: T?): MutableIterator<T?> {
        return object : MutableIterator<T?> {
            private var gotItem = false

            override fun hasNext(): Boolean {
                return !this.gotItem
            }

            override fun next(): T? {
                if (this.gotItem) {
                    throw NoSuchElementException()
                }
                this.gotItem = true
                return item
            }

            override fun remove() {
                if (!this.gotItem) {
                    this.gotItem = true
                } else {
                    throw NoSuchElementException()
                }
            }
        }
    }

    fun <T> emptyIterator(): MutableIterator<T?> {
        return emptyIterator as MutableIterator<T?>
    }

    fun <T> reverseIterator(sr: MutableList<T>): MutableIterator<T?> {
        return ListReverser<T?>(sr as MutableList<T?>).iterator()
    }

    // Filter iterator adapted from an implementation found in http://erikras.com/2008/01/18/the-filter-pattern-java-conditional-abstraction-with-iterables/
    fun <T> filter(
        iterator: MutableIterator<T>,
        filterFunction: FilterFunction<T>
    ): MutableIterator<T?> {
        return FilterIterator<T>(iterator, filterFunction)
    }

    interface FilterFunction<T> {
        fun passes(`object`: T?): Boolean
    }

    class ListReverser<T>(wrappedList: MutableList<T>) : Iterable<T?> {
        private val listIterator: MutableListIterator<T>

        init {
            this.listIterator = wrappedList.listIterator(wrappedList.size)
        }

        override fun iterator(): MutableIterator<T?> {
            return object : MutableIterator<T?> {
                override fun hasNext(): Boolean {
                    return listIterator.hasPrevious()
                }

                override fun next(): T? {
                    return listIterator.previous()
                }

                override fun remove() {
                    listIterator.remove()
                }
            }
        }
    }

    class FilterIterator<T>(
        private val iterator: MutableIterator<T>,
        private val filterFunction: FilterFunction<T>
    ) : MutableIterator<T?> {
        private var next: T? = null

        init {
            toNext()
        }

        override fun hasNext(): Boolean {
            return next != null
        }

        override fun next(): T? {
            val lNext = this.next
            if (lNext != null) {
                val returnValue: T = lNext
                toNext()
                return returnValue
            } else {
                throw NoSuchElementException()
            }
        }

        override fun remove() {
            throw UnsupportedOperationException()
        }

        private fun toNext() {
            next = null
            while (iterator.hasNext()) {
                val item: T? = iterator.next()
                if (filterFunction.passes(item)) {
                    next = item
                    break
                }
            }
        }
    }
}
