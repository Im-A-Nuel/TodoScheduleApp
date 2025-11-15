package id.app.todoschedule.data.repository

import id.app.todoschedule.core.model.EntryType
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.data.local.dao.EntryDao
import id.app.todoschedule.data.local.dao.ReminderDao
import id.app.todoschedule.data.local.dao.TagDao
import id.app.todoschedule.data.mapper.toDomain
import id.app.todoschedule.data.mapper.toEntity
import id.app.todoschedule.domain.model.Entry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository untuk Entry
 * Single source of truth untuk data Entry
 */
@Singleton
class EntryRepository @Inject constructor(
    private val entryDao: EntryDao,
    private val reminderDao: ReminderDao,
    private val tagDao: TagDao
) {

    /**
     * Observe semua entries dengan filter
     */
    fun observeEntries(
        type: EntryType? = null,
        status: Status? = null,
        priority: Priority? = null
    ): Flow<List<Entry>> {
        return entryDao.observeEntries(type, status, priority)
            .map { list -> list.map { it.toDomain() } }
    }

    /**
     * Observe upcoming entries (untuk home dan widget)
     */
    fun observeUpcoming(limit: Int = 10): Flow<List<Entry>> {
        return entryDao.observeUpcoming(limit)
            .map { list -> list.map { it.toDomain() } }
    }

    /**
     * Observe entry by ID
     */
    fun observeEntryById(id: Long): Flow<Entry?> {
        return entryDao.observeEntryById(id)
            .map { it?.toDomain() }
    }

    /**
     * Get entry by ID (suspend, untuk worker)
     */
    suspend fun getEntryById(id: Long): Entry? {
        return entryDao.getEntryById(id)?.toDomain()
    }

    /**
     * Insert atau update entry
     * Returns saved entry with updated ID
     */
    suspend fun upsertEntry(entry: Entry): Entry {
        // Save entry
        val entryId = entryDao.insert(entry.toEntity())

        // Save reminders
        reminderDao.deleteByEntryId(entryId)
        if (entry.reminders.isNotEmpty()) {
            val reminderEntities = entry.reminders.map { reminder ->
                reminder.copy(entryId = entryId).toEntity()
            }
            reminderDao.insertAll(reminderEntities)
        }

        // Save tags
        tagDao.setTagsForEntry(entryId, entry.tags)

        // Return entry with updated ID
        return entry.copy(
            id = entryId,
            reminders = entry.reminders.map { it.copy(entryId = entryId) }
        )
    }

    /**
     * Delete entry
     */
    suspend fun deleteEntry(entry: Entry) {
        entryDao.delete(entry.toEntity())
    }

    /**
     * Delete entry by ID
     */
    suspend fun deleteEntry(entryId: Long) {
        entryDao.deleteById(entryId)
    }

    /**
     * Toggle status entry (OPEN <-> DONE)
     */
    suspend fun toggleStatus(id: Long, done: Boolean) {
        val status = if (done) Status.DONE else Status.OPEN
        entryDao.updateStatus(id, status)
    }

    /**
     * Search entries
     */
    fun searchEntries(query: String): Flow<List<Entry>> {
        return entryDao.searchEntries(query)
            .map { list -> list.map { it.toDomain() } }
    }

    /**
     * Get all tags (untuk autocomplete)
     */
    fun observeAllTags(): Flow<List<String>> {
        return tagDao.observeAllTags()
            .map { list -> list.map { it.name } }
    }
}
