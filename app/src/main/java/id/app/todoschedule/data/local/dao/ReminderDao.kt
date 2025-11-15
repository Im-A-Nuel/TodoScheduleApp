package id.app.todoschedule.data.local.dao

import androidx.room.*
import id.app.todoschedule.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk Reminder
 */
@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder WHERE entryId = :entryId ORDER BY remindAt ASC")
    fun observeRemindersByEntryId(entryId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminder WHERE entryId = :entryId ORDER BY remindAt ASC")
    suspend fun getRemindersByEntryId(entryId: Long): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminders: List<ReminderEntity>)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("DELETE FROM reminder WHERE entryId = :entryId")
    suspend fun deleteByEntryId(entryId: Long)

    @Query("DELETE FROM reminder")
    suspend fun deleteAll()
}
