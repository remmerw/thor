package io.github.remmerw.thor.dom

import io.ktor.http.Url
import java.io.InputStream
import java.io.InputStreamReader
import java.io.LineNumberReader

const val XHTML_STRICT_PUBLIC_ID = "-//W3C//DTD XHTML 1.0 Strict//EN"
const val XHTML_STRICT_SYS_ID = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"


fun parseDocument(byteStream: InputStream, url: Url, charset: String): Document {
    val reader = LineNumberReader(InputStreamReader(byteStream, charset))
    val document = Document(reader, url.toString())
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
