package id.app.todoschedule.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import id.app.todoschedule.core.model.EntryType
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.core.model.Status

/**
 * Room Entity untuk Entry
 * Simpan di tabel Entry
 */
@Entity(tableName = "entry")
data class EntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: EntryType,
    val title: String,
    val description: String? = null,
    val priority: Priority = Priority.MED,
    val status: Status = Status.OPEN,
    val startAt: Long? = null,
    val endAt: Long? = null,
    val dueAt: Long? = null,
    val locationText: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
