package io.github.remmerw.thor.css

import cz.vutbr.web.css.CSSFactory
import cz.vutbr.web.css.CombinedSelector
import cz.vutbr.web.css.RuleSet
import cz.vutbr.web.css.StyleSheet
import org.w3c.dom.DOMException

internal object CSSUtils {
    fun parse(css: String?): StyleSheet {
        try {
            return CSSFactory.parse(css)
        } catch (_: Throwable) {
            throw DOMException(DOMException.SYNTAX_ERR, "")
        }
    }

    fun createCombinedSelectors(selectorText: String?): List<CombinedSelector> {
        val jSheet = parse("$selectorText{}")
        if (jSheet.isNotEmpty()) {
            val ruleSet = jSheet[0] as RuleSet
            return ruleSet.selectors.toList()
        }
        return emptyList()
    }

    fun removeBrackets(str: String): String {
        return str.substring(1, str.length - 1)
    }
}
