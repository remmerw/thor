package io.github.remmerw.thor.cobra.html.renderer

internal class SizeExceededException : RuntimeException {
    constructor() : super()

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(message: String?) : super(message)

    constructor(cause: Throwable?) : super(cause)

    companion object {
        private const val serialVersionUID = 5789004695720876706L
    }
}
