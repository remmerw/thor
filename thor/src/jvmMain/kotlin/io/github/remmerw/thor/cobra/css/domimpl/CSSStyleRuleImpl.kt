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

import cz.vutbr.web.css.RuleSet
import org.w3c.dom.DOMException
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
        this.containingStyleSheet.informChanged()
    }

    override fun getStyle(): CSSStyleDeclaration {
        return CSSStyleDeclarationImpl(ruleSet.asList(), this)
    }

    override fun toString(): String {
        return ruleSet.toString()
    }
}
