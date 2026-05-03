package com.yumedev.jetpackcomposecomponents.ui.screens

import androidx.compose.runtime.Composable
import com.yumedev.jetpackcomposecomponents.ui.components.musicplayer.MusicPlayerComponent

@Composable
fun MusicPlayerScreen(onBack: () -> Unit) {
    MusicPlayerComponent(onNavigateBack = onBack)
}