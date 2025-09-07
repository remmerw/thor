package io.github.remmerw.thor.dom


class DocumentType(
    document: Document,
    parent: Node,
    uid: Long,
    val qualifiedName: String, // todo
    val publicId: String?,
    val systemId: String?
) : Node(document, parent, uid, "#document_type", DOCUMENT_TYPE_NODE)
