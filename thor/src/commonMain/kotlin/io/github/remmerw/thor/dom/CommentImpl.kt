package io.github.remmerw.thor.dom

import org.w3c.dom.Comment
import org.w3c.dom.Node.COMMENT_NODE

class CommentImpl(text: String) : CharacterDataImpl(), Comment {
    init {
        textContent = text
    }

    override fun getLocalName(): String? {
        return null
    }

    override fun getNodeName(): String {
        return "#comment"
    }

    override fun getNodeValue(): String? {
        return this.textContent
    }

    override fun setNodeValue(nodeValue: String) {
        this.textContent = nodeValue
    }

    override fun getNodeType(): Short {
        return COMMENT_NODE
    }

}
