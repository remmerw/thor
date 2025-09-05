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

            document.getAnchors().toString()
            document.getApplets().toString()
            document.getForms().toString()
            document.getImages().toString()
            document.getLinks().toString()
            document.getElementsByName("hello").toString()
            document.getElementById("hello").toString()
            document.getDocumentUrl().toString()
            document.getDocumentHost().toString()
            document.inputEncoding
            document.getBody().toString()
            document.xmlVersion
            document.xmlEncoding
            document.xmlVersion
            document.isXML()

            println("done " + document.documentURI)

        }



    }
}