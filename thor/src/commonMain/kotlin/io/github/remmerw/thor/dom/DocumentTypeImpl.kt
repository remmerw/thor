package io.github.remmerw.thor.dom

import org.w3c.dom.Document
import org.w3c.dom.DocumentType
import org.w3c.dom.NamedNodeMap

class DocumentTypeImpl(
    document: Document, uid: Long,
    private val qualifiedName: String,
    private val publicId: String?,
    private val systemId: String?
) : NodeImpl(document, uid, "#document_type", DOCUMENT_TYPE_NODE), DocumentType {

    override fun getLocalName(): String? {
        return null
    }


    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        return null
    }

    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
        // nop
    }

    override fun getName(): String {
        return this.qualifiedName
    }

    override fun getEntities(): NamedNodeMap? {
        // TODO: DOCTYPE declared entities
        return null
    }

    override fun getNotations(): NamedNodeMap? {
        // TODO: DOCTYPE notations
        return null
    }

    override fun getPublicId(): String? {
        return this.publicId
    }

    override fun getSystemId(): String? {
        return this.systemId
    }

    override fun getInternalSubset(): String? {
        // TODO: DOCTYPE internal subset
        return null
    }

}
