package io.github.remmerw.thor.dom

import java.io.InputStream
import java.io.InputStreamReader
import java.io.LineNumberReader

class DocumentModelBuilder() {

    fun parse(byteStream: InputStream, uri: String, charset: String): DocumentImpl? {
        val document = this.createDocument(byteStream, uri, charset)
        document.load()
        return document
    }

    fun createDocument(byteStream: InputStream, uri: String, charset: String): DocumentImpl {
        val reader = LineNumberReader(InputStreamReader(byteStream, charset))
        val document = DocumentImpl(reader, uri, charset)
        return document
    }


}
