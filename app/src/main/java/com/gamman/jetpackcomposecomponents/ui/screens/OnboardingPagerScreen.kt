package com.gamman.jetpackcomposecomponents.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gamman.jetpackcomposecomponents.ui.components.onboardingpager.OnboardingPagerComponent

@Composable
fun OnboardingPagerScreen(onBack: () -> Unit) {
    OnboardingPagerComponent(
        modifier = Modifier.fillMaxSize(),
        onFinish = onBack,
    )
}