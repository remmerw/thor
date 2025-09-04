package io.github.remmerw.thor.dom

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import cz.vutbr.web.css.NodeData
import cz.vutbr.web.css.RuleSet
import cz.vutbr.web.css.Selector
import cz.vutbr.web.css.StyleSheet
import cz.vutbr.web.domassign.AnalyzerUtil
import io.github.remmerw.thor.style.CSSUtilities
import io.github.remmerw.thor.style.StyleElements
import org.w3c.dom.Attr
import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.Node.ELEMENT_NODE
import org.w3c.dom.NodeList
import org.w3c.dom.TypeInfo
import java.util.LinkedList
import java.util.Locale

class ElementImpl(document: Document, private val type: ElementType) :
    NodeImpl(document), ElementModel {

    private val attributes = mutableStateMapOf<String, String>()
    private val properties = mutableStateMapOf<String, String>()

    override fun attributes(): SnapshotStateMap<String, String> {
        return attributes
    }

    override fun properties(): SnapshotStateMap<String, String> {
        return properties
    }

    override fun elementType(): ElementType {
        return type
    }

    fun setProperty(name: String, value: String?) {
        if (value != null) {
            properties.put(name, value)
        }
    }

    fun finish() {
        synchronized(this) {
            val data = getNodeData(null)
            if (data != null) {
                HtmlStyles.cssProperties(data, this)
            }
        }
    }

    protected fun getNodeData(psuedoElement: Selector.PseudoElementType?): NodeData? {
        // The analyzer needs the tree lock, when traversing the DOM.
        // To break deadlocks, we take the tree lock before taking the element lock (priority based dead-lock break).

        synchronized(this) {

            val doc = this.ownerDocument as DocumentImpl


            val ruleSets = ArrayList<RuleSet>(2)
            val attributeStyle = StyleElements.convertAttributesToStyles(this)
            if (attributeStyle != null && attributeStyle.isNotEmpty()) {
                ruleSets.add(attributeStyle[0] as RuleSet)
            }

            val inlineStyle = inlineStyle()
            if (inlineStyle != null && inlineStyle.isNotEmpty()) {
                ruleSets.add(inlineStyle[0] as RuleSet)
            }

            val cachedRules = AnalyzerUtil.getApplicableRules(
                this,
                doc.getClassifiedRules(),
                if (ruleSets.isNotEmpty()) ruleSets.toTypedArray<RuleSet?>() else null
            )


            val nodeData = AnalyzerUtil.getElementStyle(
                this,
                psuedoElement,
                doc.matcher,
                HtmlStyles.elementMatchCondition,
                cachedRules
            )
            val parent = this.parentNode
            if ((parent != null) && (parent is ElementImpl)) {
                nodeData.inheritFrom(parent.getNodeData(psuedoElement))
                nodeData.concretize()
            }

            return nodeData
        }

    }

    protected fun inlineStyle(): StyleSheet? {
        val style = this.getAttribute("style")
        if (style != null && style.isNotEmpty()) {
            return CSSUtilities.parseInlineStyle(style, null, this, true)
        }
        return null
    }

    override fun getAttributes(): NamedNodeMap {
        synchronized(this) {
            return NamedNodeMapImpl(this, this.attributes)
        }
    }

    override fun hasAttributes(): Boolean {
        synchronized(this) {
            return !attributes.isEmpty()
        }
    }

    override fun equalAttributes(arg: Node?): Boolean {
        if (arg is ElementImpl) {
            synchronized(this) {
                val attrs1: MutableMap<String, String> = this.attributes
                val attrs2: MutableMap<String, String> = arg.attributes
                return attrs1 == attrs2
            }
        } else {
            return false
        }
    }

    override fun getAttribute(name: String): String? {
        val normalName: String = normalizeAttributeName(name)
        synchronized(this) {
            return attributes[normalName]
        }
    }

    private fun getAttr(normalName: String, value: String?): Attr {
        return AttrImpl(
            normalName, value,
            true, this, "id" == normalName
        )
    }

    override fun getAttributeNode(name: String): Attr? {
        val normalName: String = normalizeAttributeName(name)
        synchronized(this) {
            val value = this.attributes[normalName]
            return if (value == null) null else this.getAttr(normalName, value)
        }
    }

    @Throws(DOMException::class)
    override fun getAttributeNodeNS(namespaceURI: String?, localName: String?): Attr? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    @Throws(DOMException::class)
    override fun getAttributeNS(namespaceURI: String?, localName: String?): String {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    override fun getElementsByTagName(classNames: String): NodeList {
        val matchesAll = "*" == classNames
        val descendents: MutableList<Node> = LinkedList<Node>()


        this.nodes().forEach { nodeModel ->
            val child = nodeModel
            if (child is Element) {
                if (matchesAll || isTagName(child, classNames)) {
                    descendents.add(child)
                }
                val sublist = child.getElementsByTagName(classNames)
                val length = sublist.length
                for (idx in 0..<length) {
                    descendents.add(sublist.item(idx))
                }
            }
        }

        return NodeListImpl(descendents)
    }

    @Throws(DOMException::class)
    override fun getElementsByTagNameNS(namespaceURI: String?, localName: String?): NodeList {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    override fun getSchemaTypeInfo(): TypeInfo? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    override fun getTagName(): String {
        // In HTML, tag names are supposed to be returned in upper-case, but in XHTML they are returned in original case
        // as per https://developer.mozilla.org/en-US/docs/Web/API/Element.tagName
        return this.nodeName.uppercase(Locale.getDefault())
    }

    override fun hasAttribute(name: String): Boolean {
        val normalName: String = normalizeAttributeName(name)
        synchronized(this) {
            val attributes: MutableMap<String, String>? = this.attributes
            return attributes != null && attributes.containsKey(normalName)
        }
    }

    @Throws(DOMException::class)
    override fun hasAttributeNS(namespaceURI: String?, localName: String?): Boolean {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    @Throws(DOMException::class)
    override fun removeAttribute(name: String) {
        changeAttribute(name, null)
    }

    @Throws(DOMException::class)
    override fun removeAttributeNode(oldAttr: Attr): Attr? {
        val attrName = oldAttr.name
        val oldValue = changeAttribute(attrName, null)

        val normalName: String = normalizeAttributeName(attrName)
        return if (oldValue == null) null else this.getAttr(normalName, oldValue)
    }


    @Throws(DOMException::class)
    override fun removeAttributeNS(namespaceURI: String?, localName: String?) {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }


    @Throws(DOMException::class)
    override fun setAttribute(name: String, value: String?) {
        // Convert null to "null" : String.
        // This is how Firefox behaves and is also consistent with DOM 3
        val valueNonNull = if (value == null) "null" else value
        changeAttribute(name, valueNonNull)
    }

    @Throws(DOMException::class)
    override fun setAttributeNode(newAttr: Attr): Attr {
        changeAttribute(newAttr.name, newAttr.value)

        return newAttr
    }

    @Throws(DOMException::class)
    override fun setAttributeNodeNS(newAttr: Attr?): Attr? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    @Throws(DOMException::class)
    override fun setAttributeNS(namespaceURI: String?, qualifiedName: String?, value: String?) {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }

    @Throws(DOMException::class)
    override fun setIdAttribute(name: String, isId: Boolean) {
        val normalName: String = normalizeAttributeName(name)
        if ("id" != normalName) {
            throw DOMException(
                DOMException.NOT_SUPPORTED_ERR,
                "IdAttribute can't be anything other than ID"
            )
        }
    }

    @Throws(DOMException::class)
    override fun setIdAttributeNode(idAttr: Attr, isId: Boolean) {
        val normalName: String = normalizeAttributeName(idAttr.name)
        if ("id" != normalName) {
            throw DOMException(
                DOMException.NOT_SUPPORTED_ERR,
                "IdAttribute can't be anything other than ID"
            )
        }
    }

    @Throws(DOMException::class)
    override fun setIdAttributeNS(namespaceURI: String?, localName: String?, isId: Boolean) {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported")
    }


    override fun getLocalName(): String? {
        return this.nodeName
    }


    override fun getNodeName(): String {
        return this.type.name
    }

    override fun getNodeType(): Short {
        return ELEMENT_NODE
    }


    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        return null
    }


    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
        // nop
    }


    private fun changeAttribute(name: String, newValue: String?): String? {
        val normalName: String = normalizeAttributeName(name)

        var oldValue: String? = null
        synchronized(this) {
            if (newValue == null) {
                oldValue = attributes.remove(normalName)
            } else {

                oldValue = attributes.put(normalName, newValue)
            }
        }


        return oldValue
    }


    companion object {
        protected fun isTagName(node: Node, name: String?): Boolean {
            return node.nodeName.equals(name, ignoreCase = true)
        }

        @JvmStatic
        protected fun normalizeAttributeName(name: String): String {
            return name.lowercase(Locale.getDefault())
        }
    }
}
