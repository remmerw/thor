package io.github.remmerw.thor

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.remmerw.thor.model.HtmlModel
import io.github.remmerw.thor.ui.HtmlViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import org.junit.Rule
import kotlin.test.Test

class FormTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun internalCss(): Unit = runBlocking(Dispatchers.IO) {

        val htmlModel = HtmlModel()

        val data = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "body {background-color: powderblue;}\n" +
                "h1   {color: blue;}\n" +
                "p    {color: red;}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>This is a heading</h1>\n" +
                "<p>This is a paragraph.</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n"

        launch {
            val model = htmlModel.model()

            val buffer = Buffer()
            buffer.write(data.encodeToByteArray())
            htmlModel.parse(buffer)

            println(model.content())
        }

        composeTestRule.setContent {
            val htmlModel: HtmlModel = viewModel { htmlModel }
            HtmlViewer(htmlModel)
        }


        delay(30000)

    }
}