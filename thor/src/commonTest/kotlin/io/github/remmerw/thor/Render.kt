package io.github.remmerw.thor

import io.github.remmerw.saga.Model
import io.github.remmerw.saga.attachToModel
import io.ktor.http.Url
import kotlinx.io.asSource
import kotlinx.io.buffered
import java.net.URL

class Render(var url: Url) {


    suspend fun parse(model: Model) {
        var urlObj: URL?
        try {
            urlObj = URL(url.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

        // Open a connection to the HTML page and use Cobra to parse it.
        // Cobra does not return until page is loaded.
        try {
            val connection = urlObj.openConnection()
            val inputStream = connection.getInputStream()
            requireNotNull(inputStream)

            attachToModel(inputStream.asSource().buffered(), model)
        } catch (e: Exception) {
            e.printStackTrace()
            println("parsePage($url):  $e")
            throw e
        }

    }
}