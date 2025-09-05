package io.github.remmerw.thor.dom

import org.w3c.dom.Document
import org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE
import org.w3c.dom.ProcessingInstruction

class ProcessingInstructionImpl(
    document: Document,
    uid: Long,
    private var target: String,
    private var data: String?
) : NodeImpl(document, uid, target),
    ProcessingInstruction, Cloneable {


    override fun getLocalName(): String {
        return target
    }


    override fun getNodeType(): Short {
        return PROCESSING_INSTRUCTION_NODE
    }

    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        return data
    }

    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
        this.data = nodeValue
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

    public override fun clone(): Any {
        try {
            return super.clone()
        } catch (e: CloneNotSupportedException) {
            throw IllegalStateException(e)
        }
    }

}
