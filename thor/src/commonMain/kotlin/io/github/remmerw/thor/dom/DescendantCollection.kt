package io.github.remmerw.thor.dom


interface Collection {
    fun getLength(): Int

    fun item(index: Int): Node?

    fun namedItem(name: String): Node?
}

open class DescendantHTMLCollection(
    private val rootNode: Node,
    private val nodeFilter: NodeFilter?,
    private val nestIntoMatchingNodes: Boolean = true
) : Collection { // TODO cleanup


    private var itemsByName: MutableMap<String?, Element?>? = null
    private var itemsByIndex: MutableList<Node>? = null


    private fun ensurePopulatedImpl() {
        if (this.itemsByName == null) {
            val descendents =
                this.rootNode.getDescendants(this.nodeFilter!!, this.nestIntoMatchingNodes)
            this.itemsByIndex = descendents
            val size = descendents.size
            val itemsByName: MutableMap<String?, Element?> = HashMap((size * 3) / 2)
            this.itemsByName = itemsByName
            for (i in 0..<size) {
                val descNode = descendents[i]
                if (descNode is Element) {
                    val id = descNode.getAttribute("id")
                    if ((id != null) && (id.isNotEmpty())) {
                        itemsByName.put(id, descNode)
                    }
                    val name = descNode.getAttribute("name")
                    if ((name != null) && (name.isNotEmpty()) && (name != id)) {
                        itemsByName.put(name, descNode)
                    }
                }
            }
        }
    }

    override fun getLength(): Int {

        this.ensurePopulatedImpl()
        return this.itemsByIndex!!.size

    }

    override fun item(index: Int): Node? {

        this.ensurePopulatedImpl()
        return try {
            this.itemsByIndex!![index]
        } catch (_: Throwable) {
            null
        }

    }


    override fun namedItem(name: String): Node? {

        this.ensurePopulatedImpl()
        return this.itemsByName!![name]

    }

}
