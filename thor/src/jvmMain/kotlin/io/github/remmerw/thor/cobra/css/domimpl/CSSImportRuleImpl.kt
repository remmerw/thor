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

import cz.vutbr.web.css.RuleBlock
import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSImportRule
import org.w3c.dom.css.CSSRule.IMPORT_RULE
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.stylesheets.MediaList

internal class CSSImportRuleImpl(rule: RuleBlock<*>?, containingStyleSheet: JStyleSheetWrapper?) :
    AbstractCSSRule(containingStyleSheet), CSSImportRule {
    override fun getType(): Short {
        return IMPORT_RULE
    }

    override fun getHref(): String? {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    override fun getMedia(): MediaList? {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    override fun getStyleSheet(): CSSStyleSheet? {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    override fun getCssText(): String? {
        // TODO implement this method
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setCssText(cssText: String?) {
        // TODO implement this method
        throw UnsupportedOperationException()
    }
}
