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

class HeaderTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun renderTest(): Unit = runBlocking(Dispatchers.IO) {

        val htmlModel = HtmlModel()

        launch {
            val model = htmlModel.model()

            val html = model.createEntity(Tag.HTML.tag())
            val body = model.createEntity(Tag.BODY.tag(), html)
            val h1 = model.createEntity(Tag.H1.tag(), body)
            model.createText(h1, "H1")
            val h2 = model.createEntity(Tag.H2.tag(), body)
            model.createText(h2, "H2")
            val h3 = model.createEntity(Tag.H3.tag(), body)
            model.createText(h3, "H3")
            val h4 = model.createEntity(Tag.H4.tag(), body)
            model.createText(h4, "H4")
            val h5 = model.createEntity(Tag.H5.tag(), body)
            model.createText(h5, "H5")
            val h6 = model.createEntity(Tag.H6.tag(), body)
            model.createText(h6, "H6")
        }

        composeTestRule.setContent {
            val htmlModel: HtmlModel = viewModel { htmlModel }
            HtmlViewer(htmlModel)
        }


        delay(30000)

    }
}