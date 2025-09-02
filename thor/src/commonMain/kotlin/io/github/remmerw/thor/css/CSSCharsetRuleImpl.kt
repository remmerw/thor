package io.github.remmerw.thor.css

import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSCharsetRule
import org.w3c.dom.css.CSSRule.CHARSET_RULE

internal class CSSCharsetRuleImpl(containingStyleSheet: StyleSheetWrapper?) :
    AbstractCSSRule(containingStyleSheet), CSSCharsetRule {
    override fun getType(): Short {
        return CHARSET_RULE
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

    override fun getEncoding(): String? {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setEncoding(encoding: String?) {
        // TODO implement this method
        throw UnsupportedOperationException()
    }
}
