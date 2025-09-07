package io.github.remmerw.thor.dom


class ProcessingInstruction(
    document: Document,
    uid: Long,
    name: String,
    val data: String // todo
) : Node(document, uid, name, PROCESSING_INSTRUCTION_NODE)
