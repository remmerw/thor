package io.github.remmerw.thor.dom


class CDataSection(document: Document, parent: Node, uid: Long, text: String) :
    CharacterData(document, parent, uid, "#cdata-section", text)
