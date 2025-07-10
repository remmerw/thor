package io.github.remmerw.thor.core

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bookmark(
    @field:PrimaryKey(autoGenerate = true) val id: Long, val url: String,
    val title: String,
    @field:ColumnInfo(typeAffinity = ColumnInfo.BLOB) val icon: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Bookmark

        if (id != other.id) return false
        if (url != other.url) return false
        if (title != other.title) return false
        if (!icon.contentEquals(other.icon)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (icon?.contentHashCode() ?: 0)
        return result
    }


}
