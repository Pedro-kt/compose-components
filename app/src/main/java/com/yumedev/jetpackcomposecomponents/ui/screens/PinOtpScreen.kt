package com.yumedev.jetpackcomposecomponents.ui.screens

import androidx.compose.runtime.Composable
import com.yumedev.jetpackcomposecomponents.ui.components.pinotp.PinOtpComponent

@Composable
fun PinOtpScreen(onBack: () -> Unit) {
    PinOtpComponent(onNavigateBack = onBack)
}