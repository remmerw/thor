package io.github.remmerw.thor.dom


class Element(document: Document, uid: Long, name: String) :
    Node(document, uid, name, ELEMENT_NODE) {
    private val attributes = mutableMapOf<String, String>()

    fun attributes(): Map<String, String> {
        return attributes
    }


    // todo
    fun hasAttributes(): Boolean {
        synchronized(this) {
            return !attributes.isEmpty()
        }
    }

    fun getAttribute(name: String): String? {
        val normalName: String = normalizeAttributeName(name)
        synchronized(this) {
            return attributes[normalName]
        }
    }


    fun getTagName(): String {
        // In HTML, tag names are supposed to be returned in upper-case, but in XHTML they are returned in original case
        // as per https://developer.mozilla.org/en-US/docs/Web/API/Element.tagName
        return this.getNodeName().uppercase()
    }

    fun hasAttribute(name: String): Boolean {
        val normalName: String = normalizeAttributeName(name)
        synchronized(this) {
            val attributes: MutableMap<String, String>? = this.attributes
            return attributes != null && attributes.containsKey(normalName)
        }
    }

    @Throws(DOMException::class)
    fun hasAttributeNS(namespaceURI: String?, localName: String?): Boolean {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    @Throws(DOMException::class)
    fun removeAttribute(name: String) {
        changeAttribute(name, null)
    }


    @Throws(DOMException::class)
    fun removeAttributeNS(namespaceURI: String?, localName: String?) {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }


    @Throws(DOMException::class)
    fun setAttribute(name: String, value: String?) {
        // Convert null to "null" : String.
        // This is how Firefox behaves and is also consistent with DOM 3
        val valueNonNull = if (value == null) "null" else value
        changeAttribute(name, valueNonNull)
    }


    @Throws(DOMException::class)
    fun setAttributeNS(namespaceURI: String?, qualifiedName: String?, value: String?) {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    @Throws(DOMException::class)
    fun setIdAttribute(name: String, isId: Boolean) {
        val normalName: String = normalizeAttributeName(name)
        if ("id" != normalName) {
            throw DOMException(
                DOMException.NOT_SUPPORTED_ERR,
                "IdAttribute can't be anything other than ID"
            )
        }
    }


    @Throws(DOMException::class)
    fun setIdAttributeNS(namespaceURI: String?, localName: String?, isId: Boolean) {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }


    fun getLocalName(): String? {
        return this.getNodeName()
    }


    private fun changeAttribute(name: String, newValue: String?): String? {
        val normalName: String = normalizeAttributeName(name)

        var oldValue: String? = null
        synchronized(this) {
            if (newValue == null) {
                oldValue = attributes.remove(normalName)
            } else {

                oldValue = attributes.put(normalName, newValue)
            }
        }


        return oldValue
    }


    companion object {
        private fun isTagName(node: Node, name: String?): Boolean {
            return node.getNodeName().equals(name, ignoreCase = true)
        }

        private fun normalizeAttributeName(name: String): String {
            return name.lowercase()
        }
    }
}
