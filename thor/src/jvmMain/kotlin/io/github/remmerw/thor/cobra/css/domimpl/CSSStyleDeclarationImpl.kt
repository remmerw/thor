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
import cz.vutbr.web.css.RuleSet
import cz.vutbr.web.css.StyleSheet
import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.css.CSSValue

internal class CSSStyleDeclarationImpl(
    private val declarations: MutableList<Declaration>,
    private val parentRule: AbstractCSSRule
) : CSSStyleDeclaration {
    override fun getCssText(): String? {
        return declarations.toString()
    }

    @Throws(DOMException::class)
    override fun setCssText(cssText: String?) {
        val jSheet = CSSUtils.parse("*{" + cssText + "}")
        declarations.clear()
        for (rule in jSheet) {
            declarations.addAll((rule as RuleSet?)!!)
        }
        val styleSheet = parentRule.containingStyleSheet
        styleSheet?.informChanged()
    }

    override fun getPropertyValue(propertyName: String?): String? {
        val propertyValueTerm = getPropertyValueTerm(propertyName)
        return if (propertyValueTerm == null) null else propertyValueTerm.asList().toString()
    }

    private fun getPropertyValueTerm(propertyName: String?): Declaration? {
        for (d in declarations) {
            if (d.property.equals(propertyName, ignoreCase = true)) {
                return d
            }
        }
        return null
    }

    override fun getPropertyCSSValue(propertyName: String?): CSSValue? {
        // TODO implement this method
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "This operation is not supported")
    }

    @Throws(DOMException::class)
    override fun removeProperty(propertyName: String?): String? {
        if (!declarations.isEmpty()) {
            val currDecl = getPropertyValueTerm(propertyName)
            if (currDecl != null) {
                val `val`: String? = currDecl.toString()
                declarations.remove(currDecl)
                val styleSheet = parentRule.containingStyleSheet
                styleSheet?.informChanged()
                return `val`
            }
        }
        return ""
    }

    override fun getPropertyPriority(propertyName: String?): String? {
        // TODO implement this method
        throw DOMException(DOMException.NOT_SUPPORTED_ERR, "This operation is not supported")
    }

    // TODO check if priority is optional
    // see how priority can be used with respect to jStyle.
    // currently priority is not being used at all
    @Throws(DOMException::class)
    override fun setProperty(propertyName: String?, value: String?, priority: String?) {
        val jSheet = parseStyle(propertyName, value, priority)
        for (rule in jSheet) {
            val rs = rule as RuleSet
            for (decl in rs) {
                val currDec = getPropertyValueTerm(propertyName)
                if (currDec == null) {
                    declarations.add(decl)
                } else {
                    currDec.clear()
                    currDec.addAll(decl)
                }
            }
        }
    }

    override fun getLength(): Int {
        return declarations.size
    }

    override fun item(index: Int): String? {
        return declarations.get(index).property
    }

    override fun getParentRule(): CSSRule {
        return this.parentRule
    }

    private fun parseStyle(propertyName: String?, value: String?, priority: String?): StyleSheet {
        return CSSUtils.parse("* { " + propertyName + ": " + value + "; }")
    }

    override fun toString(): String {
        return declarations.toString()
    }
}
