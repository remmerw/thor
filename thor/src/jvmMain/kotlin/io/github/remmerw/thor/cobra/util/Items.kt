package io.github.remmerw.thor.cobra.util

import java.util.WeakHashMap

object Items {
    private val sourceMap: MutableMap<Any?, MutableMap<String?, Any?>?> =
        WeakHashMap<Any?, MutableMap<String?, Any?>?>()

    fun getItem(source: Any?, name: String?): Any? {
        val sm = sourceMap
        synchronized(sm) {
            val itemMap = sm.get(source)
            if (itemMap == null) {
                return null
            }
            return itemMap.get(name)
        }
    }

    fun setItem(source: Any?, name: String?, value: Any?) {
        val sm = sourceMap
        synchronized(sm) {
            var itemMap = sm.get(source)
            if (itemMap == null) {
                itemMap = HashMap<String?, Any?>(1)
                sm.put(source, itemMap)
            }
            itemMap.put(name, value)
        }
    }
}
