package io.github.remmerw.thor.cobra.html.dom

import org.mozilla.javascript.Function

/**
 * Implements common functionality of most elements.
 */
open class HTMLAbstractUIElement(name: String) : HTMLElementImpl(name) {

    private var functionByAttribute: MutableMap<String?, Function?>? = null

    open fun focus() {
        val node = this.uINode
        if (node != null) {
            node.focus()
        }
    }

    open fun blur() {
        val node = this.uINode
        if (node != null) {
            node.blur()
        }
    }


    override fun handleAttributeChanged(name: String, oldValue: String?, newValue: String?) {
        super.handleAttributeChanged(name, oldValue, newValue)
        if (name.startsWith("on")) {
            synchronized(this) {
                val fba = this.functionByAttribute
                if (fba != null) {
                    fba.remove(name)
                }
            }
        }
    }
}
