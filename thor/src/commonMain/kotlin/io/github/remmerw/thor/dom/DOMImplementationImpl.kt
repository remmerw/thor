package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.DocumentType

class DOMImplementationImpl() : DOMImplementation {
    override fun hasFeature(feature: String?, version: String): Boolean {
        return "HTML" == feature && ("2.0" <= version)
    }

    @Throws(DOMException::class)
    override fun createDocumentType(
        qualifiedName: String,
        publicId: String?,
        systemId: String?
    ): DocumentType {
        return DocumentTypeImpl(qualifiedName, publicId, systemId)
    }

    @Throws(DOMException::class)
    override fun createDocument(
        namespaceURI: String?,
        qualifiedName: String?,
        doctype: DocumentType?
    ): Document {
        return HTMLDocumentImpl()
    }

    override fun getFeature(feature: String?, version: String): Any? {
        return if ("HTML" == feature && ("2.0" <= version)) {
            this
        } else {
            null
        }
    }


}
