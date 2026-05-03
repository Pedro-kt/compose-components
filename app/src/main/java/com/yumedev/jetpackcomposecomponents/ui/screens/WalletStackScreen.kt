package com.yumedev.jetpackcomposecomponents.ui.screens

import androidx.compose.runtime.Composable
import com.yumedev.jetpackcomposecomponents.ui.components.walletstack.WalletStackComponent

@Composable
fun WalletStackScreen(onBack: () -> Unit) {
    WalletStackComponent(onNavigateBack = onBack)
}