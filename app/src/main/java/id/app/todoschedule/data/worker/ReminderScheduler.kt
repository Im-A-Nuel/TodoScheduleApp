package id.app.todoschedule.data.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import id.app.todoschedule.domain.model.Entry
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class untuk scheduling reminder dengan WorkManager
 */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule semua reminders untuk sebuah entry
     */
    fun scheduleReminders(entry: Entry) {
        // Cancel reminders lama untuk entry ini
        cancelReminders(entry.id)

        // Schedule reminder baru
        entry.reminders.forEach { reminder ->
            scheduleReminder(
                entryId = entry.id,
                remindAt = reminder.remindAt,
                title = entry.title,
                description = entry.description
            )
        }
    }

    /**
     * Schedule single reminder
     */
    private fun scheduleReminder(
        entryId: Long,
        remindAt: Long,
        title: String,
        description: String?
    ) {
        val currentTime = System.currentTimeMillis()
        val delay = remindAt - currentTime

        // Jangan schedule jika waktu sudah lewat
        if (delay <= 0) {
            return
        }

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    ReminderWorker.KEY_ENTRY_ID to entryId,
                    ReminderWorker.KEY_ENTRY_TITLE to title,
                    ReminderWorker.KEY_ENTRY_DESCRIPTION to description
                )
            )
            .addTag("${ReminderWorker.WORK_TAG_PREFIX}$entryId")
            .build()

        // Enqueue work dengan unique name untuk menghindari duplikasi
        workManager.enqueueUniqueWork(
            "${ReminderWorker.WORK_TAG_PREFIX}${entryId}_$remindAt",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Cancel semua reminders untuk entry tertentu
     */
    fun cancelReminders(entryId: Long) {
        workManager.cancelAllWorkByTag("${ReminderWorker.WORK_TAG_PREFIX}$entryId")
    }

    /**
     * Cancel semua reminders
     */
    fun cancelAllReminders() {
        workManager.cancelAllWorkByTag(ReminderWorker.WORK_TAG_PREFIX)
    }
}
