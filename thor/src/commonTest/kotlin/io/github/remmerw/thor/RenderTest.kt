package io.github.remmerw.thor

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.remmerw.thor.model.StateModel
import io.github.remmerw.thor.ui.HtmlViewer
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import kotlin.test.Test

class RenderTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun renderTest(): Unit = runBlocking(Dispatchers.IO) {

        val url = Url("http://www.benjysbrain.com/")
        val p = Render(url)
        val document = p.parsePage()

        composeTestRule.setContent {

            val stateModel: StateModel = viewModel { StateModel() }
            stateModel.documentUri = url.toString()
            stateModel.setDocument(document)


            HtmlViewer(stateModel)
        }


        delay(100000)

    }
}