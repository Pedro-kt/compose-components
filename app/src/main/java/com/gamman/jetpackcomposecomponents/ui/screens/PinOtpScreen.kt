package com.gamman.jetpackcomposecomponents.ui.screens

import androidx.compose.runtime.Composable
import com.gamman.jetpackcomposecomponents.ui.components.pinotp.PinOtpComponent

@Composable
fun PinOtpScreen(onBack: () -> Unit) {
    PinOtpComponent(onNavigateBack = onBack)
}