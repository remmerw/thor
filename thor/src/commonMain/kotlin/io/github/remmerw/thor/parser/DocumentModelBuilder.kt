package io.github.remmerw.thor.parser

import io.github.remmerw.thor.dom.DOMImplementationImpl
import io.github.remmerw.thor.dom.HTMLDocumentImpl
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document
import org.xml.sax.EntityResolver
import org.xml.sax.ErrorHandler
import java.io.IOException
import java.io.InputStreamReader
import javax.xml.parsers.DocumentBuilder

class DocumentModelBuilder() : DocumentBuilder() {

    private var domImplementation: DOMImplementation? = null


    /**
     * Parses an HTML document. Note that this method will read the entire input
     * source before returning a `Document` instance.
     *
     * @param `is` The input source, which may be an instance of
     * [InputSourceImpl].
     * @see .createDocument
     */
    @Throws(Exception::class, IOException::class)
    fun parse(inputSource: InputSource): Document? {
        val document = this.createDocument(inputSource, "") as HTMLDocumentImpl
        document.load()
        return document
    }

    /**
     * Creates a document without parsing the input provided, so the document
     * object can be used for incremental rendering.
     *
     * @param `is` The input source, which may be an instance of
     * [InputSourceImpl]. The input
     * source must provide either an input stream or a reader.
     * @see HTMLDocumentImpl.load
     */
    @Throws(Exception::class, IOException::class)
    fun createDocument(inputSource: InputSource, contentType: String?): Document {
        val charset = inputSource.charset
        val uri = inputSource.uri
        val wis: WritableLineReader?
        val inputStream = inputSource.byteStream
        wis = WritableLineReader(InputStreamReader(inputStream, charset))

        val document = HTMLDocumentImpl(wis, uri!!, contentType)
        return document
    }

    override fun parse(p0: org.xml.sax.InputSource?): Document? {
        TODO("Not yet implemented")
    }

    override fun isNamespaceAware(): Boolean {
        return false
    }

    override fun isValidating(): Boolean {
        return false
    }

    override fun setEntityResolver(p0: EntityResolver?) {
        TODO("Not yet implemented")
    }

    override fun setErrorHandler(p0: ErrorHandler?) {
        TODO("Not yet implemented")
    }

    override fun newDocument(): Document {
        return HTMLDocumentImpl()
    }

    override fun getDOMImplementation(): DOMImplementation {
        synchronized(this) {
            if (this.domImplementation == null) {
                this.domImplementation = DOMImplementationImpl()
            }
            return this.domImplementation!!
        }
    }

}
