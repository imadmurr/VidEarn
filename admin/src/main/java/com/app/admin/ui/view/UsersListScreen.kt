package com.app.admin.ui.view

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.admin.data.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Main Composable with Navigation
@Composable
fun UserListApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "user_list") {
        composable("user_list") {
            UserListScreen(users = sampleUsers(), navController = navController)
        }
        composable("user_detail/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val user = sampleUsers().first { it.id == userId }
            UserDetailScreen(user = user)
        }
    }
}

// User List Screen with Navigation
@Composable
fun UserListScreen(users: List<User>, navController: NavHostController) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var filteredUsers by remember { mutableStateOf(users) }
    val coroutineScope = rememberCoroutineScope()
    var searchJob: Job? by remember { mutableStateOf(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {
        // Search bar with refresh button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    searchJob?.cancel()
                    searchJob = coroutineScope.launch {
                        delay(300) // Debounce for 300 milliseconds
                        filteredUsers = filterUsers(users, it.text)
                    }
                },
                placeholder = { Text("Search users...") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,  // Removes line when focused
                    unfocusedIndicatorColor = Color.Transparent, // Removes line when unfocused
                ),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray, RoundedCornerShape(24.dp))
            )
            IconButton(
                onClick = { /* Refresh logic */ },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }

        // Filters button
        Button(
            onClick = { /* Filter logic */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Filters")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // User list
        LazyColumn {
            if (filteredUsers.isEmpty()) {
                item {
                    Text("No users found.", modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp))
                }
            } else {
                items(filteredUsers) { user ->
                    UserCard(user = user) {
                        // Navigate to detail screen when a user is clicked
                        navController.navigate("user_detail/${user.id}")
                    }
                }
            }
        }
    }
}

fun filterUsers(users: List<User>, query: String): List<User> {
    return users.filter { user ->
        user.name.contains(query, ignoreCase = true) ||
                user.email.contains(query, ignoreCase = true)
    }
}

@Composable
fun UserCard(user: User, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick) // Add click listener to navigate
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Default user icon
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = user.email, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// User Detail Screen
@Composable
fun UserDetailScreen(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "User Icon",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Name: ${user.name}", style = MaterialTheme.typography.titleLarge)
        Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Points: ${user.points}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Invite Code: ${user.inviteCode}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Daily Bonus Time: ${user.dailyBonusTime}", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { /* Additional actions like edit or delete */ }) {
            Text(text = "Edit User")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListAppPreview() {
    UserListApp()
}

// Sample User Data
fun sampleUsers(): List<User> {
    return listOf(
        User(id = "1", name = "Imad Murr", email = "imadmurr1@gmail.com", points = 1200L),
        User(id = "2", name = "John Doe", email = "john.doe@example.com", points = 500L),
        User(id = "3", name = "Jane Smith", email = "jane.smith@example.com", points = 950L)
    )
}
