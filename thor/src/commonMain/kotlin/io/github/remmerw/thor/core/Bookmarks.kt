package io.github.remmerw.thor.core

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.remmerw.thor.BookmarksConstructor

@Database(entities = [Bookmark::class], version = 1, exportSchema = false)
@ConstructedBy(BookmarksConstructor::class)
abstract class Bookmarks : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}
