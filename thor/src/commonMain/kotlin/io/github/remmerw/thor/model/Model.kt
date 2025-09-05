package io.github.remmerw.thor.model


interface NodeModel {
    fun uid(): Long
    fun name(): String
}

data class TodoModel(val uid: Long, val name: String) : NodeModel {
    override fun uid(): Long {
        return uid
    }

    override fun name(): String {
        return name
    }
}

data class ElementModel(val uid: Long, val name: String) :
    NodeModel {

    override fun uid(): Long {
        return uid
    }

    override fun name(): String {
        return name
    }
}

data class TextModel(val uid: Long, val name: String, val text: String) : NodeModel {
    override fun uid(): Long {
        return uid
    }

    override fun name(): String {
        return name
    }

    fun text(): String {
        return text
    }

}
