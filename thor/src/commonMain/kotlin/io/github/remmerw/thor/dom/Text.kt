package io.github.remmerw.thor.dom


open class Text(model: Model, parent: Node, uid: Long, text: String) :
    CharacterData(model, parent, uid, "#text", text)
