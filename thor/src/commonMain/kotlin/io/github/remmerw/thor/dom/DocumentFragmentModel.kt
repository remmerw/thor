package io.github.remmerw.thor.dom

import org.w3c.dom.DocumentFragment
import org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE

class DocumentFragmentModel : ElementImpl("#document-fragment"), DocumentFragment {

    override fun getNodeType(): Short {
        return DOCUMENT_FRAGMENT_NODE
    }

    override fun getId(): String? {
        TODO("Not yet implemented")
    }

}
