package io.github.remmerw.thor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class RenderTest {
    @Test
    fun renderTest(): Unit = runBlocking(Dispatchers.IO) {

        render()

    }
}