package io.github.remmerw.thor.dom


class Element(document: Document, parent: Node, uid: Long, name: String) :
    Node(document, parent, uid, name) {
    private val attributes = mutableMapOf<String, String>()

    fun attributes(): Map<String, String> {
        return attributes
    }

    fun hasAttributes(): Boolean {
        return !attributes.isEmpty()
    }

    fun getAttribute(name: String): String? {
        return attributes[name.lowercase()]
    }


    // todo test
    internal fun removeAttribute(name: String) {
        attributes.remove(name.lowercase())
    }

    fun setAttribute(name: String, value: String) {
        attributes.put(name.lowercase(), value)
    }

}
