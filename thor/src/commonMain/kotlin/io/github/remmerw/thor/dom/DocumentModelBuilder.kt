package io.github.remmerw.thor.dom

import io.ktor.http.Url
import java.io.InputStream
import java.io.InputStreamReader
import java.io.LineNumberReader


fun parseDocument(byteStream: InputStream, url: Url, charset: String): DocumentImpl {
    val reader = LineNumberReader(InputStreamReader(byteStream, charset))
    val document = DocumentImpl(reader, url, charset)
    document.load()
    return document
}


@Suppress("SameReturnValue")
private val isError: Boolean
    get() = true

@Suppress("SameReturnValue")
private val isDebug: Boolean
    get() = false

internal fun debug(text: String) {
    if (isDebug) {
        println(text)
    }
}

internal fun debug(throwable: Throwable) {
    if (isError) {
        throwable.printStackTrace()
    }
}
