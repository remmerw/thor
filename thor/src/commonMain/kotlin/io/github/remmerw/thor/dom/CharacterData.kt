package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.MutableStateFlow

abstract class CharacterData(
    document: Document,
    uid: Long,
    name: String,
    type: Short,
    var text: String = ""
) :
    Node(document, uid, name, type) {

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
        return this.getNodeName() + "[length=" + length + ",text=" + someText + "]"
    }
}
