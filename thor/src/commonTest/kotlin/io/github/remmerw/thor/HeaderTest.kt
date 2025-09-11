package io.github.remmerw.thor

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.remmerw.thor.model.HtmlModel
import io.github.remmerw.thor.model.Type
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

            val html = model.createEntity(Type.HTML.name)
            val body = model.createEntity(Type.BODY.name, html)
            val h1 = model.createEntity(Type.H1.name, body)
            model.createText(h1, "H1")
            val h2 = model.createEntity(Type.H2.name, body)
            model.createText(h2, "H2")
            val h3 = model.createEntity(Type.H3.name, body)
            model.createText(h3, "H3")
            val h4 = model.createEntity(Type.H4.name, body)
            model.createText(h4, "H4")
            val h5 = model.createEntity(Type.H5.name, body)
            model.createText(h5, "H5")
            val h6 = model.createEntity(Type.H6.name, body)
            model.createText(h6, "H6")
        }

        composeTestRule.setContent {
            val htmlModel: HtmlModel = viewModel { htmlModel }
            HtmlViewer(htmlModel)
        }


        delay(30000)

    }
}