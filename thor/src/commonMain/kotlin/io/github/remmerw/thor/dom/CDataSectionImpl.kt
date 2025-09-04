package io.github.remmerw.thor.dom

import org.w3c.dom.CDATASection
import org.w3c.dom.Document
import org.w3c.dom.Node.CDATA_SECTION_NODE

class CDataSectionImpl(document: Document, text: String) : TextImpl(document, text), CDATASection {

    override fun getNodeName(): String {
        return "#cdata-section"
    }

    override fun getNodeType(): Short {
        return CDATA_SECTION_NODE
    }
}
