package io.github.remmerw.thor

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.remmerw.asen.bootstrap
import io.github.remmerw.idun.Idun
import io.github.remmerw.idun.newIdun
import io.github.remmerw.thor.core.Bookmarks
import io.github.remmerw.thor.core.Peers
import io.github.remmerw.thor.core.Tasks
import kotlinx.coroutines.flow.Flow
import kotlinx.io.files.Path
import java.io.File

private var thor: Thor? = null


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual abstract class Context
object JvmContext : Context()

internal class JvmThor(
    private val datastore: DataStore<Preferences>,
    private val tasks: Tasks,
    private val bookmarks: Bookmarks,
    private val idun: Idun
) : Thor {


    override fun tasks(): Tasks {
        return tasks
    }

    override fun bookmarks(): Bookmarks {
        return bookmarks
    }

    override fun idun(): Idun {
        return idun
    }

    override fun cacheDir(): Path {
        val temp = File(System.getProperty("java.io.tmpdir"))
        return Path(temp.absolutePath)
    }

    override suspend fun removeHomepage() {
        io.github.remmerw.thor.core.removeHomepage(datastore)
    }

    override fun homepageUri(default: String): Flow<String> {
        return io.github.remmerw.thor.core.homepageUri(datastore, default)
    }

    override suspend fun homepage(uri: String, title: String, icon: ByteArray?) {
        io.github.remmerw.thor.core.homepage(datastore, uri, title, icon)
    }

}


actual fun thor(): Thor = thor!!


actual fun initializeThor(context: Context) {
    val datastore = createDataStore()
    val tasks = createTasks()
    val peers = createPeers()
    val bookmarks = createBookmarks()
    val idun = newIdun(
        peerStore = peers,
        bootstrap = bootstrap()
    )
    thor = JvmThor(datastore, tasks, bookmarks, idun)
}


fun peersBuilder(): RoomDatabase.Builder<Peers> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "peers.db")
    return Room.databaseBuilder<Peers>(
        name = dbFile.absolutePath
    )
}

fun createPeers(): Peers {
    return peersDatabaseBuilder(peersBuilder())
}


fun tasksBuilder(): RoomDatabase.Builder<Tasks> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "tasks.db")
    return Room.databaseBuilder<Tasks>(
        name = dbFile.absolutePath
    )
}

fun createDataStore(): DataStore<Preferences> = createDataStore(
    producePath = {
        File(System.getProperty("java.io.tmpdir"))
            .resolve(dataStoreFileName).absolutePath
    }
)


fun bookmarksBuilder(): RoomDatabase.Builder<Bookmarks> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "bookmarks.db")
    return Room.databaseBuilder<Bookmarks>(
        name = dbFile.absolutePath
    )
}

fun createTasks(): Tasks {
    return tasksDatabaseBuilder(tasksBuilder())
}


fun createBookmarks(): Bookmarks {
    return bookmarksDatabaseBuilder(bookmarksBuilder())
}


