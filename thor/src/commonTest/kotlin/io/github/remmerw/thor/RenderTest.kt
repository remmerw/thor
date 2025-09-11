package io.github.remmerw.thor

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.remmerw.thor.model.HtmlModel
import io.github.remmerw.thor.ui.HtmlViewer
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.io.readString
import org.junit.Rule
import java.net.URL
import kotlin.test.Test

class RenderTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun renderTest(): Unit = runBlocking(Dispatchers.IO) {

        //val url = Url("http://www.benjysbrain.com/")
        val url = Url("https://www.w3schools.com/")

        var data: String? = null
        val connection = URL(url.toString()).openConnection()
        connection.getInputStream().use { inputStream ->
            data = inputStream.asSource().buffered().readString()
        }

        checkNotNull(data)

        val htmlModel = HtmlModel()
        htmlModel.documentUri = url.toString()

        launch {
            val buffer = Buffer()
            buffer.write(data.encodeToByteArray())
            htmlModel.parse(buffer)
            htmlModel.model().debug()
        }


        composeTestRule.setContent {
            val htmlModel: HtmlModel = viewModel { htmlModel }
            HtmlViewer(htmlModel)
        }


        delay(100000)

    }
}