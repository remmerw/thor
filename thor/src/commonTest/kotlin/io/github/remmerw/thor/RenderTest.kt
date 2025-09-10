package io.github.remmerw.thor

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.remmerw.thor.model.StateModel
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

        val url = Url("http://www.benjysbrain.com/")
        //val url = Url("https://www.welt.de/")

        var data: String? = null
        val connection = URL(url.toString()).openConnection()
        connection.getInputStream().use { inputStream ->
            data = inputStream.asSource().buffered().readString()
        }

        checkNotNull(data)

        val stateModel = StateModel()
        stateModel.documentUri = url.toString()

        launch {
            val buffer = Buffer()
            buffer.write(data.encodeToByteArray())
            stateModel.parse(buffer)
            stateModel.model().debug()
        }


        composeTestRule.setContent {
            val stateModel: StateModel = viewModel { stateModel }
            HtmlViewer(stateModel)
        }


        delay(100000)

    }
}