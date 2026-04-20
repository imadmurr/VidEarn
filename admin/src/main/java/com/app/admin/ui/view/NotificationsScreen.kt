package com.app.admin.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.admin.ui.theme.MyApplicationTheme

/**
 * NotificationsScreen allows the admin to send notifications to users.
 */
@Composable
fun NotificationsScreen() {
    var notificationText by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {

        // Title
        Text(
            text = "Send Notifications",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Notification text field
        TextField(
            value = notificationText,
            onValueChange = { notificationText = it },
            placeholder = { Text("Enter notification message...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Send button
        Button(
            onClick = { /* Send notification logic */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Send")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    MyApplicationTheme {
        NotificationsScreen()
    }
}
