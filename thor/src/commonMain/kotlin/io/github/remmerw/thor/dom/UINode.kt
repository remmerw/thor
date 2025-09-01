package io.github.remmerw.thor.dom


/**
 * A UI node abstraction that is used to send notifications back to the UI and
 * to obtain information the DOM needs from the UI (such as image dimensions).
 */
interface UINode {
    fun repaint(modelNode: ModelNode?)

    fun bounds(): Rectangle?

    fun boundsRelativeToBlock(): Rectangle?

    fun focus()

    fun blur()
}
