package com.gamman.jetpackcomposecomponents.ui.screens

import androidx.compose.runtime.Composable
import com.gamman.jetpackcomposecomponents.ui.components.musicplayer.MusicPlayerComponent

@Composable
fun MusicPlayerScreen(onBack: () -> Unit) {
    MusicPlayerComponent(onNavigateBack = onBack)
}