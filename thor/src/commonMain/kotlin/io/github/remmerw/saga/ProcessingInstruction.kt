package io.github.remmerw.thor.dom


internal class ProcessingInstruction(
    model: Model,
    uid: Long,
    name: String,
    val data: String // todo test
) : Node(model, uid, name)
