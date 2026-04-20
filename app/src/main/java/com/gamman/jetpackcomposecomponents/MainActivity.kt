package com.gamman.jetpackcomposecomponents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gamman.jetpackcomposecomponents.navigation.AppNavigation
import com.gamman.jetpackcomposecomponents.ui.theme.JetpackComposeComponentsTheme

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