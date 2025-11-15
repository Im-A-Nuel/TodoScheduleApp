package id.app.todoschedule

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import id.app.todoschedule.data.notification.NotificationHelper
import id.app.todoschedule.presentation.navigation.MainNavigationScaffold
import id.app.todoschedule.presentation.navigation.Screen
import id.app.todoschedule.ui.theme.TodoScheduleTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var navController: NavHostController? = null

    // Permission launcher untuk Android 13+
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, notifications akan berfungsi
        } else {
            // Permission denied, notifikasi tidak akan muncul
            // TODO: Show explanation to user (optional)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission untuk Android 13+
        requestNotificationPermission()

        setContent {
            TodoScheduleTheme {
                navController = rememberNavController()

                // Handle deep link dari notifikasi
                LaunchedEffect(intent) {
                    handleIntent(intent, navController!!)
                }

                MainNavigationScaffold(navController = navController!!)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    // Request permission
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController?.let { handleIntent(intent, it) }
    }

    private fun handleIntent(intent: Intent, navController: NavHostController) {
        // Cek apakah intent dari notifikasi
        if (intent.action == Intent.ACTION_VIEW) {
            val entryId = intent.getLongExtra(NotificationHelper.EXTRA_ENTRY_ID, -1L)
            if (entryId != -1L) {
                // Navigate ke detail screen
                navController.navigate(Screen.Detail.createRoute(entryId)) {
                    // Clear back stack untuk menghindari loop
                    launchSingleTop = true
                }
            }
        }
    }
}