package io.github.remmerw.thor.dom

import org.w3c.dom.Node
import org.w3c.dom.Text

class TextFilter : NodeFilter {
    override fun accept(node: Node): Boolean {
        return node is Text
    }
}
