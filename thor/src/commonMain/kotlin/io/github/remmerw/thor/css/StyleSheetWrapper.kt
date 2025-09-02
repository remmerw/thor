package io.github.remmerw.thor.css

import cz.vutbr.web.css.StyleSheet
import org.w3c.dom.DOMException
import org.w3c.dom.Node
import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSRuleList
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.stylesheets.MediaList
import org.w3c.dom.stylesheets.StyleSheetList
import kotlin.concurrent.Volatile

/**
 * This is a wrapper for CSS DOM API implementation.
 *
 *
 * Acts as a base Class for `CSSNodeStyleSheet` and
 * `CSSRuleStyleSheet`.
 *
 *
 * This class implements the common methods between the two class mentioned.
 */
class StyleSheetWrapper internal constructor(
    @JvmField @field:Volatile var styleSheet: StyleSheet?,
    private val mediaStr: String?,
    private val href: String?,
    private var ownerNode: Node?,
    private val parentStyleSheet: CSSStyleSheet?,
    private val type: String?,
    private val title: String?,
    private val bridge: StyleSheetBridge
) : CSSStyleSheet {
    @Volatile
    private var disabled = false

    /**
     * @param jStyleSheet      parsed style sheet from jStyleParser
     * @param mediaStr         The intended destination media for style information. The media is
     * often specified in the `ownerNode`. If no media has
     * been specified, the `MediaList` will be empty. See the
     * media attribute definition for the `LINK` element in
     * HTML 4.0, and the media pseudo-attribute for the XML style sheet
     * processing instruction . Modifying the media list may cause a
     * change to the attribute `disabled`.
     * @param href             If the style sheet is a linked style sheet, the value of its
     * attribute is its location. For inline style sheets, the value of
     * this attribute is `null`. See the href attribute
     * definition for the `LINK` element in HTML 4.0, and the
     * href pseudo-attribute for the XML style sheet processing
     * instruction.
     * @param parentStyleSheet For style sheet languages that support the concept of style sheet
     * inclusion, this attribute represents the including style sheet, if
     * one exists. If the style sheet is a top-level style sheet, or the
     * style sheet language does not support inclusion, the value of this
     * attribute is `null`.
     * @param bridge           callback to notify any changes in the style sheet or to
     * dynamically get data from the caller.
     */
    constructor(
        jStyleSheet: StyleSheet?, mediaStr: String?, href: String?,
        parentStyleSheet: CSSStyleSheet?, bridge: StyleSheetBridge
    ) : this(jStyleSheet, mediaStr, href, null, parentStyleSheet, null, null, bridge)

    /**
     * @param jStyleSheet parsed style sheet from jStyleParser
     * @param mediaStr    The intended destination media for style information. The media is
     * often specified in the `ownerNode`. If no media has
     * been specified, the `MediaList` will be empty. See the
     * media attribute definition for the `LINK` element in
     * HTML 4.0, and the media pseudo-attribute for the XML style sheet
     * processing instruction . Modifying the media list may cause a
     * change to the attribute `disabled`.
     * @param href        If the style sheet is a linked style sheet, the value of its
     * attribute is its location. For inline style sheets, the value of
     * this attribute is `null`. See the href attribute
     * definition for the `LINK` element in HTML 4.0, and the
     * href pseudo-attribute for the XML style sheet processing
     * instruction.
     * @param type        the type of the style sheet. e.g. "text/css"
     * @param title       The advisory title. The title is often specified in the
     * `ownerNode`. See the title attribute definition for the
     * `LINK` element in HTML 4.0, and the title
     * pseudo-attribute for the XML style sheet processing instruction.
     * @param ownerNode   The node that associates this style sheet with the document. For
     * HTML, this may be the corresponding `LINK` or
     * `STYLE` element. For XML, it may be the linking
     * processing instruction. For style sheets that are included by
     * other style sheets, the value of this attribute is
     * `null`.
     * @param bridge      callback to notify any changes in the style sheet or to
     * dynamically get data from the caller.
     */
    constructor(
        jStyleSheet: StyleSheet?, mediaStr: String?, href: String?, type: String?,
        title: String?, ownerNode: Node?, bridge: StyleSheetBridge
    ) : this(jStyleSheet, mediaStr, href, ownerNode, null, type, title, bridge)

    /**
     * @return The state of the style sheet `true` if the style sheet
     * is disabled, `false` otherwise
     */
    override fun getDisabled(): Boolean {
        return this.disabled
    }

    /**
     * This will enable/disable the style sheet and also send a notification to
     * all the listeners about this change in the style sheet.
     *
     * @param disabled state of the style sheet `true` if the style sheet is
     * disabled, `false` otherwise
     */
    override fun setDisabled(disabled: Boolean) {
        this.disabled = disabled
        this.informChanged()
    }

    /**
     * @return If the style sheet is a linked style sheet, the value returned is
     * its location. For inline style sheets, the value returned will be
     * `null`. For `@import` rules the returned
     * value will be the url specified in the rule.
     */
    override fun getHref(): String? {
        return this.href
    }

    /**
     * @return A list of media to which this style sheet is applicable
     */
    override fun getMedia(): MediaList {
        return MediaListImpl(mediaStr, this)
    }

    /**
     * @return style sheet language for this style sheet. `null` for
     * `@import` rule as it does not have a type associated
     * with it.
     */
    override fun getType(): String? {
        return this.type
    }

    /**
     * @return The node that associates this style sheet with the document. For
     * HTML, this may be the corresponding `LINK` or
     * `STYLE` element. For XML, it may be the linking
     * processing instruction. `null` for `@import`
     * as the `@import` rule is not associated any
     * `Node`.
     */
    override fun getOwnerNode(): Node? {
        return this.ownerNode
    }

    // TODO hide it from JS
    fun setOwnerNode(ownerNode: Node?) {
        this.ownerNode = ownerNode
    }

    /**
     * @return The containing `Style Sheet`, applicable only for
     * `@import` rules. `null` for nodes as the
     * style sheet is a top-level style sheet, either from
     * `LINK` or `STYLE` element.
     */
    override fun getParentStyleSheet(): org.w3c.dom.stylesheets.StyleSheet? {
        return this.parentStyleSheet
    }

    /**
     * @return The advisory title. The title specified in the
     * `ownerNode`. e.g. the title attribute of the
     * `LINK` element in HTML 4.0, and the title
     * pseudo-attribute for the XML style sheet processing instruction.
     * `null` for Import rules as the `@import` rule
     * does not have a title associated with it.
     */
    override fun getTitle(): String? {
        return this.title
    }

    override fun getOwnerRule(): CSSRule? {
        // TODO Import Rules are not yet supported, once supported this method should return
        // the @import rule which gets this style sheet
        // null for other cases
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "this operation is not supported")
    }

    /**
     * @return A list of rules contained in this style sheet.
     */
    override fun getCssRules(): CSSRuleList {
        if (this.styleSheet != null) {
            return CSSRuleListImpl(styleSheet!!, this)
        }
        throw DOMException(
            DOMException.INVALID_ACCESS_ERR,
            "A parameter or an operation is not supported by the underlying object"
        )
    }

    /**
     * Used to insert a new rule into the style sheet. The new rule now becomes
     * part of the cascade.
     *
     * @param rule  The parsable text representing the rule. For rule sets this
     * contains both the selector and the style declaration. For
     * at-rules, this specifies both the at-identifier and the rule
     * content.
     * @param index The index within the style sheet's rule list of the rule before
     * which to insert the specified rule. If the specified index is
     * equal to the length of the style sheet's rule collection, the rule
     * will be added to the end of the style sheet.
     * @return The index within the style sheet's rule collection of the newly
     * inserted rule.
     * @throws DOMException HIERARCHY_REQUEST_ERR: Raised if the rule cannot be inserted
     * at the specified index e.g. if an `@import` rule is
     * inserted after a standard rule set or other at-rule. <br></br>
     * INDEX_SIZE_ERR: Raised if the specified index is not a valid
     * insertion point. <br></br>
     * NO_MODIFICATION_ALLOWED_ERR: Raised if this style sheet is
     * readonly. <br></br>
     * SYNTAX_ERR: Raised if the specified rule has a syntax error
     * and is unparsable.
     */
    //TODO handle all the different types of exceptions as mentioned above
    @Throws(DOMException::class)
    override fun insertRule(rule: String?, index: Int): Int {
        val jSheet = CSSUtils.parse(rule)
        if (jSheet.size > 0) {
            this.styleSheet!!.add(index, jSheet.get(0))
            bridge.notifyStyleSheetChanged(this)
            return index
        }
        return -1
    }

    /**
     * Used to delete a rule from the style sheet.
     *
     * @param index The index within the style sheet's rule list of the rule to
     * remove.
     * @throws DOMException INDEX_SIZE_ERR: Raised if the specified index does not
     * correspond to a rule in the style sheet's rule list. <br></br>
     * NO_MODIFICATION_ALLOWED_ERR: Raised if this style sheet is
     * read-only.
     */
    @Throws(DOMException::class)
    override fun deleteRule(index: Int) {
        this.styleSheet!!.removeAt(index)
        bridge.notifyStyleSheetChanged(this)
    }

    fun informChanged() {
        bridge.notifyStyleSheetChanged(this)
    }

    companion object {
        /**
         * @param styleSheets Creates an StyleSheetList instance from these style sheets
         * @return StyleSheetList object constructed from the list of style sheets
         */
        @JvmStatic
        fun getStyleSheets(bridge: StyleSheetBridge): StyleSheetList {
            return StyleSheetListImpl(bridge)
        }
    }
}
