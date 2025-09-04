package io.github.remmerw.thor.style

import cz.vutbr.web.css.StyleSheet
import io.github.remmerw.thor.dom.ElementImpl
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.util.Locale
import java.util.Vector

/**
 * Borrowed from CSSBox HTMLNorm.java This class provides a mechanism of
 * converting some HTML presentation atributes to the CSS styles and other
 * methods related to HTML specifics.
 */
object StyleElements {
    fun convertAttributesToStyles(node: Node): StyleSheet? {
        if (node.nodeType == Node.ELEMENT_NODE) {
            val el = node as ElementImpl
            //Analyze HTML attributes
            var attrs = ""
            val tagName = el.tagName
            if ("TABLE".equals(tagName, ignoreCase = true)) {
                //setting table and cell borders
                attrs = getTableElementStyle(el, attrs)
            } else if ("FONT".equals(tagName, ignoreCase = true)) {
                //Text properties
                attrs = getFontElementStyle(el, attrs)
            } else if ("CANVAS".equals(tagName, ignoreCase = true)) {
                attrs = getCanvasElementStyle(el, attrs)
            } else if ("IMG".equals(tagName, ignoreCase = true)) {
                attrs = getElementDimensionStyle(el, attrs)
            }

            if (attrs.length > 0) {
                return CSSUtilities.parseInlineStyle(attrs, null, el, false)
            }
        }
        return null
    }

    private fun getCanvasElementStyle(el: ElementImpl, attrs: String): String {
        var attrs = attrs
        val widthNode = el.attributes.getNamedItem("width")
        if (widthNode != null) {
            attrs += "width: " + pixelise(widthNode.nodeValue) + ";"
        } else {
            attrs += "width: 300px;"
        }

        val heightNode = el.attributes.getNamedItem("height")
        if (heightNode != null) {
            attrs += "height: " + pixelise(heightNode.nodeValue) + ";"
        } else {
            attrs += "height: 150px;"
        }

        return attrs
    }

    private fun getElementDimensionStyle(el: ElementImpl, attrs: String): String {
        var attrs = attrs
        val widthNode = el.attributes.getNamedItem("width")
        if (widthNode != null) {
            attrs += "width: " + pixelise(widthNode.nodeValue) + ";"
        }

        val heightNode = el.attributes.getNamedItem("height")
        if (heightNode != null) {
            attrs += "height: " + pixelise(heightNode.nodeValue) + ";"
        }

        return attrs
    }

    private fun pixelise(value: String): String? {
        try {
            value.toInt()

            return value + "px"
        } catch (e: NumberFormatException) {
            return value
        }
    }

    private fun getTableElementStyle(el: Element, attrs: String): String {
        var attrs = attrs
        var border = "0"
        var frame = "void"

        //borders
        if (el.attributes.getNamedItem("border") != null) {
            border = el.getAttribute("border")
            if (border != "0") {
                frame = "border"
            }
        }
        if (el.attributes.getNamedItem("frame") != null) {
            frame = el.getAttribute("frame").lowercase(Locale.getDefault())
        }

        if (border != "0") {
            val fstyle = "border-@-style:solid;border-@-width:" + border + "px;"
            if (frame == "above") {
                attrs = attrs + applyBorders(fstyle, "top")
            }
            if (frame == "below") {
                attrs = attrs + applyBorders(fstyle, "bottom")
            }
            if (frame == "hsides") {
                attrs = attrs + applyBorders(fstyle, "left")
                attrs = attrs + applyBorders(fstyle, "right")
            }
            if (frame == "lhs") {
                attrs = attrs + applyBorders(fstyle, "left")
            }
            if (frame == "rhs") {
                attrs = attrs + applyBorders(fstyle, "right")
            }
            if (frame == "vsides") {
                attrs = attrs + applyBorders(fstyle, "top")
                attrs = attrs + applyBorders(fstyle, "bottom")
            }
            if (frame == "box") {
                attrs = appAllBorders(attrs, fstyle)
            }
            if (frame == "border") {
                attrs = appAllBorders(attrs, fstyle)
            }
        }
        return attrs
    }

