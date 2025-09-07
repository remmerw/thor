package io.github.remmerw.thor.dom


class DocumentType(
    document: Document,
    uid: Long,
    val qualifiedName: String, // todo
    val publicId: String?,
    val systemId: String?
) : Node(document, uid, "#document_type", DOCUMENT_TYPE_NODE)
