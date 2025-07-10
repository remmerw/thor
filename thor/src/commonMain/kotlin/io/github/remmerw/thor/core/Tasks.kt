package io.github.remmerw.thor.core

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.remmerw.thor.TasksConstructor
import kotlinx.coroutines.flow.Flow


@Database(entities = [Task::class], version = 1, exportSchema = false)
@ConstructedBy(TasksConstructor::class)
abstract class Tasks : RoomDatabase() {
    abstract fun tasksDao(): TasksDao

    suspend fun createOrGet(
        pid: Long, name: String, mimeType: String, uri: String,
        size: Long, uuid: String
    ): Long {
        val id = tasksDao().parent(pid, name)
        if (id != null) {
            return id
        }
        return tasksDao().insert(
            Task(
                0, pid, name, mimeType, uri,
                size, uuid,
                active = false,
                finished = false,
                progress = 0f
            )
        )
    }

    fun active(): Flow<Boolean> {
        return tasksDao().active()
    }

    suspend fun progress(id: Long, progress: Float) {
        tasksDao().progress(id, progress)
    }

    suspend fun inactive(id: Long) {
        tasksDao().inactive(id)
    }

    suspend fun active(id: Long) {
        tasksDao().active(id)
    }

    suspend fun parent(pid: Long, name: String): Long? {
        return tasksDao().parent(pid, name)
    }

    suspend fun task(id: Long): Task {
        return tasksDao().task(id)
    }

    suspend fun work(id: Long, work: String) {
        tasksDao().work(id, work)
    }

    suspend fun purge() {
        tasksDao().purge()
    }

    suspend fun finished(id: Long, uri: String) {
        tasksDao().finished(id, uri)
    }

    suspend fun finished(id: Long) {
        tasksDao().finished(id)
    }

    fun tasks(pid: Long): Flow<List<Task>> {
        return tasksDao().tasks(pid)
    }

    suspend fun insert(task: Task): Long {
        return tasksDao().insert(task)
    }

    suspend fun delete(task: Task) {
        tasksDao().delete(task)
    }

    suspend fun reset() {
        tasksDao().reset()
    }
}
