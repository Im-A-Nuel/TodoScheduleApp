package id.app.todoschedule.data.local.dao

import androidx.room.*
import id.app.todoschedule.data.local.entity.EntryTagCrossRef
import id.app.todoschedule.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk Tag dan EntryTag relationship
 */
@Dao
interface TagDao {

    @Query("SELECT * FROM tag ORDER BY name ASC")
    fun observeAllTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tag WHERE name = :name")
    suspend fun getTagByName(name: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntryTag(crossRef: EntryTagCrossRef)

    @Delete
    suspend fun deleteEntryTag(crossRef: EntryTagCrossRef)

    @Query("DELETE FROM entry_tag WHERE entryId = :entryId")
    suspend fun deleteAllTagsForEntry(entryId: Long)

    @Query("DELETE FROM tag")
    suspend fun deleteAllTags()

    /**
     * Helper untuk add tag ke entry
     * Jika tag belum ada, buat dulu, lalu associate ke entry
     */
    @Transaction
    suspend fun addTagToEntry(entryId: Long, tagName: String) {
        val existingTag = getTagByName(tagName)
        val tagId = existingTag?.id ?: insert(TagEntity(name = tagName))
        insertEntryTag(EntryTagCrossRef(entryId = entryId, tagId = tagId))
    }

    /**
     * Set tags untuk entry (replace all)
     */
    @Transaction
    suspend fun setTagsForEntry(entryId: Long, tagNames: List<String>) {
        deleteAllTagsForEntry(entryId)
        tagNames.forEach { tagName ->
            addTagToEntry(entryId, tagName)
        }
    }
}
