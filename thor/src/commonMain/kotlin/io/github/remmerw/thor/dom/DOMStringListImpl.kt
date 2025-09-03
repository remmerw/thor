package io.github.remmerw.thor.dom

import org.w3c.dom.DOMStringList

class DOMStringListImpl(val sourceList: List<String>) : DOMStringList {

    override fun item(index: Int): String? {
        return this.sourceList[index]
    }

    override fun getLength(): Int {
        return this.sourceList.size
    }

    override fun contains(str: String?): Boolean {
        return this.sourceList.contains(str)
    }
}
