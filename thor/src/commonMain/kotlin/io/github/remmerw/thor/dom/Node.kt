package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.MutableStateFlow


abstract class Node(
    var model: Model?,
    val parent: Node?,
    val uid: Long,
    val name: String
) {
    init {
        if (model is Model) {
            (model as Model).addNode(this)
        }
    }

    val children = MutableStateFlow(mutableListOf<Entity>())

    fun children(): List<Node> {
        return children.value.map { entity ->
            model!!.node(entity)
        }
    }


    internal fun appendChild(newChild: Node) {
        this.children.value.add(newChild.entity()) // todo fix
    }

    fun getDocumentOwner(): Model {
        return model!!
    }

    internal fun setOwnerDocument(value: Model) {
        this.model = value
    }


    fun entity(): Entity {
        return Entity(uid, name)
    }

    override fun toString(): String {
        return "$name($uid)"
    }
}

