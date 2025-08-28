package io.github.remmerw.thor.cobra.util.gui

import java.util.concurrent.Future

interface DefferedLayoutSupport {
    fun layoutCompletion(): Future<Boolean?>?
}
