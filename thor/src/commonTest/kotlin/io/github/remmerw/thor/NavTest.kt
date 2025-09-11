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

class NavTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test(): Unit = runBlocking(Dispatchers.IO) {

        val htmlModel = HtmlModel()

        val data = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h1>The nav element</h1>\n" +
                "\n" +
                "<p>The nav element defines a set of navigation links:</p>\n" +
                "\n" +
                "<nav>\n" +
                "<a href=\"/html/\">HTML</a> |\n" +
                "<a href=\"/css/\">CSS</a> |\n" +
                "<a href=\"/js/\">JavaScript</a> |\n" +
                "<a href=\"/python/\">Python</a>\n" +
                "\n" +
                "<nav>\n" +
                "<a href=\"/a/\">A</a> |\n" +
                "<a href=\"/b/\">B</a> |\n" +
                "<a href=\"/c/\">C</a> |\n" +
                "<a href=\"/d/\">D</a>\n" +
                "</nav>\n" +
                "</nav>\n" +
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