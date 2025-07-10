package io.github.remmerw.thor

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.remmerw.idun.Idun
import io.github.remmerw.thor.core.Bookmarks
import io.github.remmerw.thor.core.Peers
import io.github.remmerw.thor.core.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import okio.Path.Companion.toPath


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect abstract class Context

interface Thor {

    fun datastore(): DataStore<Preferences>
    fun tasks(): Tasks
    fun bookmarks(): Bookmarks
    fun idun(): Idun
    fun cacheDir(): Path
}

expect fun initializeThor(context: Context)


fun deleteRecursively(path: Path, deleteDirectory: Boolean, mustExist: Boolean = false) {
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


@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object TasksConstructor : RoomDatabaseConstructor<Tasks> {
    override fun initialize(): Tasks
}

@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object BookmarksConstructor : RoomDatabaseConstructor<Bookmarks> {
    override fun initialize(): Bookmarks
}

@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
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


