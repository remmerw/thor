package io.github.remmerw.thor.core

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: Bookmark)

    @Query("SELECT * FROM Bookmark WHERE url = :url ORDER BY id DESC")
    suspend fun bookmark(url: String): Bookmark?

    @Query("SELECT EXISTS (SELECT * FROM Bookmark WHERE url = :url)")
    fun hasBookmark(url: String?): Flow<Boolean>

    @Query("SELECT * FROM Bookmark ")
    fun bookmarks(): Flow<List<Bookmark>>

    @Delete
    suspend fun delete(bookmark: Bookmark)
}
