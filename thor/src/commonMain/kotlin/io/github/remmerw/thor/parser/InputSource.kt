package io.github.remmerw.thor.parser

import java.io.InputStream

data class InputSource(val byteStream: InputStream, val uri: String, val charset: String)
