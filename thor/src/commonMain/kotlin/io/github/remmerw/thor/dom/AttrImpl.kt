package io.github.remmerw.thor.dom

import org.w3c.dom.Attr
import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node.ATTRIBUTE_NODE
import org.w3c.dom.TypeInfo

class AttrImpl : NodeImpl, Attr {
    private val name: String
    private val specified: Boolean
    private val ownerElement: Element?
    private var value: String?
    private var isId: Boolean

    constructor(
        name: String,
        value: String?,
        specified: Boolean,
        owner: Element,
        isId: Boolean
    ) : super(owner.ownerDocument!!, (owner.ownerDocument!! as DocumentImpl).nextUid()) {
        this.name = name
        this.value = value
        this.specified = specified
        this.ownerElement = owner
        this.isId = isId
    }


    constructor(document: Document, uid: Long, name: String) : super(document, uid) {
        this.name = name
        this.value = ""
        this.specified = false
        this.ownerElement = null
        this.isId = false
    }

    override fun getLocalName(): String {
        return this.name
    }

    override fun getNodeName(): String {
        return this.name
    }

    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        return this.value
    }

    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
        this.value = nodeValue
    }

    override fun getNodeType(): Short {
        return ATTRIBUTE_NODE
    }

    override fun getName(): String {
        return this.name
    }

    override fun getSpecified(): Boolean {
        return this.specified
    }

    override fun getValue(): String? {
        return this.value
    }

    @Throws(DOMException::class)
    override fun setValue(value: String?) {
        this.value = value
    }

    override fun getOwnerElement(): Element? {
        return this.ownerElement
    }

    override fun getSchemaTypeInfo(): TypeInfo? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    override fun isId(): Boolean {
        return this.isId
    }

}
