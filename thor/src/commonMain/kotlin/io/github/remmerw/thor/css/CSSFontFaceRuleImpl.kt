package io.github.remmerw.thor.css

import cz.vutbr.web.css.RuleFontFace
import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSFontFaceRule
import org.w3c.dom.css.CSSRule.FONT_FACE_RULE
import org.w3c.dom.css.CSSStyleDeclaration

internal class CSSFontFaceRuleImpl(
    val rule: RuleFontFace,
    containingStyleSheet: JStyleSheetWrapper?
) : AbstractCSSRule(containingStyleSheet), CSSFontFaceRule {
    override fun getCssText(): String? {
        return rule.toString()
    }

    @Throws(DOMException::class)
    override fun setCssText(cssText: String?) {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    override fun getStyle(): CSSStyleDeclaration {
        return CSSStyleDeclarationImpl(rule.asList(), this)
    }

    override fun getType(): Short {
        return FONT_FACE_RULE
    }
}
