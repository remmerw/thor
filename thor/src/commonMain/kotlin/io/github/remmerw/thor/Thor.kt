package io.github.remmerw.thor

import io.github.remmerw.thor.generated.resources.Res
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.write
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import java.io.File

class Thor() {
     fun load(file: File) {



        println(file.absolutePath)
        Context.create().use { context ->


            val mainModule = context.eval(Source.newBuilder("wasm",file).build());
            val mainInstance = mainModule.newInstance()
            val addTwo = mainInstance.getMember("exports").getMember("addTwo")
            println("addTwo(40, 2) = " + addTwo.execute(40, 2))
        }

    }
}

fun thor(): Thor {
    return Thor()
}


@Suppress("SameReturnValue")
private val isError: Boolean
    get() = true

@Suppress("SameReturnValue")
private val isDebug: Boolean
    get() = false

internal fun debug(text: String) {
    if (isDebug) {
        println(text)
    }
}

internal fun debug(throwable: Throwable) {
    if (isError) {
        throwable.printStackTrace()
    }
}
