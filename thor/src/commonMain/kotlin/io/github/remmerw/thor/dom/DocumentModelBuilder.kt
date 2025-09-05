package io.github.remmerw.thor.dom

import org.w3c.dom.Document
import java.io.InputStream
import java.io.InputStreamReader
import java.io.LineNumberReader

class DocumentModelBuilder() {

    fun parse(byteStream: InputStream, uri: String, charset: String): Document? {
        val document = this.createDocument(byteStream, uri, charset) as DocumentImpl
        document.load()
        return document
    }

    fun createDocument(byteStream: InputStream, uri: String, charset: String): Document {
        val wis = LineNumberReader(InputStreamReader(byteStream, charset))
        val document = DocumentImpl(wis, uri, charset)
        return document
    }


}
