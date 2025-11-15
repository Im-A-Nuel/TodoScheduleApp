package id.app.todoschedule.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.app.todoschedule.data.local.dao.EntryDao
import id.app.todoschedule.data.local.dao.ReminderDao
import id.app.todoschedule.data.local.dao.TagDao
import id.app.todoschedule.data.local.entity.EntryEntity
import id.app.todoschedule.data.local.entity.EntryTagCrossRef
import id.app.todoschedule.data.local.entity.ReminderEntity
import id.app.todoschedule.data.local.entity.TagEntity

/**
 * Room Database untuk TodoSchedule
 * Version 1: Initial schema
 */
@Database(
    entities = [
        EntryEntity::class,
        ReminderEntity::class,
        TagEntity::class,
        EntryTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun reminderDao(): ReminderDao
    abstract fun tagDao(): TagDao

    companion object {
        const val DATABASE_NAME = "todoschedule_db"
    }
}
