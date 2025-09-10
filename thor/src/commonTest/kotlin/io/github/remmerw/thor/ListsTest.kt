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

class ListsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test(): Unit = runBlocking(Dispatchers.IO) {

        val stateModel = StateModel()
        val model = stateModel.model()

        val html = model.createEntity(Type.HTML.name)
        val body = model.createEntity(Type.BODY.name, html)

        val ol = model.createEntity(Type.OL.name, body)
        val ol1 = model.createEntity(Type.LI.name, ol)
        model.createText(ol1, "OL1")
        val ol2 = model.createEntity(Type.LI.name, ol)
        model.createText(ol2, "OL2")
        val ol3 = model.createEntity(Type.LI.name, ol)
        model.createText(ol3, "OL2")


        val ul = model.createEntity(Type.UL.name, body)
        val ul1 = model.createEntity(Type.LI.name, ul)
        model.createText(ul1, "UL1")
        val ul2 = model.createEntity(Type.LI.name, ul)
        model.createText(ul2, "UL2")
        val ul3 = model.createEntity(Type.LI.name, ul)
        model.createText(ul3, "UL2")
        stateModel.setModel(model.entity())



        model.debug()


        composeTestRule.setContent {
            val stateModel: StateModel = viewModel { stateModel }
            HtmlViewer(stateModel)
        }


        delay(30000)

    }
}