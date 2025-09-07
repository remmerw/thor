package io.github.remmerw.thor

import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull

class ParserTest {

    @Test
    fun parserTest(): Unit = runBlocking(Dispatchers.IO) {

        val urls = listOf("http://www.benjysbrain.com/",
            "https://www.welt.de/",
            "https://www.spiegel.de/",
            "https://www.handelsblatt.de/",
            "https://www.zeit.de/")

        urls.forEach { url ->
            val p = Render(Url(url))
            val document = p.parsePage()
            assertNotNull(document)

            document.getDoctype().toString()
            document.getBaseUri()


            document.getXmlEncoding()
            document.isXML()

            println("done " + document.getDocumentURI())

        }



    }
}