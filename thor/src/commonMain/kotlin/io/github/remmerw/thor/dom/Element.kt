package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.MutableStateFlow


internal class Element(model: Model, uid: Long, name: String) : Node(model, uid, name) {
    val attributes = MutableStateFlow(mutableMapOf<String, String>())

    fun attributes(): Map<String, String> {
        return attributes.value
    }

    fun hasAttributes(): Boolean {
        return !attributes.value.isEmpty()
    }

    fun getAttribute(name: String): String? {
        return attributes.value[name.lowercase()]
    }


    // todo test
    internal fun removeAttribute(name: String) {
        attributes.value.remove(name.lowercase()) // todo fix
    }

    fun setAttribute(name: String, value: String) {
        attributes.value.put(name.lowercase(), value)  // todo fix
    }

    override fun debug() {
        if (children.value.isEmpty()) {
            println("<$name/>")
            if (hasAttributes()) {
                println("Attributes : " + attributes().toString())
            }
        } else {
            println("<$name>")
            if (hasAttributes()) {
                println("Attributes : " + attributes().toString())
            }
            children.value.forEach { entity ->
                model!!.node(entity).debug()
            }
            println("</$name")
        }
    }
}
