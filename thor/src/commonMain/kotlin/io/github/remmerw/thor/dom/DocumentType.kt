package io.github.remmerw.thor.dom


class DocumentType(
    model: Model,
    parent: Node,
    uid: Long,
    val qualifiedName: String, // todo test
    val publicId: String?,
    val systemId: String?
) : Node(model, parent, uid, "#document_type")
