package io.github.remmerw.thor.dom


class ProcessingInstruction(
    document: Document,
    parent: Node,
    uid: Long,
    name: String,
    val data: String // todo
) : Node(document, parent, uid, name, PROCESSING_INSTRUCTION_NODE)
