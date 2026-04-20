package com.app.admin.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing an item in the bottom navigation bar.
 *
 * @param title The label for the navigation item.
 * @param route The route for the navigation item.
 * @param icon The icon to display for the navigation item.
 */
data class BottomNavItem(val title: String, val route: String, val icon: ImageVector)
