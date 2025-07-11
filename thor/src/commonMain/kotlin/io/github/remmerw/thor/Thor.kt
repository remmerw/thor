package io.github.remmerw.thor

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.remmerw.idun.Idun
import io.github.remmerw.thor.core.Bookmark
import io.github.remmerw.thor.core.Bookmarks
import io.github.remmerw.thor.core.Peers
import io.github.remmerw.thor.core.Task
import io.github.remmerw.thor.core.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import okio.Path.Companion.toPath


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect abstract class Context

abstract class Thor {

    internal abstract fun datastore(): DataStore<Preferences>
    internal abstract fun bookmarks(): Bookmarks
    internal abstract fun tasks(): Tasks

    abstract fun idun(): Idun
    abstract fun cacheDir(): Path

    suspend fun removeHomepage() {
        io.github.remmerw.thor.core.removeHomepage(datastore())
    }

    fun getHomepageUri(default: String): Flow<String> {
        return io.github.remmerw.thor.core.homepageUri(datastore(), default)
    }

    suspend fun setHomepage(uri: String, title: String, icon: ByteArray?) {
        io.github.remmerw.thor.core.homepage(datastore(), uri, title, icon)
    }


    suspend fun storeBookmark(bookmark: Bookmark) {
        bookmarks().insert(bookmark)
    }

    fun hasBookmark(url: String?): Flow<Boolean> {
        return bookmarks().hasBookmark(url)
    }

    suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarks().delete(bookmark)
    }

    suspend fun getBookmark(url: String): Bookmark? {
        return bookmarks().bookmark(url)
    }

    fun getBookmarks(): Flow<List<Bookmark>> {
        return bookmarks().bookmarks()
    }

    suspend fun reset() {
        tasks().reset()
        deleteRecursively(cacheDir(), false)
    }


    suspend fun startTask(task: Task, uuid: String) {
        tasks().active(task.id)
        tasks().work(task.id, uuid)
    }

    suspend fun removeTask(task: Task) {
       tasks().delete(task)
    }

    suspend fun cancelTask(task: Task) {
        tasks().inactive(task.id)
    }

    fun activeTasks(): Flow<Boolean> {
        return tasks().active()
    }

    fun getTasks(pid: Long): Flow<List<Task>> {
        return tasks().tasks(pid)
    }

    suspend fun purgeTasks() {
        tasks().purge()
    }

    suspend fun storeTask(task: Task){
        tasks().insert(task)
    }

    suspend fun setTaskWork(taskId: Long, uuid:String){
        tasks().work(taskId, uuid)
    }

    suspend fun setTaskActive(taskId: Long){
        tasks().active(taskId)
    }

    suspend fun setTaskInactive(taskId: Long){
        tasks().inactive(taskId)
    }

    suspend fun setTaskFinished(taskId: Long, url: String){
        tasks().finished(taskId, url)
    }

    suspend fun setTaskFinished(taskId: Long){
        tasks().finished(taskId)
    }

    suspend fun setTaskProgress(taskId: Long, progress: Float){
        tasks().progress(taskId, progress)
    }

    suspend fun getTask(taskId: Long) : Task{
        return tasks().task(taskId)
    }

    suspend fun createOrGetTask(
        pid: Long, name: String, mimeType: String, uri: String,
        size: Long, uuid: String
    ): Long {
        return tasks().createOrGet(pid, name, mimeType, uri, size, uuid)
    }
}

expect fun initializeThor(context: Context)


private fun deleteRecursively(path: Path, deleteDirectory: Boolean, mustExist: Boolean = false) {
    val isDirectory = SystemFileSystem.metadataOrNull(path)?.isDirectory ?: false
    if (isDirectory) {
        for (child in SystemFileSystem.list(path)) {
            deleteRecursively(child, true, mustExist)
        }
        if (deleteDirectory) {
            SystemFileSystem.delete(path, mustExist)
        }
    } else {
        SystemFileSystem.delete(path, mustExist)
    }
}

expect fun thor(): Thor


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object TasksConstructor : RoomDatabaseConstructor<Tasks> {
    override fun initialize(): Tasks
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object BookmarksConstructor : RoomDatabaseConstructor<Bookmarks> {
    override fun initialize(): Bookmarks
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object PeersConstructor : RoomDatabaseConstructor<Peers> {
    override fun initialize(): Peers
}

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

internal const val dataStoreFileName = "settings.preferences_pb"


fun peersDatabaseBuilder(
    builder: RoomDatabase.Builder<Peers>
): Peers {
    return builder
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}


fun tasksDatabaseBuilder(
    builder: RoomDatabase.Builder<Tasks>
): Tasks {
    return builder
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}


fun bookmarksDatabaseBuilder(
    builder: RoomDatabase.Builder<Bookmarks>
): Bookmarks {
    return builder
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}


