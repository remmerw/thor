package io.github.remmerw.thor.dom

import org.w3c.dom.Node

fun interface NodeVisitor {
    fun visit(node: Node)
}
