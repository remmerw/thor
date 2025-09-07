package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.MutableStateFlow

abstract class CharacterData(
    model: Model,
    parent: Node,
    uid: Long,
    name: String,
    text: String
) :
    Node(model, parent, uid, name) {

    val data = MutableStateFlow(text)

    fun getData(): String {
        return data.value
    }

    override fun toString(): String {
        var someText = this.getData()
        if (someText.length > 32) {
            someText = someText.substring(0, 29) + "..."
        }
        val length = someText.length
        return this.name + "[length=" + length + ",text=" + someText + "]"
    }
}
