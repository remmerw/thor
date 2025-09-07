package io.github.remmerw.thor.dom


internal class Text(model: Model, uid: Long, text: String) :
    CharacterData(model, uid, "#text", text)
