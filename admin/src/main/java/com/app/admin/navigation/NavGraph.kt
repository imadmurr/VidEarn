package com.app.admin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.admin.ui.view.NotificationsScreen
import com.app.admin.ui.view.PayoutsScreen
import com.app.admin.ui.view.TasksScreen
import com.app.admin.ui.view.UserListApp
import com.app.admin.ui.view.sampleTasks

/**
 * NavGraph sets up the navigation for the application.
 *
 * @param navController The navigation controller that handles navigation actions.
 * @param modifier Modifier to apply to the NavHost.
 */
@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "users", Modifier.then(modifier)) {
        composable("users") {
            UserListApp()
        }
        composable("payouts") {
            PayoutsScreen()
        }
        composable("notifications") {
            NotificationsScreen()
        }
        composable("tasks") {
            TasksScreen(sampleTasks())
        }
    }
}
