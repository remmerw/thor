package io.github.remmerw.thor.cobra.html.dom

import org.w3c.dom.CDATASection
import org.w3c.dom.Node.CDATA_SECTION_NODE

class CDataSectionImpl(text: String) : TextImpl(text), CDATASection {

    override fun getNodeName(): String {
        return "#cdata-section"
    }

    override fun getNodeType(): Short {
        return CDATA_SECTION_NODE
    }
}
