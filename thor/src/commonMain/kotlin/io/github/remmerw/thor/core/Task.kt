package io.github.remmerw.thor.core

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @field:PrimaryKey(autoGenerate = true) val id: Long,
    val pid: Long,
    val name: String,
    val mimeType: String,
    val uri: String,
    val size: Long,
    val work: String?,
    val active: Boolean,
    val finished: Boolean,
    val progress: Float
)
