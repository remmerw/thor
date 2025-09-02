package io.github.remmerw.thor.css

import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSRule.UNKNOWN_RULE
import org.w3c.dom.css.CSSUnknownRule

internal class CSSUnknownRuleImpl(containingStyleSheet: StyleSheetWrapper?) :
    AbstractCSSRule(containingStyleSheet), CSSUnknownRule {
    override fun getType(): Short {
        return UNKNOWN_RULE
    }

    override fun getCssText(): String? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "")
    }

    @Throws(DOMException::class)
    override fun setCssText(cssText: String?) {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "")
    }
}
