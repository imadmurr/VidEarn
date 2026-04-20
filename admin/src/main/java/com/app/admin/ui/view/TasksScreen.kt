package com.app.admin.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.admin.ui.theme.MyApplicationTheme

/**
 * Data class representing a Task.
 *
 * @param title The title of the task.
 * @param description The description of the task.
 * @param isCompleted The completion status of the task.
 */
data class Task(val title: String, val description: String, val isCompleted: Boolean)

/**
 * TasksScreen displays a list of tasks and allows managing them.
 *
 * @param tasks List of tasks to display.
 */
@Composable
fun TasksScreen(tasks: List<Task>) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {

        // Title
        Text(
            text = "Tasks",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Tasks list
        LazyColumn {
            if (tasks.isEmpty()) {
                item {
                    Text("No tasks available.", modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp))
                }
            } else {
                items(tasks) { task ->
                    TaskCard(task = task)
                }
            }
        }
    }
}

/**
 * TaskCard displays information about a single task.
 *
 * @param task The task to display.
 */
@Composable
fun TaskCard(task: Task) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Task details
            Column {
                Text(text = task.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = task.description, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = if (task.isCompleted) "Completed" else "Not Completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (task.isCompleted) Color.Green else Color.Red
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TasksScreenPreview() {
    MyApplicationTheme {
        TasksScreen(sampleTasks())
    }
}

fun sampleTasks(): List<Task> {
    return listOf(
        Task(title = "Complete Report", description = "Finish the monthly report by EOD", isCompleted = false),
        Task(title = "Update App", description = "Deploy the latest version of the app", isCompleted = true),
        Task(title = "Review Feedback", description = "Go through user feedback and make improvements", isCompleted = false)
    )
}
