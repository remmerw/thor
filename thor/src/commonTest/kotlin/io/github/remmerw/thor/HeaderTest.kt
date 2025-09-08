package io.github.remmerw.thor

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.remmerw.saga.attachToModel
import io.github.remmerw.saga.createModel
import io.github.remmerw.thor.model.StateModel
import io.github.remmerw.thor.ui.HtmlViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.junit.Rule
import kotlin.test.Test

class HeaderTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val path: String = "src/commonTest/resources"

    @Test
    fun renderTest(): Unit = runBlocking(Dispatchers.IO) {

        val resources = Path(path)

        val file = Path(resources, "header.html")

        val model = createModel()
        SystemFileSystem.source(file).buffered().use { source ->
            attachToModel(source, model)
        }

        composeTestRule.setContent {

            val stateModel: StateModel = viewModel { StateModel() }

            stateModel.setModel(model)


            HtmlViewer(stateModel)
        }


        delay(30000)

    }
}