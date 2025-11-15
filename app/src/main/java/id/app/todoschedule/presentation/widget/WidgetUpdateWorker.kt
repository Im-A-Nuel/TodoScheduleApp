package id.app.todoschedule.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import id.app.todoschedule.data.repository.EntryRepository

/**
 * Background worker untuk periodic update widget
 * Dijalankan setiap 30 menit atau saat data berubah
 */
@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: EntryRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Trigger widget update
            val glanceManager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = glanceManager.getGlanceIds(UpcomingWidget::class.java)

            // Update semua widget instances
            glanceIds.forEach { glanceId ->
                UpcomingWidget().update(applicationContext, glanceId)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Retry jika ada error
            Result.retry()
        }
    }
}
