package io.github.remmerw.thor.cobra.html.parser

import org.xml.sax.InputSource
import java.io.InputStream
import java.io.Reader

class InputSourceImpl : InputSource {

    constructor(byteStream: InputStream, uri: String, charset: String) : super(byteStream) {
        this.encoding = charset
        this.systemId = uri
        this.publicId = uri
    }
}
