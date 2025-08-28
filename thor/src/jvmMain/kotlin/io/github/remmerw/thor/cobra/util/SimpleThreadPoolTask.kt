package io.github.remmerw.thor.cobra.util

interface SimpleThreadPoolTask : Runnable {
    fun cancel()
}
