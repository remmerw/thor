package io.github.remmerw.thor.dom


class NodeList(val list: List<Node>) {

    fun getLength(): Int {
        return this.list.size
    }

    fun item(index: Int): Node? {
        return this.list[index]
    }

    override fun toString(): String {
        return list.toString()
    }
}
