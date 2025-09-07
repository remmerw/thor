package io.github.remmerw.thor.dom


class ProcessingInstruction(
    document: Document,
    uid: Long,
    private var target: String,
    private var data: String?
) : Node(document, uid, target, PROCESSING_INSTRUCTION_NODE) {


    fun getLocalName(): String {
        return target
    }


    fun getData(): String? {
        return data
    }

    @Throws(DOMException::class)
    fun setData(data: String?) {
        this.data = data
    }

    fun getTarget(): String {
        return target
    }


}
