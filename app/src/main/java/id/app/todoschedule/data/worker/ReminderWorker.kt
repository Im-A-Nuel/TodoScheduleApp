package id.app.todoschedule.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.data.notification.NotificationHelper
import id.app.todoschedule.data.repository.EntryRepository

/**
 * Worker untuk menampilkan notifikasi reminder di background
 * Menggunakan WorkManager untuk scheduling yang reliable
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: EntryRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_ENTRY_ID = "entry_id"
        const val KEY_ENTRY_TITLE = "entry_title"
        const val KEY_ENTRY_DESCRIPTION = "entry_description"
        const val WORK_TAG_PREFIX = "reminder_"
    }

    override suspend fun doWork(): Result {
        return try {
            // Ambil data dari input
            val entryId = inputData.getLong(KEY_ENTRY_ID, -1L)
            if (entryId == -1L) {
                return Result.failure()
            }

            // Ambil entry dari database untuk mendapatkan data terbaru
            val entry = repository.getEntryById(entryId)

            if (entry == null) {
                // Entry sudah dihapus, return success
                return Result.success()
            }

            // Cek apakah entry masih OPEN/IN_PROGRESS
            // Jika sudah DONE, tidak perlu show notifikasi
            if (entry.status == Status.DONE) {
                return Result.success()
            }

            // Ambil title dan description dari input data atau entry
            val title = inputData.getString(KEY_ENTRY_TITLE) ?: entry.title
            val description = inputData.getString(KEY_ENTRY_DESCRIPTION) ?: entry.description

            // Tampilkan notifikasi
            notificationHelper.showReminderNotification(
                entryId = entryId,
                title = title,
                description = description,
                notificationId = entryId.toInt()
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Retry jika ada error (max 3x oleh WorkManager)
            Result.retry()
        }
    }
}
