package io.github.remmerw.thor.dom

class Comment(document: Document, uid: Long, text: String) :
    CharacterData(document, uid, "#comment", COMMENT_NODE, text)
