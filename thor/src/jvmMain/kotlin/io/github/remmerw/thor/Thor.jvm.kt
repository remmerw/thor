package io.github.remmerw.thor

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.remmerw.idun.Idun
import io.github.remmerw.idun.newIdun
import io.github.remmerw.thor.core.Bookmarks
import io.github.remmerw.thor.core.Tasks
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
) : Thor() {


    override fun datastore(): DataStore<Preferences> {
        return datastore
    }

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


}


actual fun thor(): Thor = thor!!


actual fun initializeThor(context: Context) {
    val datastore = createDataStore()
    val tasks = createTasks()
    val bookmarks = createBookmarks()
    val idun = newIdun()
    thor = JvmThor(datastore, tasks, bookmarks, idun)
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


actual fun render() {
    val url = "http://www.benjysbrain.com/"
    val p = Render(url)
    p.parsePage()
}