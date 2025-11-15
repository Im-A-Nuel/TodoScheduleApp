package id.app.todoschedule.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import id.app.todoschedule.MainActivity
import id.app.todoschedule.R
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class untuk mengelola notifikasi reminder
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID_REMINDERS = "NOTIF_REMINDERS"
        const val CHANNEL_NAME_REMINDERS = "Pengingat Tugas & Jadwal"
        const val CHANNEL_DESC_REMINDERS = "Notifikasi untuk pengingat tugas dan jadwal Anda"

        const val EXTRA_ENTRY_ID = "entry_id"
        const val EXTRA_ENTRY_TITLE = "entry_title"

        const val ACTION_MARK_DONE = "id.app.todoschedule.ACTION_MARK_DONE"
        const val ACTION_SNOOZE = "id.app.todoschedule.ACTION_SNOOZE"
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    /**
     * Membuat notification channel untuk Android O+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                CHANNEL_NAME_REMINDERS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_REMINDERS
                enableVibration(true)
                enableLights(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Menampilkan notifikasi reminder untuk entry
     */
    fun showReminderNotification(
        entryId: Long,
        title: String,
        description: String?,
        notificationId: Int = entryId.toInt()
    ) {
        // Intent untuk membuka detail entry ketika notifikasi di-tap
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = Intent.ACTION_VIEW
            putExtra(EXTRA_ENTRY_ID, entryId)
        }

        val openPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent untuk mark as done
        val markDoneIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_MARK_DONE
            putExtra(EXTRA_ENTRY_ID, entryId)
            putExtra("notificationId", notificationId)
        }

        val markDonePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 1,
            markDoneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent untuk snooze
        val snoozeIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_ENTRY_ID, entryId)
            putExtra(EXTRA_ENTRY_TITLE, title)
            putExtra("notificationId", notificationId)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: Ganti dengan icon yang sesuai
            .setContentTitle(title)
            .setContentText(description ?: "Saatnya menyelesaikan tugas ini!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground, // TODO: Ganti dengan icon check
                "Tandai Selesai",
                markDonePendingIntent
            )
            .addAction(
                R.drawable.ic_launcher_foreground, // TODO: Ganti dengan icon snooze
                "Tunda 10 menit",
                snoozePendingIntent
            )
            .build()

        // Show notification
        try {
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Handle permission denied (API 33+)
            e.printStackTrace()
        }
    }

    /**
     * Membatalkan notifikasi berdasarkan ID
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    /**
     * Mengecek apakah notifikasi diizinkan (API 33+)
     */
    fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationManager.areNotificationsEnabled()
        } else {
            true // Untuk API < 33, notifikasi selalu enabled by default
        }
    }
}