    private fun appAllBorders(attrs: String, fstyle: String): String {
        var attrs = attrs
        attrs = attrs + applyBorders(fstyle, "left")
        attrs = attrs + applyBorders(fstyle, "right")
        attrs = attrs + applyBorders(fstyle, "top")
        attrs = attrs + applyBorders(fstyle, "bottom")
        return attrs
    }

    private fun getFontElementStyle(el: Element, attrs: String): String {
        var attrs = attrs
        if (el.attributes.getNamedItem("color") != null) {
            attrs = attrs + "color: " + el.getAttribute("color") + ";"
        }
        if (el.attributes.getNamedItem("face") != null) {
            attrs = attrs + "font-family: " + el.getAttribute("face") + ";"
        }
        if (el.attributes.getNamedItem("size") != null) {
            val sz = el.getAttribute("size")
            var ret = "normal"
            if (sz == "1") {
                ret = "xx-small"
            } else if (sz == "2") {
                ret = "x-small"
            } else if (sz == "3") {
                ret = "small"
            } else if (sz == "4") {
                ret = "normal"
            } else if (sz == "5") {
                ret = "large"
            } else if (sz == "6") {
                ret = "x-large"
            } else if (sz == "7") {
                ret = "xx-large"
            } else if (sz.startsWith("+")) {
                val sn = sz.substring(1).toInt()
                if ((sn > 0) && (sn <= 7)) {
                    ret = (100 + (sn * 20)).toString() + "%"
                }
            } else if (sz.startsWith("-")) {
                val sn = sz.substring(1).toInt()
                if ((sn > 0) && (sn <= 7)) {
                    ret = (100 - (sn * 10)).toString() + "%"
                }
            }
            attrs = attrs + "font-size: " + ret
        }
        return attrs
    }

    private fun applyBorders(template: String, dir: String): String {
        return template.replace("@".toRegex(), dir)
    }

    //======== Normalize the dom =============================
    /**
     * Provides a cleanup of a HTML DOM tree according to the HTML syntax
     * restrictions. Currently, following actions are implemented:
     *
     *  * Table cleanup
     *
     *  * elements that are not acceptable within a table are moved before the table
     *
     *
     *
     *
     * @param doc the processed DOM Document.
     */
    fun normalizeHTMLTree(doc: Document) {
        //normalize tables
        val tables = doc.getElementsByTagName("table")
        for (i in 0..<tables.length) {
            val nodes = Vector<Node>()
            recursiveFindBadNodesInTable(tables.item(i), null, nodes)
            for (n in nodes) {
                moveSubtreeBefore(n, tables.item(i))
            }
        }
    }

    /**
     * Finds all the nodes in a table that cannot be contained in the table
     * according to the HTML syntax.
     *
     * @param n        table root
     * @param cellroot last cell root
     * @param nodes    resulting list of nodes
     */
    private fun recursiveFindBadNodesInTable(n: Node, cellroot: Node?, nodes: Vector<Node>) {
        var cell = cellroot

        if (n.nodeType == Node.ELEMENT_NODE) {
            val tag = n.nodeName.lowercase(Locale.getDefault())
            if (tag == "table") {
                if (cell != null) { //do not enter nested tables
                    return
                }
            } else if (tag == "tbody" || tag == "thead" || tag == "tfoot"
                || tag == "tr" || tag == "col" || tag == "colgroup"
            ) {
            } else if (tag == "td" || tag == "th" || tag == "caption") {
                cell = n
            } else { //other elements
                if (cell == null) {
                    nodes.add(n)
                    return
                }
            }
        } else if (n.nodeType == Node.TEXT_NODE) { //other nodes
            if ((cell == null) && (n.nodeValue.trim { it <= ' ' }.length > 0)) {
                nodes.add(n)
                return
            }
        }

        val child = n.childNodes
        for (i in 0..<child.length) {
            recursiveFindBadNodesInTable(child.item(i), cell, nodes)
        }
    }

    private fun moveSubtreeBefore(root: Node, ref: Node) {
        root.parentNode.removeChild(root)
        ref.parentNode.insertBefore(root, ref)
    }
}