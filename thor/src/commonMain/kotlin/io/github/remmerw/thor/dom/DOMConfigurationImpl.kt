package io.github.remmerw.thor.dom

import org.w3c.dom.DOMConfiguration
import org.w3c.dom.DOMException
import org.w3c.dom.DOMStringList

class DOMConfigurationImpl : DOMConfiguration {
    private val parameters: MutableMap<String, Any> = HashMap()

    @Throws(DOMException::class)
    override fun setParameter(name: String, value: Any) {
        synchronized(this) {
            this.parameters.put(name, value)
        }
    }

    @Throws(DOMException::class)
    override fun getParameter(name: String): Any? {
        synchronized(this) {
            return this.parameters[name]
        }
    }

    override fun canSetParameter(name: String?, value: Any?): Boolean {
        // TODO
        return true
    }

    override fun getParameterNames(): DOMStringList {
        synchronized(this) {
            return DOMStringListImpl(parameters.keys.toList())
        }
    }
}
