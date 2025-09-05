package io.github.remmerw.thor.dom

import org.w3c.dom.Document
import org.w3c.dom.ProcessingInstruction

class ProcessingInstructionImpl(
    document: Document,
    uid: Long,
    private var target: String,
    private var data: String?
) : NodeImpl(document, uid, target, PROCESSING_INSTRUCTION_NODE),
    ProcessingInstruction {


    override fun getLocalName(): String {
        return target
    }


    override fun getData(): String? {
        return data
    }

    @Throws(DOMException::class)
    override fun setData(data: String?) {
        this.data = data
    }

    override fun getTarget(): String {
        return target
    }

}
