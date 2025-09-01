package io.github.remmerw.thor.style


class ListStyle {
    var type: Int = 0
    var position: Int = 0

    constructor(type: Int,  position: Int) : super() {
        this.type = type
        this.position = position
    }

    constructor()

    companion object {
        const val TYPE_UNSET: Int = 256
        const val TYPE_NONE: Int = 0
        const val TYPE_DISC: Int = 1
        const val TYPE_CIRCLE: Int = 2
        const val TYPE_SQUARE: Int = 3
        const val TYPE_DECIMAL: Int = 4
        const val TYPE_LOWER_ALPHA: Int = 5
        const val TYPE_UPPER_ALPHA: Int = 6
        const val TYPE_LOWER_LATIN: Int = 7
        const val TYPE_UPPER_LATIN: Int = 8

        const val POSITION_UNSET: Int = 0
        const val POSITION_INSIDE: Int = 0
        const val POSITION_OUTSIDE: Int = 0
    }
}
