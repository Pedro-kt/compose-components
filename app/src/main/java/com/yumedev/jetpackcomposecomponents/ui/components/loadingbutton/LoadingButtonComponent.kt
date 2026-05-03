package com.yumedev.jetpackcomposecomponents.ui.components.loadingbutton

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// State
// ---------------------------------------------------------------------------

enum class LoadingButtonState { IDLE, LOADING, SUCCESS, ERROR }

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

@Composable
fun LoadingButtonComponent(
    text: String,
    state: LoadingButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Press feedback - scale down slightly on tap
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "press_scale",
    )

    // Width: smooth tween, no bounce to avoid overshoot on clip
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val fullWidth = maxWidth

        val buttonWidth by animateDpAsState(
            targetValue = if (state == LoadingButtonState.IDLE) fullWidth else 56.dp,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "button_width",
        )

        // Corner radius in sync with width
        val cornerRadius by animateDpAsState(
            targetValue = if (state == LoadingButtonState.IDLE) 16.dp else 28.dp,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "button_corner",
        )

        // Animated gradient colors per state
        val gradientStart by animateColorAsState(
            targetValue = when (state) {
                LoadingButtonState.IDLE, LoadingButtonState.LOADING -> Color(0xFF9B7FE8)
                LoadingButtonState.SUCCESS -> Color(0xFF43A047)
                LoadingButtonState.ERROR -> Color(0xFFE53935)
            },
            animationSpec = tween(400, easing = FastOutSlowInEasing),
            label = "gradient_start",
        )
        val gradientEnd by animateColorAsState(
            targetValue = when (state) {
                LoadingButtonState.IDLE, LoadingButtonState.LOADING -> Color(0xFF3D1F7A)
                LoadingButtonState.SUCCESS -> Color(0xFF1B5E20)
                LoadingButtonState.ERROR -> Color(0xFFB71C1C)
            },
            animationSpec = tween(400, easing = FastOutSlowInEasing),
            label = "gradient_end",
        )

        Box(
            modifier = Modifier
                .width(buttonWidth)
                .height(56.dp)
                .align(Alignment.Center)
                .scale(pressScale)
                .clip(RoundedCornerShape(cornerRadius))
                .background(Brush.linearGradient(listOf(gradientStart, gradientEnd)))
                .then(
                    Modifier.clickableNoRipple(
                        enabled = state == LoadingButtonState.IDLE,
                        interactionSource = interactionSource,
                        onClick = onClick,
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = state,
                transitionSpec = {
                    when {
                        // Text -> Spinner: text slides up out, spinner fades in
                        initialState == LoadingButtonState.IDLE ->
                            fadeIn(tween(200)) togetherWith
                                (fadeOut(tween(150)) + slideOutVertically(tween(200)) { -it / 2 })

                        // Spinner -> result icon: spinner shrinks out, icon pops in
                        targetState == LoadingButtonState.SUCCESS || targetState == LoadingButtonState.ERROR ->
                            scaleIn(
                                spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
                                initialScale = 0.4f,
                            ) + fadeIn(tween(100)) togetherWith scaleOut(tween(150)) + fadeOut(tween(100))

                        // Icon -> text (reset): icon fades, text slides in from below
                        else ->
                            (fadeIn(tween(250)) + slideInVertically(tween(300)) { it / 2 }) togetherWith
                                fadeOut(tween(150))
                    }
                },
                label = "button_content",
            ) { currentState ->
                when (currentState) {
                    LoadingButtonState.IDLE -> Text(
                        text = text,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                    )
                    LoadingButtonState.LOADING -> SpinnerIcon()
                    LoadingButtonState.SUCCESS -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp),
                    )
                    LoadingButtonState.ERROR -> Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun Modifier.clickableNoRipple(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
): Modifier = this.then(
    Modifier.clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick,
    )
)

// ---------------------------------------------------------------------------
// Spinner
// ---------------------------------------------------------------------------

@Composable
private fun SpinnerIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "spinner_rotation",
    )

    // Sweeping arc length - makes spinner feel more dynamic
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = 280f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "spinner_sweep",
    )

    Canvas(modifier = Modifier.size(26.dp)) {
        rotate(degrees = rotation, pivot = center) {
            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF0E0E0E)
@Composable
private fun LoadingButtonIdlePreview() {
    Box(
        modifier = Modifier
            .size(width = 300.dp, height = 100.dp)
            .background(Color(0xFF0E0E0E)),
        contentAlignment = Alignment.Center,
    ) {
        LoadingButtonComponent(text = "Confirm payment", state = LoadingButtonState.IDLE, onClick = {})
    }
}