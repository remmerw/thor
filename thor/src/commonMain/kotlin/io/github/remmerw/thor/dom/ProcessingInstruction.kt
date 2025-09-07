package io.github.remmerw.thor.dom


class ProcessingInstruction(
    model: Model,
    parent: Node,
    uid: Long,
    name: String,
    val data: String // todo test
) : Node(model, parent, uid, name)
