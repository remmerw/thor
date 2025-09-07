package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.MutableStateFlow


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

    val children = MutableStateFlow(mutableListOf<Entity>())

    fun children(): List<Node> {
        return children.value.map { entity ->
            document!!.node(entity)
        }
    }


    internal fun appendChild(newChild: Node) {
        this.children.value.add(newChild.entity()) // todo fix
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

