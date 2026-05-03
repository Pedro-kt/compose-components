package com.yumedev.jetpackcomposecomponents.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.yumedev.jetpackcomposecomponents.ui.components.aurorashader.AuroraShaderComponent

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AuroraShaderScreen(onBack: () -> Unit) {
    AuroraShaderComponent(onNavigateBack = onBack)
}