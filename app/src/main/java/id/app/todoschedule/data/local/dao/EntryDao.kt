package id.app.todoschedule.data.local.dao

import androidx.room.*
import id.app.todoschedule.core.model.EntryType
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.data.local.entity.EntryEntity
import id.app.todoschedule.data.local.entity.EntryWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk Entry
 */
@Dao
interface EntryDao {

    /**
     * Observe semua entries dengan filter
     * Sorted by: Due date (upcoming first), then Priority (High → Low)
     */
    @Transaction
    @Query("""
        SELECT * FROM entry
        WHERE (:type IS NULL OR type = :type)
        AND (:status IS NULL OR status = :status)
        AND (:priority IS NULL OR priority = :priority)
        ORDER BY
            COALESCE(dueAt, startAt, 9999999999999) ASC,
            CASE priority
                WHEN 'HIGH' THEN 1
                WHEN 'MED' THEN 2
                WHEN 'LOW' THEN 3
            END,
            createdAt DESC
    """)
    fun observeEntries(
        type: EntryType? = null,
        status: Status? = null,
        priority: Priority? = null
    ): Flow<List<EntryWithDetails>>

    /**
     * Observe upcoming entries (untuk widget dan home)
     */
    @Transaction
    @Query("""
        SELECT * FROM entry
        WHERE status = 'OPEN'
        AND (dueAt IS NOT NULL OR startAt IS NOT NULL)
        ORDER BY
            CASE priority
                WHEN 'HIGH' THEN 1
                WHEN 'MED' THEN 2
                WHEN 'LOW' THEN 3
            END,
            COALESCE(dueAt, startAt) ASC
        LIMIT :limit
    """)
    fun observeUpcoming(limit: Int = 10): Flow<List<EntryWithDetails>>

    /**
     * Get entry by ID dengan details
     */
    @Transaction
    @Query("SELECT * FROM entry WHERE id = :id")
    fun observeEntryById(id: Long): Flow<EntryWithDetails?>

    /**
     * Get entry by ID (tanpa flow, untuk worker)
     */
    @Transaction
    @Query("SELECT * FROM entry WHERE id = :id")
    suspend fun getEntryById(id: Long): EntryWithDetails?

    /**
     * Insert entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: EntryEntity): Long

    /**
     * Update entry
     */
    @Update
    suspend fun update(entry: EntryEntity)

    /**
     * Delete entry
     */
    @Delete
    suspend fun delete(entry: EntryEntity)

    /**
     * Delete entry by ID
     */
    @Query("DELETE FROM entry WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Update status entry (toggle done/open)
     */
    @Query("""
        UPDATE entry
        SET status = :status, updatedAt = :updatedAt
        WHERE id = :id
    """)
    suspend fun updateStatus(id: Long, status: Status, updatedAt: Long = System.currentTimeMillis())

    /**
     * Search entries by title or description
     */
    @Transaction
    @Query("""
        SELECT * FROM entry
        WHERE title LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchEntries(query: String): Flow<List<EntryWithDetails>>

    /**
     * Delete all entries
     */
    @Query("DELETE FROM entry")
    suspend fun deleteAll()
}
