package io.github.remmerw.thor.parser

import org.xml.sax.InputSource
import java.io.InputStream

class InputSourceImpl : InputSource {

    constructor(byteStream: InputStream, uri: String, charset: String) : super(byteStream) {
        this.encoding = charset
        this.systemId = uri
        this.publicId = uri
    }
}
