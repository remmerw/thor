package io.github.remmerw.thor.dom

import java.io.InputStream
import java.io.InputStreamReader
import java.io.LineNumberReader

const val XHTML_STRICT_PUBLIC_ID = "-//W3C//DTD XHTML 1.0 Strict//EN"
const val XHTML_STRICT_SYS_ID = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"


fun createModel(): Model {
    return Model()
}

fun parseModel(
    model: Model = createModel(),
    inputStream: InputStream,
    charset: String
): Model {
    val reader = LineNumberReader(InputStreamReader(inputStream, charset))
    reader.use { reader ->
        val parser = HtmlParser(
            model = model,
            isXML = false
        )
        parser.parse(reader)
    }
    return model
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
