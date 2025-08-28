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

import cz.vutbr.web.css.RuleFontFace
import cz.vutbr.web.css.RuleMedia
import cz.vutbr.web.css.RulePage
import cz.vutbr.web.css.RuleSet
import cz.vutbr.web.css.StyleSheet
import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSRuleList

internal class CSSRuleListImpl(
    private val jSheet: StyleSheet,
    private val parentStyleSheet: JStyleSheetWrapper?
) : CSSRuleList {
    /**
     * @return The number of `CSSRules` in the list. The range of valid
     * child rule indices is `0` to `length-1`
     * inclusive.
     */
    override fun getLength(): Int {
        return this.jSheet.size
    }

    /**
     * Used to retrieve a CSS rule by ordinal index. The order in this collection
     * represents the order of the rules in the CSS style sheet. If index is
     * greater than or equal to the number of rules in the list, this returns
     * `null`.
     *
     * @param index Index into the collection
     * @return The style rule at the `index` position in the
     * `CSSRuleList`, or `null` if that is not a
     * valid index.
     */
    override fun item(index: Int): CSSRule? {
        try {
            val ruleBlock = jSheet.asList().get(index)
            if (ruleBlock is RuleSet) {
                return CSSStyleRuleImpl(ruleBlock, parentStyleSheet)
            } else if (ruleBlock is RuleFontFace) {
                return CSSFontFaceRuleImpl(ruleBlock, parentStyleSheet)
            } else if (ruleBlock is RulePage) {
                return CSSPageRuleImpl(ruleBlock, parentStyleSheet)
            } else if (ruleBlock is RuleMedia) {
                return CSSMediaRuleImpl(ruleBlock, parentStyleSheet)
            } else {
                // TODO need to return the other types of RuleBlocks as well.
                // * Import Rule
                // * Charset Rule
                // Currently returning Unknown rule
                return CSSUnknownRuleImpl(parentStyleSheet)
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            return null
        }
    }
}
