package id.app.todoschedule.data.local

import androidx.room.TypeConverter
import id.app.todoschedule.core.model.EntryType
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.core.model.Status

/**
 * Type converters untuk Room Database
 * Mengkonversi enum ke string dan sebaliknya
 */
class Converters {

    @TypeConverter
    fun fromEntryType(value: EntryType): String {
        return value.name
    }

    @TypeConverter
    fun toEntryType(value: String): EntryType {
        return EntryType.valueOf(value)
    }

    @TypeConverter
    fun fromPriority(value: Priority): String {
        return value.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }

    @TypeConverter
    fun fromStatus(value: Status): String {
        return value.name
    }

    @TypeConverter
    fun toStatus(value: String): Status {
        return Status.valueOf(value)
    }
}
