package io.github.remmerw.thor.style

interface CSS2PropertiesContext {
    fun informLookInvalid()

    fun informSizeInvalid()

    fun informPositionInvalid()

    fun informLayoutInvalid()

    fun informInvalid()

    fun documentBaseURI(): String?
}
