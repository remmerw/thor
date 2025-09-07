package io.github.remmerw.thor.dom

internal class Comment(model: Model, uid: Long, text: String) :
    CharacterData(model, uid, "#comment", text)
