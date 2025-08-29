/*    GNU LESSER GENERAL PUBLIC LICENSE
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
 * Created on Sep 3, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import cz.vutbr.web.css.MatchCondition
import cz.vutbr.web.css.NodeData
import cz.vutbr.web.css.RuleSet
import cz.vutbr.web.css.Selector
import cz.vutbr.web.css.StyleSheet
import cz.vutbr.web.css.TermList
import cz.vutbr.web.csskit.MatchConditionOnElements
import cz.vutbr.web.domassign.Analyzer.OrderedRule
import cz.vutbr.web.domassign.AnalyzerUtil
import io.github.remmerw.thor.cobra.html.FormInput
import io.github.remmerw.thor.cobra.html.parser.HtmlParser
import io.github.remmerw.thor.cobra.html.style.CSS2PropertiesContext
import io.github.remmerw.thor.cobra.html.style.CSSUtilities
import io.github.remmerw.thor.cobra.html.style.ComputedJStyleProperties
import io.github.remmerw.thor.cobra.html.style.JStyleProperties
import io.github.remmerw.thor.cobra.html.style.LocalJStyleProperties
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.html.style.StyleElements
import io.github.remmerw.thor.cobra.html.style.StyleSheetRenderState
import io.github.remmerw.thor.cobra.util.Strings
import org.w3c.css.sac.InputSource
import org.w3c.dom.DOMException
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLFormElement
import org.xml.sax.SAXException
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.util.Arrays
import java.util.Locale
import java.util.StringTokenizer
import java.util.logging.Level
import kotlin.concurrent.Volatile

open class HTMLElementImpl : ElementImpl, HTMLElement, CSS2PropertiesContext {
    @Volatile
    private var currentStyle: JStyleProperties? = null
    private var cachedNodeData: NodeData? = null

    @Volatile
    private var cachedRules: Array<OrderedRule>? = null

    /**
     * True if there is any hover rule that is applicable to this element or descendants.
     * This is a very crude measure, but highly effective with most web-sites.
     */
    private var cachedHasHoverRule = false
    private var beforeNode: GeneratedElement? = null
    private var afterNode: GeneratedElement? = null
    private var isMouseOver = false

    // TODO: noStyleSheet is not used. Consider removing.
    constructor(name: String, noStyleSheet: Boolean) : super(name)

    constructor(name: String) : super(name)

    protected fun forgetLocalStyle() {
        synchronized(this) {
            //TODO to be reconsidered in issue #41
            this.currentStyle = null
            this.cachedNodeData = null
        }
    }

    fun forgetStyle(deep: Boolean) {
        // TODO: OPTIMIZATION: If we had a ComputedStyle map in
        // window (Mozilla model) the map could be cleared in one shot.
        synchronized(treeLock) {
            //TODO to be reconsidered in issue #41
            /*
      this.currentStyleDeclarationState = null;
      this.computedStyles = null;
      this.isHoverStyle = null;
      this.hasHoverStyleByElement = null;
       */
            this.currentStyle = null
            this.cachedRules = null
            this.cachedNodeData = null
            if (deep) {
                val nl = this.nodeList
                if (nl != null) {
                    val i = nl.iterator()
                    while (i.hasNext()) {
                        val node: Any? = i.next()
                        if (node is HTMLElementImpl) {
                            node.forgetStyle(deep)
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the style object associated with the element. It may return null only
     * if the type of element does not handle stylesheets.
     * Hiding from JS because it is not a standard property. See GH #141
     */

    open fun getCurrentStyle(): JStyleProperties {
        synchronized(this) {
            if (currentStyle != null) {
                return currentStyle!!
            }
            currentStyle = ComputedJStyleProperties(this, getNodeData(null), true)
            return currentStyle!!
        }
    }

    private fun getNodeData(psuedoElement: Selector.PseudoElementType?): NodeData? {
        // The analyzer needs the tree lock, when traversing the DOM.
        // To break deadlocks, we take the tree lock before taking the element lock (priority based dead-lock break).
        synchronized(this.treeLock) {
            synchronized(this) {
                if (cachedNodeData != null) {
                    return cachedNodeData
                }
                val doc = this.document as HTMLDocumentImpl

                if (cachedRules == null) {
                    val jSheets = ArrayList<RuleSet?>(2)
                    val attributeStyle = StyleElements.convertAttributesToStyles(this)
                    if (attributeStyle != null && attributeStyle.size > 0) {
                        jSheets.add(attributeStyle.get(0) as RuleSet?)
                    }

                    val inlineStyle = this.inlineJStyle
                    if (inlineStyle != null && inlineStyle.size > 0) {
                        jSheets.add(inlineStyle.get(0) as RuleSet?)
                    }

                    cachedRules = AnalyzerUtil.getApplicableRules(
                        this,
                        doc.getClassifiedRules(),
                        if (jSheets.size > 0) jSheets.toTypedArray<RuleSet?>() else null
                    )
                    cachedHasHoverRule = hasHoverRule(cachedRules!!)
                }

                val nodeData = AnalyzerUtil.getElementStyle(
                    this,
                    psuedoElement,
                    doc.matcher,
                    elementMatchCondition,
                    cachedRules
                )
                val parent = this.parentNode
                if ((parent != null) && (parent is HTMLElementImpl)) {
                    nodeData.inheritFrom(parent.getNodeData(psuedoElement))
                    nodeData.concretize()
                }

                this.beforeNode = setupGeneratedNode(
                    doc,
                    nodeData,
                    Selector.PseudoElementType.BEFORE,
                    cachedRules!!,
                    this
                )
                this.afterNode = setupGeneratedNode(
                    doc,
                    nodeData,
                    Selector.PseudoElementType.AFTER,
                    cachedRules!!,
                    this
                )

                cachedNodeData = nodeData
                // System.out.println("In " + this);
                // System.out.println("  Node data: " + nodeData);
                return nodeData
            }
        }
    }


    fun getBeforeNode(): NodeImpl? {
        return beforeNode
    }


    fun getAfterNode(): NodeImpl? {
        return afterNode
    }

    /*
   currentStyle is not a standard property. See GH 141.
  public void setCurrentStyle(final Object value) {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Cannot set currentStyle property");
  }
  */
    var style: Any?
        /**
         * Gets the local style object associated with the element. The properties
         * object returned only includes properties from the local style attribute. It
         * may return null only if the type of element does not handle stylesheets.
         */
        get() = LocalJStyleProperties(this)
        set(value) {
            throw DOMException(
                DOMException.NOT_SUPPORTED_ERR,
                "Cannot set style property"
            )
        }

    private val inlineJStyle: StyleSheet?
        get() {
            synchronized(this) {
                val style = this.getAttribute("style")
                if ((style != null) && (style.length != 0)) {
                    return CSSUtilities.jParseInlineStyle(style, null, this, true)
                }
            }
            // Synchronization note: Make sure getStyle() does not return multiple values.
            return null
        }

    // TODO hide from JS
    // Chromium(v37) and firefox(v32) do not expose this function
    // couldn't find anything in the standards.
    fun getComputedStyle(pseudoElement: String?): JStyleProperties {
        return ComputedJStyleProperties(
            this,
            getNodeData(getPseudoDeclaration(pseudoElement)),
            false
        )
    }

    override fun getId(): String? {
        TODO("Not yet implemented")
    }

    override fun setId(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getTitle(): String? {
        TODO("Not yet implemented")
    }

    override fun setTitle(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getLang(): String? {
        TODO("Not yet implemented")
    }

    override fun setLang(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getDir(): String? {
        TODO("Not yet implemented")
    }

    override fun setDir(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getClassName(): String {
        val className = this.getAttribute("class")
        // Blank required instead of null.
        return if (className == null) "" else className
    }

    override fun setClassName(className: String?) {
        this.setAttribute("class", className)
    }

    var charset: String?
        get() = this.getAttribute("charset")
        set(charset) {
            this.setAttribute("charset", charset)
        }

    /*
  @Override
  protected void assignAttributeField(final String normalName, final String value) {
    if (!this.notificationsSuspended) {
      this.informInvalidAttibute(normalName);
    } else {
      if ("style".equals(normalName)) {
        this.forgetLocalStyle();
      }
    }
    super.assignAttributeField(normalName, value);
  }*/
    override fun warn(message: String?, err: Throwable?) {
        logger.log(Level.WARNING, message, err)
    }

    override fun warn(message: String?) {
        logger.log(Level.WARNING, message)
    }

    protected fun getAttributeAsInt(name: String, defaultValue: Int): Int {
        val value = this.getAttribute(name)
        try {
            return value!!.toInt()
        } catch (err: Exception) {
            this.warn("Bad integer", err)
            return defaultValue
        }
    }

    fun getAttributeAsBoolean(name: String): Boolean {
        return this.getAttribute(name) != null
    }

    override fun handleAttributeChanged(name: String, oldValue: String?, newValue: String?) {
        super.handleAttributeChanged(name, oldValue, newValue)
        forgetStyle(true)
        this.informInvalidRecursive()
    }

    fun setMouseOver(mouseOver: Boolean) {
        // TODO: Synchronize with treeLock here instead of in invalidateDescendtsForHover?
        if (this.isMouseOver != mouseOver) {
            if (mouseOver) {
                elementMatchCondition.addMatch(this, Selector.PseudoClassType.HOVER)
            } else {
                elementMatchCondition.removeMatch(this, Selector.PseudoClassType.HOVER)
            }
            // Change isMouseOver field before checking to invalidate.
            this.isMouseOver = mouseOver

            // TODO: If informLocalInvalid detects a layout change, then there is no need to do descendant invalidation.

            // Check if descendents are affected (e.g. div:hover a { ... } )
            if (cachedHasHoverRule) {
                this.invalidateDescendentsForHover(mouseOver)
                if (this.hasHoverStyle()) {
                    this.informLocalInvalid()
                }
            }
        }
    }

    /* Not required anymore
  private static boolean isSameNodeData(final NodeData a, final NodeData b) {
    final Collection<String> aProps = a.getPropertyNames();
    final Collection<String> bProps = b.getPropertyNames();
    if (aProps.size() == bProps.size()) {
      for (final String ap : aProps) {
        final Term<?> aVal = a.getValue(ap, true);
        final Term<?> bVal = b.getValue(ap, true);
        if (aVal != null) {
          if (!aVal.equals(bVal)) {
            return false;
          }
        }
        final CSSProperty aProp = a.getProperty(ap);
        final CSSProperty bProp = b.getProperty(ap);
        if (!aProp.equals(bProp)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  */
    private fun invalidateDescendentsForHover(mouseOver: Boolean) {
        synchronized(this.treeLock) {
            if (!mouseOver) {
                val hoverCondition = elementMatchCondition.clone() as MatchConditionOnElements
                hoverCondition.addMatch(this, Selector.PseudoClassType.HOVER)
                invalidateDescendentsForHoverImpl(this, hoverCondition)
            } else {
                invalidateDescendentsForHoverImpl(this, elementMatchCondition)
            }
        }
    }

    private fun invalidateDescendentsForHoverImpl(
        ancestor: HTMLElementImpl?,
        hoverCondition: MatchCondition?
    ) {
        val nodeList = this.nodeList
        if (nodeList != null) {
            val size = nodeList.size
            for (i in 0..<size) {
                val node: Any? = nodeList.get(i)
                if (node is HTMLElementImpl) {
                    val hasMatch = node.hasHoverStyle(ancestor, hoverCondition)
                    if (hasMatch) {
                        node.informLocalInvalid()
                    }
                    node.invalidateDescendentsForHoverImpl(ancestor, hoverCondition)
                }
            }
        }
    }

    // TODO: Cache the result of this
    private fun hasHoverStyle(): Boolean {
        val rules = cachedRules
        if (rules == null) {
            return false
        }
        return AnalyzerUtil.hasPseudoSelector(
            rules,
            this,
            elementMatchCondition,
            Selector.PseudoClassType.HOVER
        )
    }

    // TODO: Cache the result of this
    private fun hasHoverStyle(
        ancestor: HTMLElementImpl?,
        hoverCondition: MatchCondition?
    ): Boolean {
        val rules = cachedRules
        if (rules == null) {
            return false
        }
        val doc = this.document as HTMLDocumentImpl
        return AnalyzerUtil.hasPseudoSelectorForAncestor(
            rules,
            this,
            ancestor,
            doc.matcher,
            hoverCondition,
            Selector.PseudoClassType.HOVER
        )
    }

    val pseudoNames: MutableSet<String?>?
        /**
         * Gets the pseudo-element lowercase names currently applicable to this
         * element. Method must return `null` if there are no such
         * pseudo-elements.
         */
        get() {
            var pnset: MutableSet<String?>? = null
            if (this.isMouseOver) {
                pnset = HashSet<String?>(1)
                pnset.add("hover")
            }
            return pnset
        }

    override fun informInvalid() {
        // This is called when an attribute or child changes.
        // TODO: forgetStyle can call informInvalid() since informInvalid() seems to always follow forgetStyle()
        this.forgetStyle(false)
        super.informInvalid()
    }

    override val documentBaseURI: String?
        get() = TODO("Not yet implemented")

    fun informLocalInvalid() {
        // TODO: forgetStyle can call informInvalid() since informInvalid() seems to always follow forgetStyle()
        //       ^^ Hah, not any more
        val prevStyle: JStyleProperties? = currentStyle
        this.forgetLocalStyle()
        val newStyle = getCurrentStyle()
        if (layoutChanges(prevStyle, newStyle)) {
            super.informInvalid()
        } else {
            super.informLookInvalid()
        }
    }

    // TODO: Use the handleAttributeChanged() system and remove informInvalidAttribute
    /*
  private void informInvalidAttibute(final String normalName) {
    if (isAttachedToDocument()) {
      // This is called when an attribute changes while
      // the element is allowing notifications.
      if ("style".equals(normalName)) {
        this.forgetLocalStyle();
      }

      forgetStyle(true);
      informInvalidRecursive();
    }
  }*/
    private fun informInvalidRecursive() {
        super.informInvalid()
        val nodeList = this.childrenArray
        if (nodeList != null) {
            for (n in nodeList) {
                if (n is HTMLElementImpl) {
                    n.informInvalidRecursive()
                }
            }
        }
    }

    open val formInputs: Array<FormInput?>?
        /**
         * Gets form input due to the current element. It should return
         * `null` except when the element is a form input element.
         */
        get() =// Override in input elements
            null

    private fun classMatch(classTL: String?): Boolean {
        val classNames = this.getClassName()
        if ((classNames == null) || (classNames.length == 0)) {
            return classTL == null
        }
        val tok = StringTokenizer(classNames, " \t\r\n")
        while (tok.hasMoreTokens()) {
            val token = tok.nextToken()
            if (token.lowercase(Locale.getDefault()) == classTL) {
                return true
            }
        }
        return false
    }

    /**
     * Get an ancestor that matches the element tag name given and the style class
     * given.
     *
     * @param elementTL An tag name in lowercase or an asterisk (*).
     * @param classTL   A class name in lowercase.
     */
    fun getAncestorWithClass(elementTL: String, classTL: String?): HTMLElementImpl? {
        val nodeObj: Any? = this.parentNode
        if (nodeObj is HTMLElementImpl) {
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && nodeObj.classMatch(classTL)) {
                return nodeObj
            }
            return nodeObj.getAncestorWithClass(elementTL, classTL)
        } else {
            return null
        }
    }

    fun getParentWithClass(elementTL: String, classTL: String?): HTMLElementImpl? {
        val nodeObj: Any? = this.parentNode
        if (nodeObj is HTMLElementImpl) {
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && nodeObj.classMatch(classTL)) {
                return nodeObj
            }
        }
        return null
    }

    val preceedingSiblingElement: HTMLElementImpl?
        get() {
            val parentNode = this.parentNode
            if (parentNode == null) {
                return null
            }
            val childNodes = parentNode.childNodes
            if (childNodes == null) {
                return null
            }
            val length = childNodes.length
            var priorElement: HTMLElementImpl? = null
            for (i in 0..<length) {
                val child = childNodes.item(i)
                if (child === this) {
                    return priorElement
                }
                if (child is HTMLElementImpl) {
                    priorElement = child
                }
            }
            return null
        }

    fun getPreceedingSiblingWithClass(elementTL: String, classTL: String?): HTMLElementImpl? {
        val psibling = this.preceedingSiblingElement
        if (psibling != null) {
            val pelementTL = psibling.tagName.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && psibling.classMatch(classTL)) {
                return psibling
            }
        }
        return null
    }

    fun getAncestorWithId(elementTL: String, idTL: String): HTMLElementImpl? {
        val nodeObj: Any? = this.parentNode
        if (nodeObj is HTMLElementImpl) {
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            val pid = nodeObj.id
            val pidTL = if (pid == null) null else pid.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && idTL == pidTL) {
                return nodeObj
            }
            return nodeObj.getAncestorWithId(elementTL, idTL)
        } else {
            return null
        }
    }

    fun getParentWithId(elementTL: String, idTL: String): HTMLElementImpl? {
        val nodeObj: Any? = this.parentNode
        if (nodeObj is HTMLElementImpl) {
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            val pid = nodeObj.id
            val pidTL = if (pid == null) null else pid.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && idTL == pidTL) {
                return nodeObj
            }
        }
        return null
    }

    fun getPreceedingSiblingWithId(elementTL: String, idTL: String): HTMLElementImpl? {
        val psibling = this.preceedingSiblingElement
        if (psibling != null) {
            val pelementTL = psibling.tagName.lowercase(Locale.getDefault())
            val pid = psibling.id
            val pidTL = if (pid == null) null else pid.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && idTL == pidTL) {
                return psibling
            }
        }
        return null
    }

    fun getAncestor(elementTL: String): HTMLElementImpl? {
        val nodeObj: Any? = this.parentNode
        if (nodeObj is HTMLElementImpl) {
            if ("*" == elementTL) {
                return nodeObj
            }
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            if (elementTL == pelementTL) {
                return nodeObj
            }
            return nodeObj.getAncestor(elementTL)
        } else {
            return null
        }
    }

    fun getParent(elementTL: String): HTMLElementImpl? {
        val nodeObj: Any? = this.parentNode
        if (nodeObj is HTMLElementImpl) {
            if ("*" == elementTL) {
                return nodeObj
            }
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            if (elementTL == pelementTL) {
                return nodeObj
            }
        }
        return null
    }

    fun getPreceedingSibling(elementTL: String): HTMLElementImpl? {
        val psibling = this.preceedingSiblingElement
        if (psibling != null) {
            if ("*" == elementTL) {
                return psibling
            }
            val pelementTL = psibling.tagName.lowercase(Locale.getDefault())
            if (elementTL == pelementTL) {
                return psibling
            }
        }
        return null
    }

    protected fun getAncestorForJavaClass(javaClass: Class<HTMLFormElement>): Any? {
        val nodeObj: Any? = this.parentNode
        if ((nodeObj == null) || javaClass.isInstance(nodeObj)) {
            return nodeObj
        } else if (nodeObj is HTMLElementImpl) {
            return nodeObj.getAncestorForJavaClass(javaClass)
        } else {
            return null
        }
    }

    fun setInnerHTML(newHtml: String) {
        val document = this.document as HTMLDocumentImpl?
        if (document == null) {
            this.warn("setInnerHTML(): Element " + this + " does not belong to a document.")
            return
        }
        val parser = HtmlParser(
            document.getUserAgentContext(),
            document,
            null,
            null,
            null,
            false,  /* TODO */
            false
        )
        synchronized(this) {
            removeAllChildrenImpl()
        }
        // Should not synchronize around parser probably.
        try {
            StringReader(newHtml).use { reader ->
                parser.parse(reader, this)
            }
        } catch (e: IOException) {
            this.warn("setInnerHTML(): Error setting inner HTML.", e)
        } catch (e: SAXException) {
            this.warn("setInnerHTML(): Error setting inner HTML.", e)
        }
    }

    val outerHTML: String
        get() {
            val buffer = StringBuffer()
            synchronized(this) {
                this.appendOuterHTMLImpl(buffer)
            }
            return buffer.toString()
        }

    fun appendOuterHTMLImpl(buffer: StringBuffer) {
        val tagName = this.tagName
        buffer.append('<')
        buffer.append(tagName)
        val attributes: MutableMap<String, String>? = this.attributes
        if (attributes != null) {
            attributes.forEach { (k: String?, v: String?) ->
                if (v != null) {
                    buffer.append(' ')
                    buffer.append(k)
                    buffer.append("=\"")
                    buffer.append(Strings.strictHtmlEncode(v, true))
                    buffer.append("\"")
                }
            }
        }
        val nl = this.nodeList
        if ((nl == null) || (nl.size == 0)) {
            buffer.append("/>")
            return
        }
        buffer.append('>')
        this.appendInnerHTMLImpl(buffer)
        buffer.append("</")
        buffer.append(tagName)
        buffer.append('>')
    }

    override fun createRenderState(prevRenderState: RenderState?): RenderState {
        // Overrides NodeImpl method
        // Called in synchronized block already
        return StyleSheetRenderState(prevRenderState, this)
    }

    val offsetTop: Int
        get() {
            // TODO: Sometimes this can be called while parsing, and
            // browsers generally give the right answer.
            val uiNode = this.uINode
            return if (uiNode == null) 0 else uiNode.boundsRelativeToBlock!!.y
        }

    val offsetLeft: Int
        get() {
            val uiNode = this.uINode
            return if (uiNode == null) 0 else uiNode.boundsRelativeToBlock!!.x
        }

    val offsetWidth: Int
        get() {
            val uiNode = this.uINode
            return if (uiNode == null) 0 else uiNode.boundsRelativeToBlock!!.width
        }

    val offsetHeight: Int
        get() {
            val uiNode = this.uINode
            return if (uiNode == null) 0 else uiNode.boundsRelativeToBlock!!.height
        }

    fun getDocumentBaseURI(): String? {
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            return doc.getBaseURI()
        } else {
            return null
        }
    }

    override fun handleDocumentAttachmentChanged() {
        if (isAttachedToDocument) {
            forgetLocalStyle()
            forgetStyle(false)
            informInvalid()
        }
        super.handleDocumentAttachmentChanged()
    }

    val classList: DOMTokenList
        get() = DOMTokenList()

    // Based on http://www.w3.org/TR/dom/#domtokenlist
    inner class DOMTokenList {
        private val classes: Array<String>
            get() = getAttribute("class")?.split(" ".toRegex())?.dropLastWhile { it.isEmpty() }
                ?.toTypedArray() ?: emptyArray()

        private fun getClasses(max: Int): Array<String> {
            return getAttribute("class")?.split(" ".toRegex(), max.coerceAtLeast(0))?.toTypedArray()
                ?: emptyArray()
        }

        val length: Long
            get() = this.classes.size.toLong()

        fun item(index: Long): String? {
            val indexInt = index.toInt()
            return getClasses(indexInt + 1)[0]
        }

        fun contains(token: String?): Boolean {
            return Arrays.stream<String?>(this.classes).anyMatch { t: String? -> t == token }
        }

        fun add(token: String?) {
            add(arrayOf<String>(token!!))
        }

        fun add(tokens: Array<String>) {
            val sb = StringBuilder()
            for (token in tokens) {
                if (token.length == 0) {
                    throw DOMException(DOMException.SYNTAX_ERR, "empty token")
                }

                // TODO: Check for whitespace and throw IllegalCharacterError
                sb.append(' ')
                sb.append(token)
            }
            setAttribute("class", getAttribute("class") + sb)
        }

        fun remove(tokenToRemove: String?) {
            remove(arrayOf<String?>(tokenToRemove))
        }

        fun remove(tokensToRemove: Array<String?>) {
            val existingClasses = this.classes
            val sb = StringBuilder()
            for (clazz in existingClasses) {
                if (!Arrays.stream<String?>(tokensToRemove)
                        .anyMatch { tr: String? -> tr == clazz }
                ) {
                    sb.append(' ')
                    sb.append(clazz)
                }
            }
            setAttribute("class", sb.toString())
        }

        fun toggle(tokenToToggle: String): Boolean {
            val existingClasses = this.classes
            for (clazz in existingClasses) {
                if (tokenToToggle == clazz) {
                    remove(tokenToToggle)
                    return false
                }
            }

            // Not found, hence add
            add(tokenToToggle)
            return true
        }

        fun toggle(token: String?, force: Boolean): Boolean {
            if (force) {
                add(token)
            } else {
                remove(token)
            }
            return force
        } /* TODO: stringifier; */
    }

    companion object {
        private val elementMatchCondition = MatchConditionOnElements()
        private val layoutProperties = arrayOf<String?>(
            "margin-top",
            "margin-bottom",
            "margin-left",
            "margin-right",
            "padding-top",
            "padding-bottom",
            "padding-left",
            "padding-right",
            "border-top-width",
            "border-bottom-width",
            "border-left-width",
            "border-right-width",
            "position",
            "display",
            "top",
            "left",
            "right",
            "bottom",
            "max-width",
            "min-width",
            "max-height",
            "min-height",
            "font-size",
            "font-family",
            "font-weight",
            "font-variant" // TODO: Add other font properties that affect layouting
        )

        private fun setupGeneratedNode(
            doc: HTMLDocumentImpl,
            nodeData: NodeData?,
            decl: Selector.PseudoElementType?,
            rules: Array<OrderedRule>,
            elem: HTMLElementImpl?
        ): GeneratedElement? {
            val genNodeData = AnalyzerUtil.getElementStyle(
                elem,
                decl,
                doc.matcher,
                elementMatchCondition,
                rules
            )
            /*
         * TODO: getValue returns null when `content:inherit` is set. This gives correct behavior per spec,
         * but one of the test disagrees https://github.com/w3c/csswg-test/issues/1133
         * If the test is accepted to be valid, then we should call inherit() and concretize() before getting the "content" value.
         */
            val content = genNodeData.getValue<TermList?>(TermList::class.java, "content", true)
            if (content != null) {
                genNodeData.inheritFrom(nodeData)
                genNodeData.concretize()
                return GeneratedElement(elem!!, genNodeData, content)
            } else {
                return null
            }
        }

        private fun hasHoverRule(rules: Array<OrderedRule>): Boolean {
            for (or in rules) {
                val r = or.rule
                for (cs in r.selectors) {
                    for (s in cs) {
                        if (s.hasPseudoClass(Selector.PseudoClassType.HOVER)) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        private fun getPseudoDeclaration(pseudoElement: String?): Selector.PseudoElementType? {
            if ((pseudoElement != null)) {
                var choppedPseudoElement: String? = pseudoElement
                if (pseudoElement.startsWith("::")) {
                    choppedPseudoElement = pseudoElement.substring(2)
                } else if (pseudoElement.startsWith(":")) {
                    choppedPseudoElement = pseudoElement.substring(1)
                }
                val pseudoDeclarations = Selector.PseudoElementType.entries.toTypedArray()
                for (pd in pseudoDeclarations) {
                    if (pd.name == choppedPseudoElement) {
                        return pd
                    }
                }
            }
            return null
        }

        protected fun getCssInputSourceForDecl(text: String): InputSource {
            val reader: Reader = StringReader(text)
            val `is` = InputSource(reader)
            return `is`
        }

        private fun layoutChanges(
            prevStyle: JStyleProperties?,
            newStyle: JStyleProperties?
        ): Boolean {
            if (prevStyle == null || newStyle == null) {
                return true
            }

            for (p in layoutProperties) {
                if (prevStyle.helperTryBoth(p) != newStyle.helperTryBoth(p)) {
                    return true
                }
            }
            return false
        }
    }
}
