/*
 GNU LESSER GENERAL PUBLIC LICENSE
 Copyright (C) 2006 The Lobo Project

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Oct 29, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.parser.HtmlParser
import io.github.remmerw.thor.cobra.util.Strings
import org.w3c.dom.Attr
import org.w3c.dom.Comment
import org.w3c.dom.DOMException
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.Node.ELEMENT_NODE
import org.w3c.dom.NodeList
import org.w3c.dom.Text
import org.w3c.dom.TypeInfo
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventException
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import java.util.LinkedList
import java.util.Locale

abstract class ElementImpl(private val name: String) : NodeImpl(), Element, EventTarget {
    protected var attributes: MutableMap<String, String>? = null


    override fun getAttributes(): NamedNodeMap {
        synchronized(this) {
            var attrs: MutableMap<String, String>? = this.attributes
            // TODO: Check if NamedNodeMapImpl can be changed to dynamically query the attributes field
            //       instead of keeping a reference to it. This will allow the NamedNodeMap to be live as well
            //       as avoid allocating of a HashMap here when attributes are empty.
            if (attrs == null) {
                attrs = HashMap<String, String>()
                this.attributes = attrs
            }
            return NamedNodeMapImpl(this, this.attributes!!)
        }
    }

    override fun hasAttributes(): Boolean {
        synchronized(this) {
            val attrs: MutableMap<String, String>? = this.attributes
            return attrs != null && !attrs.isEmpty()
        }
    }

    override fun equalAttributes(arg: Node?): Boolean {
        if (arg is ElementImpl) {
            synchronized(this) {
                var attrs1: MutableMap<String, String>? = this.attributes
                if (attrs1 == null) {
                    attrs1 = mutableMapOf<String, String>()
                }
                var attrs2: MutableMap<String, String>? = arg.attributes
                if (attrs2 == null) {
                    attrs2 = mutableMapOf<String, String>()
                }
                return attrs1 == attrs2
            }
        } else {
            return false
        }
    }

    override fun getAttribute(name: String): String? {
        val normalName: String = normalizeAttributeName(name)
        synchronized(this) {
            val attributes: MutableMap<String, String>? = this.attributes
            return if (attributes == null) null else attributes.get(normalName)
        }
    }

    private fun getAttr(normalName: String, value: String?): Attr {
        // TODO: "specified" attributes
        return AttrImpl(normalName, value, true, this, "id" == normalName)
    }

    override fun getAttributeNode(name: String): Attr? {
        val normalName: String = normalizeAttributeName(name)
        synchronized(this) {
            val attributes: MutableMap<String, String>? = this.attributes
            val value = if (attributes == null) null else attributes.get(normalName)
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
        synchronized(this.treeLock) {
            val nl = this.nodeList
            if (nl != null) {
                val i = nl.iterator()
                while (i.hasNext()) {
                    val child = i.next()
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
        return this.nodeName!!.uppercase(Locale.getDefault())
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

    /*
  protected void assignAttributeField(final String normalName, final String value) {
    // Note: overriders assume that processing here is only done after
    // checking attribute names, i.e. they may not call the super
    // implementation if an attribute is already taken care of.

    // TODO: Need to move this to a separate function, similar to updateIdMap()
    // TODO: Need to update the name map, whenever attachment changes
    if (isAttachedToDocument()) {
      final HTMLDocumentImpl document = (HTMLDocumentImpl) this.document;
      if ("name".equals(normalName)) {
        final String oldName = this.getAttribute("name");
        if (oldName != null) {
          document.removeNamedItem(oldName);
        }
        document.setNamedItem(value, this);
      }
    }
  }*/
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

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#getLocalName()
     */
    override fun getLocalName(): String? {
        return this.nodeName!!
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#getNodeName()
     */
    override fun getNodeName(): String {
        return this.name
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#getNodeType()
     */
    override fun getNodeType(): Short {
        return ELEMENT_NODE
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#getNodeValue()
     */
    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        return null
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.domimpl.NodeImpl#setNodeValue(java.lang.String)
     */
    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
        // nop
    }

    /**
     * Gets inner text of the element, possibly including text in comments. This
     * can be used to get Javascript code out of a SCRIPT element.
     *
     * @param includeComment
     */
    protected fun getRawInnerText(includeComment: Boolean): String {
        synchronized(this.treeLock) {
            val nl = this.nodeList
            if (nl != null) {
                val i = nl.iterator()
                var sb: StringBuffer? = null
                while (i.hasNext()) {
                    val node: Any? = i.next()
                    if (node is Text) {
                        val txt = node.nodeValue
                        if ("" != txt) {
                            if (sb == null) {
                                sb = StringBuffer()
                            }
                            sb.append(txt)
                        }
                    } else if (node is ElementImpl) {
                        val txt = node.getRawInnerText(includeComment)
                        if ("" != txt) {
                            if (sb == null) {
                                sb = StringBuffer()
                            }
                            sb.append(txt)
                        }
                    } else if (includeComment && (node is Comment)) {
                        val txt = node.nodeValue
                        if ("" != txt) {
                            if (sb == null) {
                                sb = StringBuffer()
                            }
                            sb.append(txt)
                        }
                    }
                }
                return if (sb == null) "" else sb.toString()
            } else {
                return ""
            }
        }
    }


    fun setInnerText(newText: String?) {
        // TODO: Is this check for owner document really required?
        val document = this.document
        if (document == null) {
            this.warn("setInnerText(): Element " + this + " does not belong to a document.")
            return
        }

        removeAllChildrenImpl()

        // Create node and call appendChild outside of synchronized block.
        val textNode: Node? = document.createTextNode(newText)
        this.appendChild(textNode)
    }

    override fun createSimilarNode(): Node? {
        val doc = this.document as HTMLDocumentImpl?
        return if (doc == null) null else doc.createElement(this.tagName)
    }

    override fun htmlEncodeChildText(text: String): String? {
        if (HtmlParser.isDecodeEntities(this.name)) {
            return Strings.strictHtmlEncode(text, false)
        } else {
            return text
        }
    }

    /**
     * To be overridden by Elements that need a notification of attribute changes.
     *
     *
     * This is called only when the element is attached to a document at the time
     * the attribute is changed. If an attribute is changed while not attached to
     * a document, this function is *not* called when the element is attached to a
     * document. We chose this design because it covers our current use cases
     * well.
     *
     *
     * If, in the future, a notification is always desired then the design can be
     * altered easily later.
     *
     * @param name     normalized name
     * @param oldValue null, if the attribute was absent
     * @param newValue null, if the attribute is now removed
     */
    protected open fun handleAttributeChanged(name: String, oldValue: String?, newValue: String?) {
        // TODO: Need to move this to a separate function, similar to updateIdMap()
        // TODO: Need to update the name map, whenever attachment changes
        val document = this.document as HTMLDocumentImpl
        if ("name" == name) {
            if (oldValue != null) {
                document.removeNamedItem(oldValue)
            }
            document.setNamedItem(newValue, this)
        }
    }

    /**
     * changes an attribute to the specified value. If the specified value is
     * null, the attribute is removed
     *
     * @return the old attribute value. null if not set previously.
     */
    private fun changeAttribute(name: String, newValue: String?): String? {
        val normalName: String = normalizeAttributeName(name)

        var oldValue: String? = null
        synchronized(this) {
            if (newValue == null) {
                if (attributes != null) {
                    oldValue = attributes!!.remove(normalName)
                }
            } else {
                if (attributes == null) {
                    attributes = HashMap<String, String>(2)
                }

                oldValue = attributes!!.put(normalName, newValue)
            }
        }

        if ("id" == normalName) {
            updateIdMap(oldValue, newValue)
        }

        if (isAttachedToDocument) {
            handleAttributeChanged(normalName, oldValue, newValue)
        }

        return oldValue
    }

    abstract fun getId(): String?
    fun updateIdMap(isAttached: Boolean) {
        if (hasAttribute("id")) {
            val id = getId()!!
            if (isAttached) {
                (document as HTMLDocumentImpl).setElementById(id, this)
            } else {
                (document as HTMLDocumentImpl).removeElementById(getId())
            }
        }
    }

    private fun updateIdMap(oldIdValue: String?, newIdValue: String?) {
        if (isAttachedToDocument && oldIdValue != newIdValue) {
            if (oldIdValue != null) {
                (document as HTMLDocumentImpl).removeElementById(oldIdValue)
            }
            if (newIdValue != null) {
                (document as HTMLDocumentImpl).setElementById(newIdValue, this)
            }
        }
    }

    val firstElementChild: Element?
        // TODO: GH #88 Need to implement these for Document and DocumentFragment as part of ParentNode API
        get() {
            val nl = this.nodeList!!
            for (n in nl) {
                if (n is Element) {
                    return n
                }
            }

            return null
        }

    val lastElementChild: Element?
        get() {
            val nl = this.nodeList!!
            val N = nl.size
            for (i in N - 1 downTo 0) {
                val n = nl.get(i)
                if (n is Element) {
                    return n
                }
            }

            return null
        }

    val childElementCount: Int
        get() {
            val nl = this.nodeList!!
            var count = 0
            for (n in nl) {
                if (n is Element) {
                    count++
                }
            }

            return count
        }

    override fun addEventListener(type: String?, listener: EventListener?, useCapture: Boolean) {
        // TODO Auto-generated method stub
        println("TODO: addEventListener() in ElementImpl")
    }

    override fun removeEventListener(type: String?, listener: EventListener?, useCapture: Boolean) {
        // TODO Auto-generated method stub
        println("TODO: removeEventListener() in ElementImpl")
    }

    @Throws(EventException::class)
    override fun dispatchEvent(evt: Event?): Boolean {
        // TODO Auto-generated method stub
        println("TODO: dispatchEvent() in ElementImpl")
        return false
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
