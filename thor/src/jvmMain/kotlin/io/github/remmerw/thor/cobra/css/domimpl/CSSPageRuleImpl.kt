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

import cz.vutbr.web.css.Declaration
import cz.vutbr.web.css.RulePage
import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSPageRule
import org.w3c.dom.css.CSSRule.PAGE_RULE
import org.w3c.dom.css.CSSStyleDeclaration

internal class CSSPageRuleImpl(
    private val rule: RulePage,
    containingStyleSheet: JStyleSheetWrapper?
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
        val declarations: MutableList<Declaration?> = ArrayList<Declaration?>()
        for (r in rule) {
            if (r is Declaration) {
                declarations.add(r)
            }
        }
        return CSSStyleDeclarationImpl(declarations, this)
    }
}
