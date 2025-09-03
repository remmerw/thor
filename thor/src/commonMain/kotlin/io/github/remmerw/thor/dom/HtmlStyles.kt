package io.github.remmerw.thor.dom

import cz.vutbr.web.css.NodeData
import cz.vutbr.web.css.Selector
import cz.vutbr.web.css.TermList
import cz.vutbr.web.csskit.MatchConditionOnElements
import cz.vutbr.web.domassign.Analyzer.OrderedRule
import cz.vutbr.web.domassign.AnalyzerUtil
import io.github.remmerw.thor.style.CssProperties
import org.w3c.css.sac.InputSource
import java.io.Reader
import java.io.StringReader

object HtmlStyles {


    val elementMatchCondition = MatchConditionOnElements()
    private val layoutProperties = arrayOf(
        "margin-top",
        "margin-bottom",
        "margin-left",
        "margin-right",
        "padding-top",
        "padding-bottom",
        "padding-left",
        "padding-right",
        "border-top-width",
        "border-bottom-width",
        "border-left-width",
        "border-right-width",
        "position",
        "display",
        "top",
        "left",
        "right",
        "bottom",
        "max-width",
        "min-width",
        "max-height",
        "min-height",
        "font-size",
        "font-family",
        "font-weight",
        "font-variant" // TODO: Add other font properties that affect layouting
    )

    private fun setupGeneratedNode(
        doc: HTMLDocumentImpl,
        nodeData: NodeData?,
        decl: Selector.PseudoElementType?,
        rules: Array<OrderedRule>,
        elem: HTMLElementModel?
    ): GeneratedElement? {
        val genNodeData = AnalyzerUtil.getElementStyle(
            elem,
            decl,
            doc.matcher,
            elementMatchCondition,
            rules
        )
        /*
     * TODO: getValue returns null when `content:inherit` is set. This gives correct behavior per spec,
     * but one of the test disagrees https://github.com/w3c/csswg-test/issues/1133
     * If the test is accepted to be valid, then we should call inherit() and concretize() before getting the "content" value.
     */
        val content = genNodeData.getValue<TermList?>(TermList::class.java, "content", true)
        if (content != null) {
            genNodeData.inheritFrom(nodeData)
            genNodeData.concretize()
            return GeneratedElement(elem!!, genNodeData, content)
        } else {
            return null
        }
    }

