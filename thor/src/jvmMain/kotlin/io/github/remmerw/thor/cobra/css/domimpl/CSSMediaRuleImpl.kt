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

import cz.vutbr.web.css.RuleMedia
import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSMediaRule
import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSRuleList
import org.w3c.dom.stylesheets.MediaList

internal class CSSMediaRuleImpl(
    private val mediaRule: RuleMedia,
    containingStyleSheet: JStyleSheetWrapper?
) : AbstractCSSRule(containingStyleSheet), CSSMediaRule {
    override fun getType(): Short {
        return MEDIA_RULE
    }

    override fun getCssText(): String? {
        return mediaRule.toString()
    }

    @Throws(DOMException::class)
    override fun setCssText(cssText: String?) {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    override fun getMedia(): MediaList {
        return MediaListImpl(mediaRule.mediaQueries, this.containingStyleSheet)
    }

    override fun getCssRules(): CSSRuleList {
        return CSSMediaRuleList()
    }

    @Throws(DOMException::class)
    override fun insertRule(rule: String?, index: Int): Int {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun deleteRule(index: Int) {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    private inner class CSSMediaRuleList : CSSRuleList {
        /**
         * @return The number of `CSSRules` in the list. The range of
         * valid child rule indices is `0` to
         * `length-1` inclusive.
         */
        override fun getLength(): Int {
            return mediaRule.size
        }

        /**
         * Used to retrieve a CSS rule by ordinal index. The order in this
         * collection represents the order of the rules in the CSS style sheet. If
         * index is greater than or equal to the number of rules in the list, this
         * returns `null`.
         *
         * @param index Index into the collection
         * @return The style rule at the `index` position in the
         * `CSSRuleList`, or `null` if that is not a
         * valid index.
         */
        override fun item(index: Int): CSSRule? {
            try {
                // according to JStyle Parser RuleMedia is a list of RuleSet
                val ruleSet = mediaRule.asList().get(index)
                return CSSStyleRuleImpl(ruleSet, containingStyleSheet)
            } catch (e: ArrayIndexOutOfBoundsException) {
                return null
            }
        }
    }
}
