package io.github.remmerw.thor.cobra.html.gui

import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl

internal class DocumentNotification(val type: Int, val node: NodeImpl?) {
    override fun toString(): String {
        return "DocumentNotification[type=" + this.type + ",node=" + this.node + "]"
    }

    companion object {
        const val LOOK: Int = 0
        const val POSITION: Int = 1
        const val SIZE: Int = 2
        const val GENERIC: Int = 3
    }
}
