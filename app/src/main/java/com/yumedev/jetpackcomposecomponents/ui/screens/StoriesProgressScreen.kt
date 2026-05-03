package com.yumedev.jetpackcomposecomponents.ui.screens

import androidx.compose.runtime.Composable
import com.yumedev.jetpackcomposecomponents.ui.components.storiesprogress.StoriesProgressComponent

@Composable
fun StoriesProgressScreen(onBack: () -> Unit) {
    StoriesProgressComponent(onNavigateBack = onBack)
}