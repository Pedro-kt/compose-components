package com.gamman.jetpackcomposecomponents.ui.components.circulargauge

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

@Composable
fun CircularGaugeComponent(
    value: Float,
    label: String,
    modifier: Modifier = Modifier,
    unit: String = "%",
    gaugeSize: Dp = 160.dp,
    strokeWidth: Dp = 14.dp,
) {
    val clampedValue = value.coerceIn(0f, 100f)

    val animatedValue by animateFloatAsState(
        targetValue = clampedValue,
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label = "gauge_value",
    )

    val arcColor = gaugeColor(animatedValue)
    val dimArcColor = gaugeColor(animatedValue).copy(alpha = 0.25f)

    // Arc spans 270 degrees, starting at 135 (bottom-left, clockwise, bottom-right)
    val totalSweep = 270f
    val animatedSweep = (animatedValue / 100f) * totalSweep

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(gaugeSize)) {
                val strokePx = strokeWidth.toPx()
                val diameter = size.width - strokePx
                val arcTopLeft = Offset(strokePx / 2f, strokePx / 2f)
                val arcSize = Size(diameter, diameter)
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = diameter / 2f

                // Track arc
                drawArc(
                    color = Color(0xFF2A2A3A),
                    startAngle = 135f,
                    sweepAngle = totalSweep,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                )

                // Tick marks
                val tickCount = 28 // 27 gaps = 270 deg / 10 deg per gap
                for (i in 0..tickCount) {
                    val angleDeg = 135.0 + i * (totalSweep / tickCount)
                    val angleRad = Math.toRadians(angleDeg)
                    val isMajor = i % 3 == 0
                    val outer = radius + strokePx / 2f + 6.dp.toPx()
                    val inner = outer + if (isMajor) 7.dp.toPx() else 4.dp.toPx()
                    drawLine(
                        color = Color.White.copy(alpha = if (isMajor) 0.2f else 0.1f),
                        start = Offset(
                            x = center.x + outer * cos(angleRad).toFloat(),
                            y = center.y + outer * sin(angleRad).toFloat(),
                        ),
                        end = Offset(
                            x = center.x + inner * cos(angleRad).toFloat(),
                            y = center.y + inner * sin(angleRad).toFloat(),
                        ),
                        strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx(),
                    )
                }

                if (animatedSweep > 0f) {
                    // Rotate canvas so sweep gradient aligns with arc start (135 deg)
                    rotate(degrees = 135f, pivot = center) {

                        // Progress arc with sweep gradient
                        drawArc(
                            brush = Brush.sweepGradient(
                                0f to dimArcColor,
                                (animatedSweep / 360f) to arcColor,
                                center = center,
                            ),
                            startAngle = 0f,
                            sweepAngle = animatedSweep,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = strokePx, cap = StrokeCap.Round),
                        )
                    }

                    // Tip dot
                    val tipAngleRad = Math.toRadians((135.0 + animatedSweep))
                    val tipCenter = Offset(
                        x = center.x + radius * cos(tipAngleRad).toFloat(),
                        y = center.y + radius * sin(tipAngleRad).toFloat(),
                    )
                    drawCircle(
                        color = Color.White,
                        radius = strokePx * 0.35f,
                        center = tipCenter,
                    )
                }
            }

            // Center text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${animatedValue.roundToInt()}$unit",
                    color = Color.White,
                    fontSize = (gaugeSize.value * 0.175f).sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = (gaugeSize.value * 0.085f).sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Color interpolation based on value
// ---------------------------------------------------------------------------

private fun gaugeColor(value: Float): Color {
    val low = Color(0xFF00E5FF)    // cyan
    val mid = Color(0xFFFFD740)    // amber
    val high = Color(0xFFFF1744)   // red

    return when {
        value < 50f -> lerp(low, mid, value / 50f)
        else -> lerp(mid, high, (value - 50f) / 50f)
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF0E0E0E)
@Composable
private fun CircularGaugePreview() {
    CircularGaugeComponent(value = 72f, label = "CPU")
}