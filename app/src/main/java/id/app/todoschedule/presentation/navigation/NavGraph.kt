package id.app.todoschedule.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import id.app.todoschedule.presentation.agenda.AgendaScreen
import id.app.todoschedule.presentation.detail.DetailScreen
import id.app.todoschedule.presentation.editor.EditorScreen
import id.app.todoschedule.presentation.filter.FilterScreen
import id.app.todoschedule.presentation.home.HomeScreen
import id.app.todoschedule.presentation.home.HomeViewModel
import id.app.todoschedule.presentation.schedule.ScheduleScreen
import id.app.todoschedule.presentation.search.SearchScreen
import id.app.todoschedule.presentation.tasks.TasksScreen

/**
 * Navigation graph untuk aplikasi
 */
@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(Screen.Home.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Home.route)
            }
            val homeViewModel: HomeViewModel = hiltViewModel(parentEntry)

            HomeScreen(
                onNavigateToEditor = { entryId ->
                    navController.navigate(Screen.Editor.createRoute(entryId))
                },
                onNavigateToDetail = { entryId ->
                    navController.navigate(Screen.Detail.createRoute(entryId))
                },
                onNavigateToFilter = {
                    navController.navigate(Screen.Filter.route)
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                viewModel = homeViewModel
            )
        }

        // Editor Screen (Create/Edit)
        composable(
            route = Screen.Editor.route,
            arguments = listOf(
                navArgument("entryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            EditorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Detail Screen
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("entryId") {
                    type = NavType.LongType
                }
            )
        ) {
            DetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { entryId ->
                    navController.navigate(Screen.Editor.createRoute(entryId))
                }
            )
        }

        // Filter Screen
        composable(Screen.Filter.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Home.route)
            }
            val homeViewModel: HomeViewModel = hiltViewModel(parentEntry)

            FilterScreen(
                onNavigateBack = { navController.popBackStack() },
                onApplyFilter = { filterState ->
                    homeViewModel.applyAdvancedFilter(filterState)
                }
            )
        }

        // Search Screen
        composable(Screen.Search.route) {
            SearchScreen(
                navController = navController
            )
        }

        // Schedule Screen
        composable(Screen.Schedule.route) {
            ScheduleScreen(
                onNavigateToEditor = { entryId ->
                    navController.navigate(Screen.Editor.createRoute(entryId))
                }
            )
        }

        // Tasks Screen
        composable(Screen.Tasks.route) {
            TasksScreen(
                onNavigateToEditor = { entryId ->
                    navController.navigate(Screen.Editor.createRoute(entryId))
                }
            )
        }

        // Agenda Screen
        composable(Screen.Agenda.route) {
            AgendaScreen(
                onNavigateToEditor = { entryId ->
                    navController.navigate(Screen.Editor.createRoute(entryId))
                }
            )
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            // TODO: SettingsScreen akan dibuat nanti
            // SettingsScreen(
            //     onNavigateBack = { navController.popBackStack() }
            // )
        }
    }
}
