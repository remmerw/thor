package io.github.remmerw.thor.core

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.remmerw.thor.BookmarksConstructor
import kotlinx.coroutines.flow.Flow

@Database(entities = [Bookmark::class], version = 1, exportSchema = false)
@ConstructedBy(BookmarksConstructor::class)
abstract class Bookmarks : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    suspend fun insert(bookmark: Bookmark) {
        bookmarkDao().insert(bookmark)
    }

    fun hasBookmark(url: String?): Flow<Boolean> {
        return bookmarkDao().hasBookmark(url)
    }

    suspend fun delete(bookmark: Bookmark) {
        bookmarkDao().delete(bookmark)
    }

    suspend fun bookmark(url: String): Bookmark? {
        return bookmarkDao().bookmark(url)
    }

    fun bookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao().bookmarks()
    }


}
