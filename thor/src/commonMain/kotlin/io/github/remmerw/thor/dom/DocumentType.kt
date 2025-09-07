package io.github.remmerw.thor.dom


class DocumentType(
    model: Model, uid: Long,
    val qualifiedName: String, // todo test
    val publicId: String?,
    val systemId: String?
) : Node(model, uid, "#document_type")
