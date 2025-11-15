package id.app.todoschedule.domain.model

import id.app.todoschedule.core.model.EntryType
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.core.model.Status

/**
 * Domain model untuk Entry
 * Digunakan di layer presentation dan domain (use case)
 */
data class Entry(
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
    val tags: List<String> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
