package io.github.remmerw.thor.dom

import org.w3c.dom.Comment
import org.w3c.dom.Document

class CommentImpl(document: Document, uid: Long, text: String) :
    CharacterDataImpl(document, uid, "#comment", COMMENT_NODE), Comment {
    init {
        textContent = text
    }

    override fun getLocalName(): String? {
        return null
    }

}
