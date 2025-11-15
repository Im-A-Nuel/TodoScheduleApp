package id.app.todoschedule.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.AndroidEntryPoint
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.data.repository.EntryRepository
import id.app.todoschedule.data.worker.ReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * BroadcastReceiver untuk menangani action dari notifikasi
 * (Mark as Done, Snooze)
 */
@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: EntryRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val entryId = intent.getLongExtra(NotificationHelper.EXTRA_ENTRY_ID, -1L)
        val notificationId = intent.getIntExtra("notificationId", -1)

        if (entryId == -1L) return

        when (intent.action) {
            NotificationHelper.ACTION_MARK_DONE -> {
                handleMarkDone(entryId, notificationId)
            }
            NotificationHelper.ACTION_SNOOZE -> {
                val title = intent.getStringExtra(NotificationHelper.EXTRA_ENTRY_TITLE) ?: ""
                handleSnooze(context, entryId, title, notificationId)
            }
        }
    }

    /**
     * Menandai entry sebagai DONE dan cancel notifikasi
     */
    private fun handleMarkDone(entryId: Long, notificationId: Int) {
        scope.launch {
            try {
                // Update status entry menjadi DONE
                val entry = repository.getEntryById(entryId)
                if (entry != null) {
                    repository.upsertEntry(entry.copy(status = Status.DONE))
                }

                // Cancel notifikasi
                notificationHelper.cancelNotification(notificationId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Snooze reminder selama 10 menit
     */
    private fun handleSnooze(context: Context, entryId: Long, title: String, notificationId: Int) {
        // Cancel notifikasi saat ini
        notificationHelper.cancelNotification(notificationId)

        // Schedule ulang dalam 10 menit
        val snoozeTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10)

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(10, TimeUnit.MINUTES)
            .setInputData(
                workDataOf(
                    ReminderWorker.KEY_ENTRY_ID to entryId,
                    ReminderWorker.KEY_ENTRY_TITLE to title
                )
            )
            .addTag("snooze_$entryId")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
