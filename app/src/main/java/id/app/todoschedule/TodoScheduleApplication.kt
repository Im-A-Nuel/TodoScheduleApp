package id.app.todoschedule

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import id.app.todoschedule.data.notification.NotificationHelper
import javax.inject.Inject

/**
 * Application class dengan Hilt
 */
@HiltAndroidApp
class TodoScheduleApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        // NotificationHelper akan otomatis create channel di init block
        // Inject di sini untuk memastikan channel dibuat saat app start
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
