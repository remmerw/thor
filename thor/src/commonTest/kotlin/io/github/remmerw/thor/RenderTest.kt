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
import org.junit.Rule
import kotlin.test.Test

class RenderTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun renderTest(): Unit = runBlocking(Dispatchers.IO) {

        val url = Url("http://www.benjysbrain.com/")

        //val url = Url("https://www.welt.de/")
        val stateModel = StateModel()
        //launch {
            stateModel.parse(url)
            val model = stateModel.model()
            model.debug()

       // }



        composeTestRule.setContent {
            val stateModel: StateModel = viewModel { stateModel }
            HtmlViewer(stateModel)
        }


        delay(100000)

    }
}