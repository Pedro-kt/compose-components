package com.yumedev.jetpackcomposecomponents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yumedev.jetpackcomposecomponents.navigation.AppNavigation
import com.yumedev.jetpackcomposecomponents.ui.theme.JetpackComposeComponentsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComposeComponentsTheme {
                AppNavigation()
            }
        }
    }
}