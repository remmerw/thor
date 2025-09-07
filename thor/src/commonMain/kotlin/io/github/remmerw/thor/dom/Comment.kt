package io.github.remmerw.thor.dom

class Comment(model: Model, parent: Node, uid: Long, text: String) :
    CharacterData(model, parent, uid, "#comment", text)
