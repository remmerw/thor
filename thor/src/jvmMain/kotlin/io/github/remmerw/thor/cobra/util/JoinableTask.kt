package io.github.remmerw.thor.cobra.util

/**
 * A task that can be used in a thread or thread pool. The caller can wait for
 * the task to finish by joining it.
 */
abstract class JoinableTask : SimpleThreadPoolTask {
    private var done = false

    override fun run() {
        try {
            this.execute()
        } finally {
            synchronized(this) {
                this.done = true
                (this as Object).notifyAll()
            }
        }
    }

    fun forceDone() {
        synchronized(this) {
            this.done = true
            (this as Object).notifyAll()
        }
    }

    @Throws(InterruptedException::class)
    fun join() {
        synchronized(this) {
            while (!this.done) {
                (this as Object).wait()
            }
        }
    }

    override fun cancel() {
        this.forceDone()
    }

    protected abstract fun execute()
}
