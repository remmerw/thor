package org.kmp.testing

import androidx.test.core.app.ApplicationProvider
import io.github.remmerw.thor.Context

actual fun context(): Context {
    return ApplicationProvider.getApplicationContext()
}