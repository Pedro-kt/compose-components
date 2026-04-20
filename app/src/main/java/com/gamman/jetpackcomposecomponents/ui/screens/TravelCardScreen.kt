package com.gamman.jetpackcomposecomponents.ui.screens

import androidx.compose.runtime.Composable
import com.gamman.jetpackcomposecomponents.ui.components.travelcard.TravelCardComponent

@Composable
fun TravelCardScreen(onBack: () -> Unit) {
    TravelCardComponent(onNavigateBack = onBack)
}