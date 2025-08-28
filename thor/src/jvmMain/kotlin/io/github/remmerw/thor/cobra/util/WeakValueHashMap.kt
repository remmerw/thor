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
 * Created on Oct 8, 2005
 */
package io.github.remmerw.thor.cobra.util

import org.eclipse.jdt.annotation.Nullable
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.stream.Collectors

class WeakValueHashMap<K, @Nullable V> : MutableMap<K?, V?> {
    private val map: MutableMap<K?, LocalWeakReference?> = HashMap<K?, LocalWeakReference?>()
    private val queue = ReferenceQueue<V?>()

    override fun size(): Int {
        return this.map.size
    }

    override fun isEmpty(): Boolean {
        return this.map.isEmpty()
    }

    override fun containsKey(key: Any?): Boolean {
        return map.containsKey(key)
    }

    override fun containsValue(value: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun get(key: Any?): V? {
        this.checkQueue()
        val wf = this.map.get(key)
        return if (wf == null) null else wf.get()
    }

    override fun put(key: K?, value: V?): V? {
        this.checkQueue()
        return this.putImpl(key, value)
    }

    private fun putImpl(key: K?, value: V?): V? {
        requireNotNull(value) { "null values not accepted" }
        val ref = LocalWeakReference(key, value)
        val oldWf = this.map.put(key, ref)
        return if (oldWf == null) null else oldWf.get()
    }

    override fun remove(key: Any?): V? {
        this.checkQueue()
        val wf = this.map.remove(key)
        return if (wf == null) null else wf.get()
    }

    override fun putAll(t: MutableMap<out K?, out V?>) {
        this.checkQueue()
        t.forEach { (k: K?, v: V?) -> this.putImpl(k, v) }
    }

    override fun clear() {
        this.checkQueue()
        this.map.clear()
    }

    override fun keySet(): MutableSet<K?> {
        return this.map.keys
    }

    private fun checkQueue() {
        val queue = this.queue
        var ref: LocalWeakReference?
        while (((queue.poll() as LocalWeakReference?).also { ref = it }) != null) {
            this.map.remove(ref!!.key)
        }
    }

    override fun values(): MutableCollection<V?> {
        checkQueue()
        val m =
            this.map.values.stream()
                .map<V?> { t: LocalWeakReference? -> if (t == null) null else t.get() }
                .filter { t: V? -> t != null }
        return m!!.collect(Collectors.toList())
    }

    override fun entrySet(): MutableSet<MutableMap.MutableEntry<K?, V?>?> {
        throw UnsupportedOperationException()
    }

    private inner class LocalWeakReference(/*
    public boolean equals(final Object other) {
      final K target1 = this.get();
      final Object target2 = other instanceof LocalWeakReference ? ((LocalWeakReference) other).get() : null;
      return Objects.equals(target1, target2);
    }

    public int hashCode() {
      final Object target = this.get();
      return target == null ? 0 : target.hashCode();
    }*/val key: K?, target: V?
    ) : WeakReference<V?>(target, queue)
}
