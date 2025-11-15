package id.app.todoschedule.data.mapper

import id.app.todoschedule.data.local.entity.EntryEntity
import id.app.todoschedule.data.local.entity.EntryWithDetails
import id.app.todoschedule.data.local.entity.ReminderEntity
import id.app.todoschedule.domain.model.Entry
import id.app.todoschedule.domain.model.Reminder

/**
 * Mapper untuk konversi antara Entity dan Domain model
 */

// Entity -> Domain
fun EntryWithDetails.toDomain(): Entry {
    return Entry(
        id = entry.id,
        type = entry.type,
        title = entry.title,
        description = entry.description,
        priority = entry.priority,
        status = entry.status,
        startAt = entry.startAt,
        endAt = entry.endAt,
        dueAt = entry.dueAt,
        locationText = entry.locationText,
        tags = tags.map { it.name },
        reminders = reminders.map { it.toDomain() },
        createdAt = entry.createdAt,
        updatedAt = entry.updatedAt
    )
}

fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        id = id,
        entryId = entryId,
        remindAt = remindAt,
        offsetMinutes = offsetMinutes
    )
}

// Domain -> Entity
fun Entry.toEntity(): EntryEntity {
    return EntryEntity(
        id = id,
        type = type,
        title = title,
        description = description,
        priority = priority,
        status = status,
        startAt = startAt,
        endAt = endAt,
        dueAt = dueAt,
        locationText = locationText,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        entryId = entryId,
        remindAt = remindAt,
        offsetMinutes = offsetMinutes
    )
}
