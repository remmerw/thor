package io.github.remmerw.thor.cobra.html.dom

import org.w3c.dom.DOMException
import org.w3c.dom.Node
import org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE
import org.w3c.dom.ProcessingInstruction

class HTMLProcessingInstruction(
    private var target: String,
    private var data: String?
) : NodeImpl(),
    ProcessingInstruction, Cloneable {
    override fun createSimilarNode(): Node {
        return clone() as Node
    }

    override fun getLocalName(): String {
        return target
    }

    override fun getNodeName(): String {
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

    override fun toString(): String {
        return "<?" + target + " " + data + ">"
    }
}
