package io.github.remmerw.thor.dom


class CDataSection(document: Document, uid: Long, text: String) :
    CharacterData(document, uid, "#cdata-section", CDATA_SECTION_NODE, text)
