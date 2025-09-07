package io.github.remmerw.thor.dom


class ProcessingInstruction(
    document: Document,
    parent: Node,
    uid: Long,
    name: String,
    val data: String // todo test
) : Node(document, parent, uid, name)
