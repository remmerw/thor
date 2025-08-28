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
 * Created on Jun 6, 2005
 */
package io.github.remmerw.thor.cobra.util

import java.io.IOException
import java.io.ObjectInputStream
import java.io.Serializable
import java.util.Arrays
import java.util.LinkedList
import java.util.SortedSet
import java.util.TreeSet

/**
 * @author J. H. S.
 */
// TODO: Looks like it is not used
class History(
    /**
     * @param sequenceCapacity The sequenceCapacity to set.
     */
    var sequenceCapacity: Int,
    /**
     * @param commonEntriesCapacity The commonEntriesCapacity to set.
     */
    var commonEntriesCapacity: Int
) : Serializable {
    private val historySortedSet: SortedSet<String?> = TreeSet<String?>()
    private val historyMap: MutableMap<String?, TimedEntry?> = HashMap<String?, TimedEntry?>()
    private val historyTimedSet: SortedSet<TimedEntry> = TreeSet<TimedEntry>()

    @Transient
    private var historySequence: ArrayList<String?>
    /**
     * @return Returns the sequenceCapacity.
     */
    /**
     * @return Returns the commonEntriesCapacity.
     */

    @Transient
    private var sequenceIndex: Int

    /**
     * @param sequenceCapacity
     * @param commonEntriesCapacity
     */
    init {
        this.historySequence = ArrayList<String?>()
        this.sequenceIndex = -1
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(`in`: ObjectInputStream) {
        this.historySequence = ArrayList<String?>()
        this.sequenceIndex = -1
        `in`.defaultReadObject()
    }

    val currentItem: String?
        get() {
            if (this.sequenceIndex >= 0) {
                return this.historySequence.get(this.sequenceIndex)
            } else {
                return null
            }
        }

    fun back(): String? {
        if (this.sequenceIndex > 0) {
            this.sequenceIndex--
            return this.currentItem
        } else {
            return null
        }
    }

    fun forward(): String? {
        if ((this.sequenceIndex + 1) < this.historySequence.size) {
            this.sequenceIndex++
            return this.currentItem
        } else {
            return null
        }
    }

    fun getRecentItems(maxNumItems: Int): MutableCollection<String?> {
        val items: MutableCollection<String?> = LinkedList<String?>()
        val i = this.historyTimedSet.iterator()
        var count = 0
        while (i.hasNext() && (count++ < maxNumItems)) {
            val entry = i.next()
            items.add(entry.value)
        }
        return items
    }

    fun getHeadMatchItems(item: String, maxNumItems: Int): MutableCollection<String?> {
        val array = this.historySortedSet.toTypedArray()
        val idx = Arrays.binarySearch(array, item)
        val startIdx = if (idx >= 0) idx else (-idx - 1)
        var count = 0
        val items: MutableCollection<String?> = LinkedList<String?>()
        var i = startIdx
        while ((i < array.size) && (count++ < maxNumItems)) {
            val potentialItem = array[i]
            if (potentialItem!!.startsWith(item)) {
                items.add(potentialItem)
            } else {
                break
            }
            i++
        }
        return items
    }

    fun addAsRecentOnly(item: String) {
        var entry = this.historyMap.get(item)
        if (entry != null) {
            this.historyTimedSet.remove(entry)
            entry.touch()
            this.historyTimedSet.add(entry)
        } else {
            entry = TimedEntry(item)
            this.historyTimedSet.add(entry)
            this.historyMap.put(item, entry)
            this.historySortedSet.add(item)
            if (this.historyTimedSet.size > this.commonEntriesCapacity) {
                // Most outdated goes last
                val entryToRemove = this.historyTimedSet.last()
                this.historyMap.remove(entryToRemove.value)
                this.historySortedSet.remove(entryToRemove.value)
                this.historyTimedSet.remove(entryToRemove)
            }
        }
    }

    fun addItem(item: String, updateAsRecent: Boolean) {
        val newIndex = this.sequenceIndex + 1

        while (newIndex >= this.historySequence.size) {
            this.historySequence.add(null)
        }
        this.historySequence.set(newIndex, item)
        this.sequenceIndex = newIndex

        val expectedSize = newIndex + 1
        while (this.historySequence.size > expectedSize) {
            this.historySequence.removeAt(expectedSize)
        }

        while (this.historySequence.size > this.sequenceCapacity) {
            this.historySequence.removeAt(0)
            this.sequenceIndex--
        }

        if (updateAsRecent) {
            this.addAsRecentOnly(item)
        }
    }

    private inner class TimedEntry(val value: String) : Comparable<TimedEntry>, Serializable {
        private var timestamp = System.currentTimeMillis()

        fun touch() {
            this.timestamp = System.currentTimeMillis()
        }

        override fun equals(obj: Any?): Boolean {
            val other: TimedEntry = obj as TimedEntry
            return other.value == this.value
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        override fun compareTo(other: TimedEntry): Int {
            if (this == other) {
                return 0
            }
            val time1 = this.timestamp
            val time2 = other.timestamp
            if (time1 > time2) {
                // More recent goes first
                return -1
            } else if (time2 > time1) {
                return +1
            } else {
                val diff = System.identityHashCode(this) - System.identityHashCode(other)
                if (diff == 0) {
                    return +1
                }
                return diff
            }
        }

    }
}
