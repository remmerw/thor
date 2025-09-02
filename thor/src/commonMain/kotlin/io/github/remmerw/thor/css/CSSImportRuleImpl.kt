package io.github.remmerw.thor.css

import cz.vutbr.web.css.RuleBlock
import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSImportRule
import org.w3c.dom.css.CSSRule.IMPORT_RULE
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.stylesheets.MediaList

internal class CSSImportRuleImpl(rule: RuleBlock<*>?, containingStyleSheet: StyleSheetWrapper?) :
    AbstractCSSRule(containingStyleSheet), CSSImportRule {
    override fun getType(): Short {
        return IMPORT_RULE
    }

    override fun getHref(): String? {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    override fun getMedia(): MediaList? {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    override fun getStyleSheet(): CSSStyleSheet? {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    override fun getCssText(): String? {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setCssText(cssText: String?) {
        // TODO implement this method
        throw UnsupportedOperationException()
    }
}
