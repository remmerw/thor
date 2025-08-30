package io.github.remmerw.thor.cobra.html.style

interface CSS2PropertiesContext {
    fun informLookInvalid()

    fun informSizeInvalid()

    fun informPositionInvalid()

    fun informLayoutInvalid()

    fun informInvalid()

    fun documentBaseURI(): String?
}
