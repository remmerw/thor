package io.github.remmerw.thor

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.remmerw.saga.createModel
import io.github.remmerw.thor.model.StateModel
import io.github.remmerw.thor.model.Type
import io.github.remmerw.thor.ui.HtmlViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import kotlin.test.Test

class AnchorTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test(): Unit = runBlocking(Dispatchers.IO) {

        val stateModel =  StateModel()
        val model = createModel()

        val html = model.createEntity(Type.HTML.name)
        val body = model.createEntity(Type.BODY.name, html)
        val a = model.createEntity(
            Type.A.name, body,
            mapOf("href" to "https://www.w3schools.com")
        )
        model.createText(a, "Visit W3Schools.com!")

        model.debug()


        composeTestRule.setContent {

            val stateModel: StateModel = viewModel { stateModel }

            stateModel.setModel(model.entity())


            HtmlViewer(stateModel)
        }


        delay(30000)

    }
}