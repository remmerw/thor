package io.github.remmerw.thor.dom

/**
 * Implements common functionality of most elements.
 */
open class HTMLAbstractUIElement(name: String) : HTMLElementModel(name) {

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


}
