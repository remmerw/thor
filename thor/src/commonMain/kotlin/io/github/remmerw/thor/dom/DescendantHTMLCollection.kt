package io.github.remmerw.thor.dom

import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection

open class DescendantHTMLCollection @JvmOverloads constructor(
    private val rootNode: NodeImpl,
    private val nodeFilter: NodeFilter?,
    private val treeLock: Any,
    private val nestIntoMatchingNodes: Boolean = true
) : HTMLCollection {


    private var itemsByName: MutableMap<String?, ElementImpl?>? = null
    private var itemsByIndex: MutableList<NodeImpl?>? = null


    private fun ensurePopulatedImpl() {
        if (this.itemsByName == null) {
            val descendents =
                this.rootNode.getDescendants(this.nodeFilter!!, this.nestIntoMatchingNodes)
            this.itemsByIndex = descendents
            val size = descendents.size
            val itemsByName: MutableMap<String?, ElementImpl?> = HashMap((size * 3) / 2)
            this.itemsByName = itemsByName
            for (i in 0..<size) {
                val descNode = descendents!!.get(i)
                if (descNode is ElementImpl) {
                    val id = descNode.getId()
                    if ((id != null) && (id.length != 0)) {
                        itemsByName.put(id, descNode)
                    }
                    val name = descNode.getAttribute("name")
                    if ((name != null) && (name.length != 0) && (name != id)) {
                        itemsByName.put(name, descNode)
                    }
                }
            }
        }
    }

    override fun getLength(): Int {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            return this.itemsByIndex!!.size
        }
    }

    override fun item(index: Int): Node? {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            return try {
                this.itemsByIndex!!.get(index)
            } catch (_: Throwable) {
                null
            }
        }
    }

    // TODO: This is a quick hack. Need to support WEB-IDL Semantics. GH #67
    fun item(obj: Any?): Node? {
        if (obj is Int) {
            return item(obj)
        }
        return item(0)
    }


    override fun namedItem(name: String?): Node? {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            return this.itemsByName!!.get(name)
        }
    }

}
