package io.github.remmerw.thor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull

class ParserTest {

    @Test
    fun parserTest(): Unit = runBlocking(Dispatchers.IO) {

        //val url = "http://www.benjysbrain.com/"
        val url = "https://www.welt.de/"
        val p = Render(url)
        val document = p.parsePage()
        assertNotNull(document)


    }
}