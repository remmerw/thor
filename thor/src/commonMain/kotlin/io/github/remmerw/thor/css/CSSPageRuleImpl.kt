package io.github.remmerw.thor.css

import cz.vutbr.web.css.Declaration
import cz.vutbr.web.css.RulePage
import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSPageRule
import org.w3c.dom.css.CSSRule.PAGE_RULE
import org.w3c.dom.css.CSSStyleDeclaration

internal class CSSPageRuleImpl(
    private val rule: RulePage,
    containingStyleSheet: StyleSheetWrapper?
) : AbstractCSSRule(containingStyleSheet), CSSPageRule {
    override fun getType(): Short {
        return PAGE_RULE
    }

    override fun getCssText(): String? {
        return rule.toString()
    }

    @Throws(DOMException::class)
    override fun setCssText(cssText: String?) {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    override fun getSelectorText(): String? {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setSelectorText(selectorText: String?) {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    override fun getStyle(): CSSStyleDeclaration {
        val declarations: MutableList<Declaration> = ArrayList()
        for (r in rule) {
            if (r is Declaration) {
                declarations.add(r)
            }
        }
        return CSSStyleDeclarationImpl(declarations, this)
    }
}
