package id.app.todoschedule.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.compose.ui.graphics.Color
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import id.app.todoschedule.MainActivity
import id.app.todoschedule.core.model.Priority
import id.app.todoschedule.core.model.Status
import id.app.todoschedule.data.repository.EntryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.TimeUnit

/**
 * Upcoming Tasks Widget menggunakan Jetpack Glance
 * Menampilkan 5-10 upcoming tasks di home screen
 */
class UpcomingWidget : GlanceAppWidget() {

    companion object {
        private const val TAG = "UpcomingWidget"
        const val MAX_ITEMS = 5
        const val ACTION_MARK_DONE = "id.app.todoschedule.widget.ACTION_MARK_DONE"
        const val EXTRA_ENTRY_ID = "widget_entry_id"
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    fun startPeriodicUpdates(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val updateRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
            30, TimeUnit.MINUTES // Update setiap 30 menit
        )
            .setConstraints(constraints)
            .addTag("widget_update")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "upcoming_widget_update",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            updateRequest
        )
    }
}

/**
 * Receiver untuk widget actions dan updates
 */
@AndroidEntryPoint
class UpcomingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UpcomingWidget()

    @Inject
    lateinit var repository: EntryRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            "android.appwidget.action.APPWIDGET_UPDATE" -> {
                // Setup periodic updates saat widget di-add
                (glanceAppWidget as UpcomingWidget).startPeriodicUpdates(context)
            }
            UpcomingWidget.ACTION_MARK_DONE -> {
                // Handle mark as done action
                val entryId = intent.getLongExtra(UpcomingWidget.EXTRA_ENTRY_ID, -1L)
                if (entryId != -1L) {
                    handleMarkDone(entryId)
                }
            }
        }
    }

    private fun handleMarkDone(entryId: Long) {
        scope.launch {
            try {
                // Update entry status ke DONE
                val entry = repository.getEntryById(entryId)
                if (entry != null) {
                    repository.upsertEntry(entry.copy(status = Status.DONE))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

/**
 * Widget UI Content
 */
@Composable
private fun WidgetContent() {
    // Mock data untuk demo
    val mockEntries = listOf(
        MockEntry(
            id = 1,
            title = "Review proposal",
            priority = Priority.HIGH,
            status = Status.OPEN
        ),
        MockEntry(
            id = 2,
            title = "Finish report",
            priority = Priority.MED,
            status = Status.OPEN
        ),
        MockEntry(
            id = 3,
            title = "Team meeting",
            priority = Priority.HIGH,
            status = Status.OPEN
        ),
        MockEntry(
            id = 4,
            title = "Update documentation",
            priority = Priority.LOW,
            status = Status.OPEN
        ),
        MockEntry(
            id = 5,
            title = "Code review",
            priority = Priority.MED,
            status = Status.OPEN
        ),
    )

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(ColorProvider(Color.White, Color.DarkGray))
            .padding(12.dp)
            .cornerRadius(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Header
        Text(
            text = "Upcoming Tasks",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Task List
        mockEntries.take(UpcomingWidget.MAX_ITEMS).forEach { entry ->
            TaskItemWidget(entry = entry)
        }

        // Footer - View All button
        Spacer(modifier = GlanceModifier.height(8.dp))
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .clickable(
                    onClick = actionStartActivity<MainActivity>()
                )
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "View all →",
                style = TextStyle(fontSize = 12.sp)
            )
        }
    }
}

/**
 * Individual task item widget
 */
@Composable
private fun TaskItemWidget(entry: MockEntry) {
    val priorityColor = when (entry.priority) {
        Priority.HIGH -> Color(0xFFFF5252) // Red
        Priority.MED -> Color(0xFFFFC107) // Yellow
        Priority.LOW -> Color(0xFF4CAF50) // Green
    }

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Priority indicator
        Spacer(
            modifier = GlanceModifier
                .width(3.dp)
                .height(16.dp)
                .background(ColorProvider(priorityColor, priorityColor))
                .cornerRadius(2.dp)
        )

        Spacer(modifier = GlanceModifier.width(8.dp))

        // Task title
        Text(
            text = entry.title,
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
            modifier = GlanceModifier
                .fillMaxWidth()
        )
    }
}

/**
 * Mock entry untuk testing
 * Nanti akan diganti dengan real Entry model
 */
data class MockEntry(
    val id: Long,
    val title: String,
    val priority: Priority,
    val status: Status
)
