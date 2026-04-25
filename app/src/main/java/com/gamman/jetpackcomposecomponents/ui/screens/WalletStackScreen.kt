package com.gamman.jetpackcomposecomponents.ui.screens

import androidx.compose.runtime.Composable
import com.gamman.jetpackcomposecomponents.ui.components.walletstack.WalletStackComponent

@Composable
fun WalletStackScreen(onBack: () -> Unit) {
    WalletStackComponent(onNavigateBack = onBack)
}