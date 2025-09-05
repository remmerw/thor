package io.github.remmerw.thor

import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull

class ParserTest {

    @Test
    fun parserTest(): Unit = runBlocking(Dispatchers.IO) {

        //val url = Url("http://www.benjysbrain.com/")
        val url = Url("https://www.welt.de/")
        val p = Render(url)
        val document = p.parsePage()
        assertNotNull(document)


    }
}