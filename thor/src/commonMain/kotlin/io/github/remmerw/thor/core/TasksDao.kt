package io.github.remmerw.thor.core

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM Task WHERE pid = :pid ORDER BY id DESC")
    fun tasks(pid: Long): Flow<List<Task>>

    @Query("UPDATE Task SET finished = 1, active = 0 WHERE id = :id")
    suspend fun finished(id: Long)

    @Query("UPDATE Task SET finished = 1, active = 0, uri = :uri WHERE id = :id")
    suspend fun finished(id: Long, uri: String)

    @Query("DELETE FROM Task WHERE active = 0 AND finished = 0")
    suspend fun purge()

    @Query("UPDATE Task SET work = :work WHERE id = :id")
    suspend fun work(id: Long, work: String)

    @Query("SELECT * FROM Task WHERE id = :id")
    suspend fun task(id: Long): Task

    @Query("SELECT id FROM Task WHERE pid = :pid AND name =:name")
    suspend fun parent(pid: Long, name: String): Long?

    @Query("UPDATE Task SET active = 1 WHERE id = :id")
    suspend fun active(id: Long)

    @Query("UPDATE Task SET active = 0 WHERE id = :id")
    suspend fun inactive(id: Long)

    @Query("UPDATE Task SET progress = :progress WHERE id = :id")
    suspend fun progress(id: Long, progress: Float)

    @Query("SELECT EXISTS (SELECT * FROM Task WHERE active = 1)")
    fun active(): Flow<Boolean>

    @Query("DELETE FROM Task")
    suspend fun reset()

}
