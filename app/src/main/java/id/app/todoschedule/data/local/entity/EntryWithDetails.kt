package id.app.todoschedule.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Relation model untuk query Entry dengan semua detailnya
 * (tags dan reminders)
 */
data class EntryWithDetails(
    @Embedded
    val entry: EntryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "entryId"
    )
    val reminders: List<ReminderEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EntryTagCrossRef::class,
            parentColumn = "entryId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
