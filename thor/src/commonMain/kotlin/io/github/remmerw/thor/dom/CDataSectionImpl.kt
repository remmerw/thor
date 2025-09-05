package io.github.remmerw.thor.dom

import org.w3c.dom.CDATASection
import org.w3c.dom.Document
import org.w3c.dom.Text

class CDataSectionImpl(document: Document, uid: Long, text: String) :
    CharacterDataImpl(document, uid, "#cdata-section", CDATA_SECTION_NODE, text), CDATASection {


    override fun getNodeType(): Short {
        return CDATA_SECTION_NODE
    }

    override fun getLocalName(): String? {
        TODO("Not yet implemented")
    }

    override fun splitText(p0: Int): Text? {
        TODO("Not yet implemented")
    }

    override fun isElementContentWhitespace(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getWholeText(): String? {
        TODO("Not yet implemented")
    }

    override fun replaceWholeText(p0: String?): Text? {
        TODO("Not yet implemented")
    }

    override fun textContent(): String {
        return text
    }
}
