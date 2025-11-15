package id.app.todoschedule.presentation.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Main Navigation Scaffold dengan Bottom Navigation
 * Menggabungkan NavGraph dan BottomNavigationBar
 */
@Composable
fun MainNavigationScaffold(
    navController: NavHostController,
    onIntentHandled: () -> Unit = {}
) {
    var currentTab by remember { mutableStateOf(BottomNavTab.SCHEDULE) }

    // Track current route untuk update tab state
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    // Update tab state berdasarkan current route
    LaunchedEffect(currentRoute) {
        currentTab = when {
            currentRoute.startsWith(BottomNavTab.SCHEDULE.route) -> BottomNavTab.SCHEDULE
            currentRoute.startsWith(BottomNavTab.TASKS.route) -> BottomNavTab.TASKS
            currentRoute.startsWith(BottomNavTab.AGENDA.route) -> BottomNavTab.AGENDA
            currentRoute.startsWith(BottomNavTab.SETTINGS.route) -> BottomNavTab.SETTINGS
            else -> currentTab // Keep current if not a main tab
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    currentTab = tab
                    navController.navigate(tab.route) {
                        // Pop up to start destination to avoid building large back stacks
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Reuse existing instances in the back stack
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavGraphWithPadding(
            navController = navController,
            paddingValues = paddingValues
        )
    }
}

/**
 * Wrapper untuk NavGraph dengan padding dari Scaffold
 */
@Composable
fun NavGraphWithPadding(
    navController: NavHostController,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    // NavGraph remains the same, padding handled by parent
    NavGraph(navController = navController)
}
