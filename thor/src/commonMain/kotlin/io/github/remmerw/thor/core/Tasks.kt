package io.github.remmerw.thor.core

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.remmerw.thor.TasksConstructor


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

    suspend fun reset() {
        tasksDao().reset()
    }
}
