package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.DocumentType
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node.DOCUMENT_TYPE_NODE

class DocumentTypeImpl(
    document: Document, uid: Long,
    private val qualifiedName: String,
    private val publicId: String?,
    private val systemId: String?
) : NodeImpl(document, uid), DocumentType {

    override fun getLocalName(): String? {
        return null
    }

    override fun getNodeName(): String {
        return this.name
    }

    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        return null
    }

    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
        // nop
    }

    override fun getNodeType(): Short {
        return DOCUMENT_TYPE_NODE
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
