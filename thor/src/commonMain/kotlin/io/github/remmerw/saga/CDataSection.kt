package io.github.remmerw.thor.dom


internal class CDataSection(model: Model, uid: Long, text: String) :
    CharacterData(model, uid, "#cdata-section", text)
