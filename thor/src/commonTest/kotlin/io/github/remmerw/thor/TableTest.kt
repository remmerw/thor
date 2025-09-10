package io.github.remmerw.thor

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.remmerw.thor.model.StateModel
import io.github.remmerw.thor.model.Type
import io.github.remmerw.thor.ui.HtmlViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import kotlin.test.Test

class TableTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test(): Unit = runBlocking(Dispatchers.IO) {

        val stateModel = StateModel()
        val model = stateModel.model()

        val html = model.createEntity(Type.HTML.name)
        val body = model.createEntity(Type.BODY.name, html)

        val table = model.createEntity(Type.TABLE.name, body)

        val caption = model.createEntity(Type.CAPTION.name, table)
        model.createText(caption, "Caption")
        val tr1 = model.createEntity(Type.TR.name, table)
        val th1 = model.createEntity(Type.TH.name, tr1)
        model.createText(th1, "Header 1")
        val th2 = model.createEntity(Type.TH.name, tr1)
        model.createText(th2, "Header 2")

        val tr2 = model.createEntity(Type.TR.name, table)
        val td1 = model.createEntity(Type.TD.name, tr2)
        model.createText(td1, "Content 1")
        val td2 = model.createEntity(Type.TD.name, tr2)
        model.createText(td2, "Content 2")

        val tr3 = model.createEntity(Type.TR.name, table)
        val td3 = model.createEntity(Type.TD.name, tr3)
        model.createText(td3, "Content 3")
        val td4 = model.createEntity(Type.TD.name, tr3)
        model.createText(td4, "Content 4")


        model.debug()


        composeTestRule.setContent {
            val stateModel: StateModel = viewModel { stateModel }
            HtmlViewer(stateModel)
        }


        delay(30000)

    }
}