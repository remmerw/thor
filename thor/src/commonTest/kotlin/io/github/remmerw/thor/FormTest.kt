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
    fun test(): Unit = runBlocking(Dispatchers.IO) {

        val htmlModel = HtmlModel()

        val data = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h1>The label element</h1>\n" +
                "\n" +
                "<p>Click on one of the text labels to toggle the related radio button:</p>\n" +
                "\n" +
                "<form action=\"/action_page.php\">\n" +
                "  <input type=\"radio\" id=\"html\" name=\"fav_language\" value=\"HTML\">\n" +
                "  <label for=\"html\">HTML</label><br>\n" +
                "  <input type=\"radio\" id=\"css\" name=\"fav_language\" value=\"CSS\">\n" +
                "  <label for=\"css\">CSS</label><br>\n" +
                "  <input type=\"radio\" id=\"javascript\" name=\"fav_language\" value=\"JavaScript\">\n" +
                "  <label for=\"javascript\">JavaScript</label><br><br>\n" +
                "  <input type=\"submit\" value=\"Submit\">\n" +
                "</form>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n"

        launch {
            val model = htmlModel.model()

            val buffer = Buffer()
            buffer.write(data.encodeToByteArray())
            htmlModel.parse(buffer)

            model.debug()
        }

        composeTestRule.setContent {
            val htmlModel: HtmlModel = viewModel { htmlModel }
            HtmlViewer(htmlModel)
        }


        delay(30000)

    }
}