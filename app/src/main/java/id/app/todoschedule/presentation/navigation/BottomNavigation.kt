package id.app.todoschedule.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom Navigation Tab enum
 * Defines semua tabs yang ada di bottom navigation
 */
enum class BottomNavTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    SCHEDULE("schedule", "Jadwal", Icons.Default.DateRange),
    TASKS("tasks", "Tugas", Icons.Default.Done),
    AGENDA("agenda", "Agenda", Icons.Default.Home),
    SETTINGS("settings", "Pengaturan", Icons.Default.Settings)
}

/**
 * Bottom Navigation Bar component
 * Menampilkan 4 tabs: Jadwal, Tugas, Agenda, Pengaturan
 */
@Composable
fun BottomNavigationBar(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    NavigationBar {
        BottomNavTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(text = tab.label)
                }
            )
        }
    }
}
