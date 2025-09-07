package io.github.remmerw.thor.dom


abstract class Node(
    var document: Document?,
    val parent: Node?,
    val uid: Long,
    val name: String
) {
    init {
        if (document is Document) {
            (document as Document).addNode(this)
        }
    }

    private val children = mutableListOf<Node>()

    fun children(): List<Node> {
        return children
    }

    internal fun appendChild(newChild: Node) {
        this.children.add(newChild)
    }

    fun getDocumentOwner(): Document {
        return document!!
    }

    internal fun setOwnerDocument(value: Document) {
        this.document = value
    }


    fun entity(): Entity {
        return Entity(uid, name)
    }

    override fun toString(): String {
        return "$name($uid)"
    }
}

