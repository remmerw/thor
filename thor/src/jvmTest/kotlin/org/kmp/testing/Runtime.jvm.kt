package org.kmp.testing

import io.github.remmerw.thor.Context
import io.github.remmerw.thor.JvmContext

actual fun context(): Context {
    return JvmContext
}