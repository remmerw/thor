package io.github.remmerw.thor.dom

class TextFilter : NodeFilter {
    override fun accept(node: Node): Boolean {
        return node is Text
    }
}
