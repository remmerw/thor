package io.github.remmerw.thor.dom

class Comment(document: Document, parent: Node, uid: Long, text: String) :
    CharacterData(document, parent, uid, "#comment", COMMENT_NODE, text)
