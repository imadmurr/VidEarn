package com.app.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.app.admin.ui.theme.MyApplicationTheme
import com.app.admin.ui.view.MainScaffold
import kotlinx.coroutines.launch

/**
 * The MainActivity is the entry point of the application.
 * It sets up the content and navigation for the admin panel.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                MainScaffold(navController)
            }
        }
    }
}
