package com.gamman.jetpackcomposecomponents.ui.screens

import androidx.compose.runtime.Composable
import com.gamman.jetpackcomposecomponents.ui.components.storiesprogress.StoriesProgressComponent

@Composable
fun StoriesProgressScreen(onBack: () -> Unit) {
    StoriesProgressComponent(onNavigateBack = onBack)
}