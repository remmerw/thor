package io.github.remmerw.thor

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.remmerw.saga.Tag
import io.github.remmerw.thor.model.HtmlModel
import io.github.remmerw.thor.ui.HtmlViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import kotlin.test.Test

class AnchorTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test(): Unit = runBlocking(Dispatchers.IO) {

        val htmlModel = HtmlModel()

        launch {
            val model = htmlModel.model()
            val html = model.createEntity(Tag.HTML.tag())
            val body = model.createEntity(Tag.BODY.tag(), html)
            val a = model.createEntity(
                Tag.A.tag(), body,
                mapOf("href" to "https://www.w3schools.com")
            )
            model.createText(a, "Visit W3Schools.com!")

            println(model.content())
        }

        composeTestRule.setContent {
            val htmlModel: HtmlModel = viewModel { htmlModel }
            HtmlViewer(htmlModel)
        }


        delay(30000)

    }
}