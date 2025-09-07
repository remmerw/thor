package io.github.remmerw.thor.dom


open class Text(document: Document, parent: Node, uid: Long, text: String) :
    CharacterData(document, parent, uid, "#text", text)
