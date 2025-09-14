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

class TableTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test(): Unit = runBlocking(Dispatchers.IO) {

        val htmlModel = HtmlModel()
        launch {
            val model = htmlModel.model()

            val html = model.createEntity(Tag.HTML.tag())
            val body = model.createEntity(Tag.BODY.tag(), html)

            val table = model.createEntity(Tag.TABLE.tag(), body)

            val caption = model.createEntity(Tag.CAPTION.tag(), table)
            model.createText(caption, "Caption")
            val tr1 = model.createEntity(Tag.TR.tag(), table)
            val th1 = model.createEntity(Tag.TH.tag(), tr1)
            model.createText(th1, "Header 1")
            val th2 = model.createEntity(Tag.TH.tag(), tr1)
            model.createText(th2, "Header 2")

            val tr2 = model.createEntity(Tag.TR.tag(), table)
            val td1 = model.createEntity(Tag.TD.tag(), tr2)
            model.createText(td1, "Content 1")
            val td2 = model.createEntity(Tag.TD.tag(), tr2)
            model.createText(td2, "Content 2")

            val tr3 = model.createEntity(Tag.TR.tag(), table)
            val td3 = model.createEntity(Tag.TD.tag(), tr3)
            model.createText(td3, "Content 3")
            val td4 = model.createEntity(Tag.TD.tag(), tr3)
            model.createText(td4, "Content 4")


            println(model.content())
        }


        composeTestRule.setContent {
            val htmlModel: HtmlModel = viewModel { htmlModel }
            HtmlViewer(htmlModel)
        }


        delay(30000)

    }
}