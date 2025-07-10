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
import io.github.remmerw.thor.core.homepage
import kotlinx.coroutines.flow.Flow
import kotlinx.io.files.Path

private var thor: Thor? = null


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual typealias Context = android.content.Context

internal class AndroidThor(
    private val context: Context,
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
        return Path(context.cacheDir.absolutePath)
    }

    override suspend fun removeHomepage() {
        io.github.remmerw.thor.core.removeHomepage(datastore)
    }

    override fun homepageUri(default: String): Flow<String> {
        return io.github.remmerw.thor.core.homepageUri(datastore, default)
    }

    override suspend fun homepage(uri: String, title: String, icon: ByteArray?) {
        homepage(datastore, uri, title, icon)
    }
}


actual fun thor(): Thor = thor!!


actual fun initializeThor(context: Context) {
    val datastore = createDataStore(context)
    val tasks = createTasks(context)
    val peers = createPeers(context)
    val bookmarks = createBookmarks(context)
    val idun = newIdun(
        peerStore = peers,
        bootstrap = bootstrap()
    )
    thor = AndroidThor(context, datastore, tasks, bookmarks, idun)
}


fun peersBuilder(ctx: Context): RoomDatabase.Builder<Peers> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("peers.db")
    return Room.databaseBuilder<Peers>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

fun createPeers(ctx: Context): Peers {
    return peersDatabaseBuilder(peersBuilder(ctx))
}


fun tasksBuilder(ctx: Context): RoomDatabase.Builder<Tasks> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("tasks.db")
    return Room.databaseBuilder<Tasks>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)


fun bookmarksBuilder(ctx: Context): RoomDatabase.Builder<Bookmarks> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("bookmarks.db")
    return Room.databaseBuilder<Bookmarks>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

fun createTasks(ctx: Context): Tasks {
    return tasksDatabaseBuilder(tasksBuilder(ctx))
}


fun createBookmarks(ctx: Context): Bookmarks {
    return bookmarksDatabaseBuilder(bookmarksBuilder(ctx))
}