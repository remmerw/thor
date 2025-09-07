package io.github.remmerw.thor

import io.github.remmerw.thor.dom.Model
import io.github.remmerw.thor.dom.Element
import io.github.remmerw.thor.dom.Entity
import io.github.remmerw.thor.dom.Node
import io.github.remmerw.thor.dom.parseModel
import io.ktor.http.Url
import java.net.URL

class Render(var url: Url) {


    fun parse(model: Model) {
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

            parseModel( model, inputStream, "UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
            println("parsePage($url):  $e")
            throw e
        }

    }
}