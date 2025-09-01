package io.github.remmerw.thor.parser

import java.io.IOException
import java.io.Reader

class EmptyReader : Reader() {
    @Throws(IOException::class)
    override fun close() {
    }

    @Throws(IOException::class)
    override fun read(cbuf: CharArray?, off: Int, len: Int): Int {
        return 0
    }
}