package io.github.remmerw.thor.cobra.html.dom

import org.w3c.dom.Comment
import org.w3c.dom.Node
import org.w3c.dom.Node.COMMENT_NODE

class CommentImpl(text: String) : CharacterDataImpl(text), Comment {
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
        this.setTextContent(nodeValue)
    }

    override fun getNodeType(): Short {
        return COMMENT_NODE
    }

    override fun createSimilarNode(): Node {
        return CommentImpl(this.text)
    }
}
