package io.github.remmerw.thor.parser

import io.github.remmerw.thor.dom.HTMLDocumentImpl
import org.w3c.dom.Document
import java.io.InputStreamReader

class DocumentModelBuilder()  {

    fun parse(inputSource: InputSource): Document? {
        val document = this.createDocument(inputSource, "") as HTMLDocumentImpl
        document.load()
        return document
    }

    fun createDocument(inputSource: InputSource, contentType: String?): Document {
        val charset = inputSource.charset
        val uri = inputSource.uri

        val inputStream = inputSource.byteStream
        val wis = WritableLineReader(InputStreamReader(inputStream, charset))

        val document = HTMLDocumentImpl(wis, uri, contentType)
        return document
    }


}
