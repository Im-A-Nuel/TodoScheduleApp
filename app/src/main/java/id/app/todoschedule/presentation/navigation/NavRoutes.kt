package id.app.todoschedule.presentation.navigation

/**
 * Navigation routes untuk aplikasi
 */
sealed class Screen(val route: String) {
    // Main tabs
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Tasks : Screen("tasks")
    object Agenda : Screen("agenda")

    // Modal/secondary screens
    object Editor : Screen("editor/{entryId}") {
        fun createRoute(entryId: Long? = null): String {
            return if (entryId != null) "editor/$entryId" else "editor/new"
        }
    }
    object Detail : Screen("detail/{entryId}") {
        fun createRoute(entryId: Long): String = "detail/$entryId"
    }
    object Filter : Screen("filter")
    object Search : Screen("search")
    object Settings : Screen("settings")
}
