package io.github.remmerw.thor

import io.github.remmerw.thor.dom.DocumentImpl
import io.github.remmerw.thor.dom.NodeImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull

class ParserTest {

    @Test
    fun parserTest(): Unit = runBlocking(Dispatchers.IO) {

        val url = "http://www.benjysbrain.com/"
        //val url = "https://www.welt.de/"
        val p = Render(url)
        val document = p.parsePage()
        assertNotNull(document)


    }
}