package com.gamman.jetpackcomposecomponents.ui.components.pinotp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Model
// ---------------------------------------------------------------------------

private const val PIN_LENGTH = 6
private const val CORRECT_PIN = "123456"

private sealed class PinState {
    data object Idle : PinState()
    data object Loading : PinState()
    data object Error : PinState()
    data object Success : PinState()
}

// ---------------------------------------------------------------------------
// Root
// ---------------------------------------------------------------------------

@Composable
fun PinOtpComponent(onNavigateBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var digits by remember { mutableStateOf("") }
    var pinState by remember { mutableStateOf<PinState>(PinState.Idle) }
    val shakeOffset = remember { Animatable(0f) }

    fun onDigit(d: Char) {
        if (pinState !is PinState.Idle || digits.length >= PIN_LENGTH) return
        val updated = digits + d
        digits = updated
        if (updated.length == PIN_LENGTH) {
            scope.launch {
                pinState = PinState.Loading
                delay(700)
                if (updated == CORRECT_PIN) {
                    pinState = PinState.Success
                } else {
                    pinState = PinState.Error
                    for (x in listOf(14f, -14f, 10f, -10f, 6f, -6f, 0f)) {
                        shakeOffset.animateTo(x, tween(50))
                    }
                    delay(500)
                    digits = ""
                    pinState = PinState.Idle
                }
            }
        }
    }

    fun onBackspace() {
        if (pinState !is PinState.Idle || digits.isEmpty()) return
        digits = digits.dropLast(1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E0E)),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 52.dp)
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onNavigateBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(120.dp))

            AnimatedContent(
                targetState = pinState,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                label = "pin_header",
            ) { state ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (state) {
                            PinState.Idle, PinState.Loading -> "Enter PIN"
                            PinState.Error -> "Wrong PIN"
                            PinState.Success -> "Access Granted"
                        },
                        color = when (state) {
                            PinState.Error -> Color(0xFFE53935)
                            PinState.Success -> Color(0xFF43A047)
                            else -> Color.White
                        },
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = when (state) {
                            PinState.Idle -> "Hint: 1 2 3 4 5 6"
                            PinState.Loading -> "Verifying..."
                            PinState.Error -> "Try again"
                            PinState.Success -> "Welcome back"
                        },
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 14.sp,
                    )
                }
            }

            Spacer(Modifier.height(52.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.graphicsLayer { translationX = shakeOffset.value },
            ) {
                repeat(PIN_LENGTH) { index ->
                    DigitBox(
                        digit = digits.getOrNull(index),
                        pinState = pinState,
                    )
                }
            }

            Spacer(Modifier.height(60.dp))

            if (pinState !is PinState.Success) {
                Keypad(onDigit = ::onDigit, onBackspace = ::onBackspace)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Digit box
// ---------------------------------------------------------------------------

@Composable
private fun DigitBox(digit: Char?, pinState: PinState) {
    val scale = remember { Animatable(1f) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(digit) {
        if (!initialized) { initialized = true; return@LaunchedEffect }
        if (digit != null) {
            scale.animateTo(1.2f, tween(70))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        } else {
            scale.animateTo(0.82f, tween(60))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    val background = when {
        pinState is PinState.Success && digit != null -> Color(0xFF2E7D32)
        pinState is PinState.Error && digit != null -> Color(0xFFC62828)
        digit != null -> Color(0xFF6C5CE7)
        else -> Color(0xFF1A1A1A)
    }

    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 58.dp)
            .scale(scale.value)
            .clip(RoundedCornerShape(14.dp))
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = pinState to digit,
            transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
            label = "digit_content",
        ) { (state, d) ->
            when {
                state is PinState.Success && d != null -> Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
                d != null -> Text(
                    text = d.toString(),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                else -> Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Keypad
// ---------------------------------------------------------------------------

@Composable
private fun Keypad(onDigit: (Char) -> Unit, onBackspace: () -> Unit) {
    val rows = listOf(
        listOf('1', '2', '3'),
        listOf('4', '5', '6'),
        listOf('7', '8', '9'),
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                row.forEach { key ->
                    KeypadButton(label = key.toString(), onClick = { onDigit(key) })
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Box(modifier = Modifier.size(72.dp))
            KeypadButton(label = "0", onClick = { onDigit('0') })
            KeypadButton(isBackspace = true, onClick = onBackspace)
        }
    }
}

@Composable
private fun KeypadButton(
    label: String = "",
    isBackspace: Boolean = false,
    onClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .size(72.dp)
            .scale(scale.value)
            .clip(CircleShape)
            .background(Color(0xFF1E1E1E))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { _ ->
                        scope.launch { scale.animateTo(0.87f, tween(60)) }
                        val released = tryAwaitRelease()
                        scope.launch {
                            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                        if (released) onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isBackspace) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        } else {
            Text(
                text = label,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun Modifier.clickable(onClick: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onTap = { onClick() })
}