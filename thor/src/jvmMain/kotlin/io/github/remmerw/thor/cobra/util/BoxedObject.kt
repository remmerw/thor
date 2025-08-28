package io.github.remmerw.thor.cobra.util

class BoxedObject {
    var `object`: Any? = null
        set(`object`) {
            field = this.`object`
        }

    constructor()

    constructor(`object`: Any?) : super() {
        this.`object` = `object`
    }
}
