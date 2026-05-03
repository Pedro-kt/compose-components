package com.yumedev.jetpackcomposecomponents.ui.screens

import androidx.compose.runtime.Composable
import com.yumedev.jetpackcomposecomponents.ui.components.travelcard.TravelCardComponent

@Composable
fun TravelCardScreen(onBack: () -> Unit) {
    TravelCardComponent(onNavigateBack = onBack)
}