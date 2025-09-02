package io.github.remmerw.thor.css

import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSStyleSheet

internal abstract class AbstractCSSRule(val containingStyleSheet: StyleSheetWrapper?) : CSSRule {
    /**
     * @return The style sheet that contains this rule.
     */
    override fun getParentStyleSheet(): CSSStyleSheet? {
        return containingStyleSheet
    }

    /**
     * If this rule is contained inside another rule (e.g. a style rule inside an @media
     * block), this is the containing rule. If this rule is not nested inside any
     * other rules, this returns `null`.
     */
    override fun getParentRule(): CSSRule? {
        // TODO needs to be overridden in MediaRule
        return null
    }
}
