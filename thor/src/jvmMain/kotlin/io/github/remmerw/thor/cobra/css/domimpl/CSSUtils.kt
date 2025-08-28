/*
   Copyright 2014 Uproot Labs India Pvt Ltd

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package io.github.remmerw.thor.cobra.css.domimpl

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
        if (jSheet.size > 0) {
            val ruleSet = jSheet.get(0) as RuleSet
            return Arrays.asList<CombinedSelector?>(*ruleSet.selectors)
        }
        return ArrayList<CombinedSelector?>()
    }

    fun removeBrackets(str: String): String {
        return str.substring(1, str.length - 1)
    }
}
