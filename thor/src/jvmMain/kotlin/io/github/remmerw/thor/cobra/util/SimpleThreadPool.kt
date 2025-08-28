package io.github.remmerw.thor.cobra.util

import java.util.LinkedList
import java.util.logging.Level
import java.util.logging.Logger

/**
 * A thread pool that allows cancelling all running tasks without shutting down
 * the thread pool.
 */
class SimpleThreadPool // Thread group needed so item requests
// don't get assigned sub-thread groups.
// TODO: Thread group needs to be thought through. It's retained in
// memory, and we need to return the right one in the GUI thread as well.
// new ThreadGroup(name);
    (
    private val name: String?,
    private val minThreads: Int,
    private val maxThreads: Int,
    private val idleAliveMillis: Int
) {
    private val taskList = LinkedList<SimpleThreadPoolTask?>()
    private val runningSet: MutableSet<SimpleThreadPoolTask?> = HashSet<SimpleThreadPoolTask?>()
    private val taskMonitor = Any()
    private val threadGroup: ThreadGroup? = null

    private var numThreads = 0
    private var numIdleThreads = 0
    private var threadNumber = 0

    fun schedule(task: SimpleThreadPoolTask) {
        requireNotNull(task) { "null task" }
        val monitor = this.taskMonitor
        synchronized(monitor) {
            if (this.numIdleThreads == 0) {
                this.addThreadImpl()
            }
            this.taskList.add(task)
            (monitor as Object).notify()
        }
    }

    fun cancel(task: SimpleThreadPoolTask) {
        synchronized(this.taskMonitor) {
            this.taskList.remove(task)
        }
        task.cancel()
    }

    private fun addThreadImpl() {
        if (this.numThreads < this.maxThreads) {
            val t = Thread(this.threadGroup, ThreadRunnable(), this.name + this.threadNumber++)
            t.isDaemon = true
            t.start()
            this.numThreads++
        }
    }

    /**
     * Cancels all waiting tasks and any currently running task.
     */
    fun cancelAll() {
        synchronized(this.taskMonitor) {
            this.taskList.clear()
            val i = this.runningSet.iterator()
            while (i.hasNext()) {
                i.next()!!.cancel()
            }
        }
    }

    private inner class ThreadRunnable : Runnable {
        override fun run() {
            val monitor = taskMonitor
            val tl = taskList
            val rs = runningSet
            val iam = idleAliveMillis
            var task: SimpleThreadPoolTask? = null
            while (true) {
                try {
                    synchronized(monitor) {
                        if (task != null) {
                            rs.remove(task)
                        }
                        numIdleThreads++
                        try {
                            var waitBase = System.currentTimeMillis()
                            INNER@ while (tl.isEmpty()) {
                                val maxWait = iam - (System.currentTimeMillis() - waitBase)
                                if (maxWait <= 0) {
                                    if (numThreads > minThreads) {
                                        // Should be only way to exit thread.
                                        numThreads--
                                        return
                                    } else {
                                        waitBase = System.currentTimeMillis()
                                        continue@INNER
                                    }
                                }
                                (monitor as Object).wait(maxWait)
                            }
                        } finally {
                            numIdleThreads--
                        }
                        task = taskList.removeFirst()
                        rs.add(task)
                    }
                    val currentThread = Thread.currentThread()
                    val baseName = currentThread.name
                    try {
                        try {
                            currentThread.name = baseName + ":" + task.toString()
                        } catch (thrown: Exception) {
                            logger.log(Level.WARNING, "run(): Unable to set task name.", thrown)
                        }
                        try {
                            task!!.run()
                        } catch (thrown: Exception) {
                            logger.log(Level.SEVERE, "run(): Error in task: " + task + ".", thrown)
                        }
                    } finally {
                        currentThread.name = baseName
                    }
                } catch (thrown: Exception) {
                    logger.log(
                        Level.SEVERE,
                        "run(): Error in thread pool: " + this@SimpleThreadPool.name + ".",
                        thrown
                    )
                }
            }
        }
    }

    companion object {
        private val logger: Logger = Logger.getLogger(SimpleThreadPool::class.java.name)
    }
}