    fun hasHoverRule(rules: Array<OrderedRule>): Boolean {
        for (or in rules) {
            val r = or.rule
            for (cs in r.selectors) {
                for (s in cs) {
                    if (s.hasPseudoClass(Selector.PseudoClassType.HOVER)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun getPseudoDeclaration(pseudoElement: String?): Selector.PseudoElementType? {
        if ((pseudoElement != null)) {
            var choppedPseudoElement: String? = pseudoElement
            if (pseudoElement.startsWith("::")) {
                choppedPseudoElement = pseudoElement.substring(2)
            } else if (pseudoElement.startsWith(":")) {
                choppedPseudoElement = pseudoElement.substring(1)
            }
            val pseudoDeclarations = Selector.PseudoElementType.entries.toTypedArray()
            for (pd in pseudoDeclarations) {
                if (pd.name == choppedPseudoElement) {
                    return pd
                }
            }
        }
        return null
    }

    fun getCssInputSourceForDecl(text: String): InputSource {
        val reader: Reader = StringReader(text)
        val `is` = InputSource(reader)
        return `is`
    }

    private fun layoutChanges(
        prevStyle: CssProperties?,
        newStyle: CssProperties?
    ): Boolean {
        if (prevStyle == null || newStyle == null) {
            return true
        }

        for (p in layoutProperties) {
            if (prevStyle.helperTryBoth(p) != newStyle.helperTryBoth(p)) {
                return true
            }
        }
        return false
    }

    fun cssProperties(nodeData: NodeData, elementImpl: ElementImpl) {
        cssProperty(nodeData, elementImpl, "azimuth")
        cssProperty(nodeData, elementImpl, "background")
        cssProperty(nodeData, elementImpl, "background-attachment")
        cssProperty(nodeData, elementImpl, "background-color")
        cssProperty(nodeData, elementImpl, "background-color")
        cssProperty(nodeData, elementImpl, "background-position")
        cssProperty(nodeData, elementImpl, "background-repeat")
        cssProperty(nodeData, elementImpl, "border")
        cssProperty(nodeData, elementImpl, "border-collapse")
        cssProperty(nodeData, elementImpl, "border-color")
        cssProperty(nodeData, elementImpl, "border-style")
        cssProperty(nodeData, elementImpl, "border-spacing")
        cssProperty(nodeData, elementImpl, "border-top")
        cssProperty(nodeData, elementImpl, "border-right")
        cssProperty(nodeData, elementImpl, "border-bottom")
        cssProperty(nodeData, elementImpl, "border-left")
        cssProperty(nodeData, elementImpl, "border-top-color")
        cssProperty(nodeData, elementImpl, "border-right-color")
        cssProperty(nodeData, elementImpl, "border-bottom-color")
        cssProperty(nodeData, elementImpl, "border-left-color")
        cssProperty(nodeData, elementImpl, "border-top-style")
        cssProperty(nodeData, elementImpl, "border-right-style")
        cssProperty(nodeData, elementImpl, "border-bottom-style")
        cssProperty(nodeData, elementImpl, "border-left-style")
        cssProperty(nodeData, elementImpl, "border-top-width")
        cssProperty(nodeData, elementImpl, "border-right-width")
        cssProperty(nodeData, elementImpl, "border-bottom-width")
        cssProperty(nodeData, elementImpl, "border-left-width")
        cssProperty(nodeData, elementImpl, "border-width")
        cssProperty(nodeData, elementImpl, "bottom")
        cssProperty(nodeData, elementImpl, "caption-side")
        cssProperty(nodeData, elementImpl, "clear")
        cssProperty(nodeData, elementImpl, "clip")
        cssProperty(nodeData, elementImpl, "color")
        cssProperty(nodeData, elementImpl, "content")
        cssProperty(nodeData, elementImpl, "couter-increment")
        cssProperty(nodeData, elementImpl, "couter-reset")
        cssProperty(nodeData, elementImpl, "cue")
        cssProperty(nodeData, elementImpl, "cue-after")
        cssProperty(nodeData, elementImpl, "cue-before")
        cssProperty(nodeData, elementImpl, "css-float")
        cssProperty(nodeData, elementImpl, "direction")
        cssProperty(nodeData, elementImpl, "display")
        cssProperty(nodeData, elementImpl, "elevation")
        cssProperty(nodeData, elementImpl, "empty-cells")
        cssProperty(nodeData, elementImpl, "float")
        cssProperty(nodeData, elementImpl, "font")
        cssProperty(nodeData, elementImpl, "font-family")
        cssProperty(nodeData, elementImpl, "font-size")
        cssProperty(nodeData, elementImpl, "font-adjust")
        cssProperty(nodeData, elementImpl, "font-stretch")
        cssProperty(nodeData, elementImpl, "font-style")
        cssProperty(nodeData, elementImpl, "font-variant")
        cssProperty(nodeData, elementImpl, "font-weight")
        cssProperty(nodeData, elementImpl, "height")
        cssProperty(nodeData, elementImpl, "left")
        cssProperty(nodeData, elementImpl, "letter-spacing")
        cssProperty(nodeData, elementImpl, "line-height")
        cssProperty(nodeData, elementImpl, "list-style")
        cssProperty(nodeData, elementImpl, "list-style-image")
        cssProperty(nodeData, elementImpl, "list-style-position")
        cssProperty(nodeData, elementImpl, "list-style-type")
        cssProperty(nodeData, elementImpl, "margin")
        cssProperty(nodeData, elementImpl, "margin-top")
        cssProperty(nodeData, elementImpl, "margin-right")
        cssProperty(nodeData, elementImpl, "margin-bottom")
        cssProperty(nodeData, elementImpl, "margin-left")
        cssProperty(nodeData, elementImpl, "marker-offset")
        cssProperty(nodeData, elementImpl, "marks")
        cssProperty(nodeData, elementImpl, "max-height")
        cssProperty(nodeData, elementImpl, "max-width")
        cssProperty(nodeData, elementImpl, "min-height")
        cssProperty(nodeData, elementImpl, "min-width")
        cssProperty(nodeData, elementImpl, "orphans")
        cssProperty(nodeData, elementImpl, "outline")
        cssProperty(nodeData, elementImpl, "outline-color")
        cssProperty(nodeData, elementImpl, "outline-style")
        cssProperty(nodeData, elementImpl, "outline-border")
        cssProperty(nodeData, elementImpl, "overflow")
        cssProperty(nodeData, elementImpl, "padding")
        cssProperty(nodeData, elementImpl, "padding-top")
        cssProperty(nodeData, elementImpl, "padding-right")
        cssProperty(nodeData, elementImpl, "padding-bottom")
        cssProperty(nodeData, elementImpl, "padding-left")
        cssProperty(nodeData, elementImpl, "page")
        cssProperty(nodeData, elementImpl, "page-break-after")
        cssProperty(nodeData, elementImpl, "page-break-before")
        cssProperty(nodeData, elementImpl, "page-break-inside")
        cssProperty(nodeData, elementImpl, "pause")
        cssProperty(nodeData, elementImpl, "pause-after")
        cssProperty(nodeData, elementImpl, "pause-before")
        cssProperty(nodeData, elementImpl, "pitch")
        cssProperty(nodeData, elementImpl, "pitchRange")
        cssProperty(nodeData, elementImpl, "play-during")
        cssProperty(nodeData, elementImpl, "position")
        cssProperty(nodeData, elementImpl, "quotes")
        cssProperty(nodeData, elementImpl, "richness")
        cssProperty(nodeData, elementImpl, "right")
        cssProperty(nodeData, elementImpl, "size")
        cssProperty(nodeData, elementImpl, "speak")
        cssProperty(nodeData, elementImpl, "speak-header")
        cssProperty(nodeData, elementImpl, "speak-numeral")
        cssProperty(nodeData, elementImpl, "speak-punctuation")
        cssProperty(nodeData, elementImpl, "speech-rate")
        cssProperty(nodeData, elementImpl, "stress")
        cssProperty(nodeData, elementImpl, "table-layout")
        cssProperty(nodeData, elementImpl, "text-align")
        cssProperty(nodeData, elementImpl, "text-decoration")
        cssProperty(nodeData, elementImpl, "text-indent")
        cssProperty(nodeData, elementImpl, "text-shadow")
        cssProperty(nodeData, elementImpl, "text-transform")
        cssProperty(nodeData, elementImpl, "top")
        cssProperty(nodeData, elementImpl, "unicode-bidi")
        cssProperty(nodeData, elementImpl, "vertical-align")
        cssProperty(nodeData, elementImpl, "visibility")
        cssProperty(nodeData, elementImpl, "voice-family")
        cssProperty(nodeData, elementImpl, "volume")
        cssProperty(nodeData, elementImpl, "white-space")
        cssProperty(nodeData, elementImpl, "widows")
        cssProperty(nodeData, elementImpl, "word-spacing")
        cssProperty(nodeData, elementImpl, "z-index")
    }

    private fun cssProperty(nodeData: NodeData, elementImpl: ElementImpl, attribute: String) {
        elementImpl.setProperty(
            attribute,
            nodeData.getAsString(attribute, true)
        )
    }
}