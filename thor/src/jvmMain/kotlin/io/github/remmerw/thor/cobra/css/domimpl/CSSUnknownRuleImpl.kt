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

import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSUnknownRule

internal class CSSUnknownRuleImpl(containingStyleSheet: JStyleSheetWrapper?) :
    AbstractCSSRule(containingStyleSheet), CSSUnknownRule {
    override fun getType(): Short {
        return UNKNOWN_RULE
    }

    override fun getCssText(): String? {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "")
    }

    @Throws(DOMException::class)
    override fun setCssText(cssText: String?) {
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "")
    }
}
