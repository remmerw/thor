package io.github.remmerw.thor.css

import cz.vutbr.web.css.CSSException
import cz.vutbr.web.css.CSSFactory
import cz.vutbr.web.css.CombinedSelector
import cz.vutbr.web.css.RuleSet
import cz.vutbr.web.css.StyleSheet
import org.w3c.dom.DOMException
import java.io.IOException
import java.util.Arrays

internal object CSSUtils {
    fun parse(css: String?): StyleSheet {
        try {
            return CSSFactory.parse(css)
        } catch (e: IOException) {
            throw DOMException(DOMException.SYNTAX_ERR, "")
        } catch (e: CSSException) {
            throw DOMException(DOMException.SYNTAX_ERR, "")
        }
    }

    fun createCombinedSelectors(selectorText: String?): MutableList<CombinedSelector?> {
        val jSheet = parse(selectorText + "{}")
        if (jSheet.isNotEmpty()) {
            val ruleSet = jSheet.get(0) as RuleSet
            return Arrays.asList<CombinedSelector?>(*ruleSet.selectors)
        }
        return ArrayList<CombinedSelector?>()
    }

    fun removeBrackets(str: String): String {
        return str.substring(1, str.length - 1)
    }
}
