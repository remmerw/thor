package io.github.remmerw.thor.dom


class CDataSection(model: Model, parent: Node, uid: Long, text: String) :
    CharacterData(model, parent, uid, "#cdata-section", text)
