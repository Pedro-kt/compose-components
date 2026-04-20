package com.gamman.jetpackcomposecomponents.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamman.jetpackcomposecomponents.ui.components.loadingbutton.LoadingButtonComponent
import com.gamman.jetpackcomposecomponents.ui.components.loadingbutton.LoadingButtonState
import kotlinx.coroutines.delay

@Composable
fun LoadingButtonScreen(onBack: () -> Unit) {
    var buttonState by remember { mutableStateOf(LoadingButtonState.IDLE) }
    var simulateSuccess by remember { mutableStateOf(true) }

    // State machine
    LaunchedEffect(buttonState) {
        when (buttonState) {
            LoadingButtonState.LOADING -> {
                delay(2000)
                buttonState = if (simulateSuccess) LoadingButtonState.SUCCESS else LoadingButtonState.ERROR
            }
            LoadingButtonState.SUCCESS, LoadingButtonState.ERROR -> {
                delay(1500)
                buttonState = LoadingButtonState.IDLE
            }
            LoadingButtonState.IDLE -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E0E)),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 4.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Text(
                text = "Loading Button",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Outcome selector
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Simulate result",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutcomeChip(
                        label = "Success",
                        selected = simulateSuccess,
                        selectedColor = Color(0xFF1B5E20),
                        onClick = { simulateSuccess = true },
                    )
                    OutcomeChip(
                        label = "Error",
                        selected = !simulateSuccess,
                        selectedColor = Color(0xFFB71C1C),
                        onClick = { simulateSuccess = false },
                    )
                }
            }

            // The button
            LoadingButtonComponent(
                text = "Confirm payment",
                state = buttonState,
                onClick = { buttonState = LoadingButtonState.LOADING },
            )

            // State label
            Text(
                text = when (buttonState) {
                    LoadingButtonState.IDLE -> "Ready to interact"
                    LoadingButtonState.LOADING -> "Processing..."
                    LoadingButtonState.SUCCESS -> "Payment confirmed!"
                    LoadingButtonState.ERROR -> "Something went wrong"
                },
                color = when (buttonState) {
                    LoadingButtonState.SUCCESS -> Color(0xFF66BB6A)
                    LoadingButtonState.ERROR -> Color(0xFFEF5350)
                    else -> Color.White.copy(alpha = 0.35f)
                },
                fontSize = 13.sp,
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun OutcomeChip(
    label: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) selectedColor else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) Color.Transparent else Color(0xFF2A2A3A),
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.45f),
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}