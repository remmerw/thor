package io.github.remmerw.thor

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.remmerw.thor.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import org.junit.runner.RunWith
import java.io.File
import kotlin.test.Test
import kotlin.use


@RunWith(AndroidJUnit4::class)
class ThorTest {

    @Test
    fun test(): Unit = runBlocking(Dispatchers.IO) {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val data = Res.readBytes("files/add-two.wasm")
        println(data.size)

        val file = File(appContext.filesDir, "add-two.wasm")
        file.createNewFile()
        file.writeBytes(data)

        thor().load(file)
    }
}