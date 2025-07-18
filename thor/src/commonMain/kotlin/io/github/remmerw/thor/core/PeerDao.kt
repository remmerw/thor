package io.github.remmerw.thor.core

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.remmerw.borr.PeerId
import kotlinx.coroutines.flow.Flow

@Dao
interface PeerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(peer: Peer)

    @Query("SELECT * FROM Peer ORDER BY RANDOM() LIMIT :limit")
    fun randomPeers(limit: Int): Flow<List<Peer>>

    @Query("DELETE FROM Peer WHERE peerId = :peerId")
    suspend fun delete(peerId: PeerId)
}
