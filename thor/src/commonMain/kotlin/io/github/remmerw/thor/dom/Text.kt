package io.github.remmerw.thor.dom


open class Text(document: Document, uid: Long, text: String = "") :
    CharacterData(document, uid, "#text", TEXT_NODE, text)
