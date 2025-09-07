package io.github.remmerw.thor.dom

import kotlinx.coroutines.flow.MutableStateFlow


abstract class Node(
    var model: Model?,
    val uid: Long,
    val name: String
) {
    init {
        if (model is Model) {
            (model as Model).addNode(this)
        }
    }

    val children = MutableStateFlow(mutableListOf<Entity>())


    internal fun appendChild(newChild: Node) {
        this.children.value.add(newChild.entity()) // todo fix
    }

    internal fun setModel(value: Model) {
        this.model = value
    }


    fun entity(): Entity {
        return Entity(uid, name)
    }

    override fun toString(): String {
        return "$name($uid)"
    }

    open fun debug() {
        if (children.value.isEmpty()) {
            println("<$name/>")
        } else {
            println("<$name>")
            children.value.forEach { entity ->
                model!!.node(entity).debug()
            }
            println("</$name>")
        }
    }
}

