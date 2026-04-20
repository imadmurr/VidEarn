package com.app.admin.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.admin.navigation.BottomNavItem
import com.app.admin.navigation.NavGraph
import com.example.bottombar.AnimatedBottomBar
import com.example.bottombar.components.BottomBarItem
import com.example.bottombar.model.IndicatorStyle

/**
 * MainScaffold is responsible for setting up the bottom navigation bar
 * and handling navigation between different sections of the app.
 *
 * @param navController The navigation controller to handle navigation between screens.
 */
@Composable
fun MainScaffold(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedItem by remember { mutableIntStateOf(0) }

    val navigationItems = listOf(
        BottomNavItem("Users", "users", Icons.Default.AccountCircle),
        BottomNavItem("Payouts", "payouts", Icons.Default.ShoppingCart),
        BottomNavItem("Notifications", "notifications", Icons.Default.Notifications),
        BottomNavItem("Tasks", "tasks", Icons.Default.List)
    )

    Scaffold(
        bottomBar = {
            AnimatedBottomBar(
                selectedItem = selectedItem,
                itemSize = navigationItems.size,
                containerColor = Color.LightGray,
                indicatorStyle = IndicatorStyle.LINE
            ) {
                navigationItems.forEachIndexed { index, navigationItem ->
                    BottomBarItem(
                        selected = currentRoute == navigationItem.route,
                        onClick = {
                            if (currentRoute != navigationItem.route) {
                                selectedItem = index
                                navController.navigate(navigationItem.route)
                            }
                        },
                        imageVector = navigationItem.icon,
                        label = navigationItem.title,
                        containerColor = Color.Transparent
                    )
                }
            }
        },
        modifier = Modifier.padding(bottom = 0.dp),
        content = { paddingValues ->
            NavGraph(navController, Modifier.padding(paddingValues))
        }
    )
}
