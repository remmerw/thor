package io.github.remmerw.thor.css

import cz.vutbr.web.css.RuleSet
import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSRule.STYLE_RULE
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.css.CSSStyleRule

internal class CSSStyleRuleImpl(val ruleSet: RuleSet, containingStyleSheet: JStyleSheetWrapper?) :
    AbstractCSSRule(containingStyleSheet), CSSStyleRule {
    override fun getType(): Short {
        return STYLE_RULE
    }

    override fun getCssText(): String {
        return ruleSet.toString()
    }

    @Throws(DOMException::class)
    override fun setCssText(cssText: String?) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException()
    }

    override fun getSelectorText(): String {
        val selector = ruleSet.selectors.toString()
        return CSSUtils.removeBrackets(selector)
    }

    @Throws(DOMException::class)
    override fun setSelectorText(selectorText: String?) {
        val combinedSelectors = CSSUtils.createCombinedSelectors(selectorText)
        this.ruleSet.setSelectors(combinedSelectors)
        this.containingStyleSheet?.informChanged()
    }

    override fun getStyle(): CSSStyleDeclaration {
        return CSSStyleDeclarationImpl(ruleSet.asList(), this)
    }

    override fun toString(): String {
        return ruleSet.toString()
    }
}
