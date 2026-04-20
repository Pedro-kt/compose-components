package com.gamman.jetpackcomposecomponents.ui.components.barchart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// Model
// ---------------------------------------------------------------------------

data class BarData(
    val label: String,
    val value: Float,
)

val sampleWeekData = listOf(
    BarData("Mon", 4200f),
    BarData("Tue", 7800f),
    BarData("Wed", 5100f),
    BarData("Thu", 9400f),
    BarData("Fri", 8600f),
    BarData("Sat", 11200f),
    BarData("Sun", 6300f),
)

val sampleMonthData = listOf(
    BarData("Jan", 32000f),
    BarData("Feb", 28500f),
    BarData("Mar", 41000f),
    BarData("Apr", 38700f),
    BarData("May", 52000f),
    BarData("Jun", 47300f),
    BarData("Jul", 61000f),
    BarData("Aug", 55400f),
    BarData("Sep", 49800f),
    BarData("Oct", 67200f),
    BarData("Nov", 72000f),
    BarData("Dec", 85000f),
)

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

@Composable
fun BarChartComponent(
    data: List<BarData>,
    modifier: Modifier = Modifier,
    onBarSelected: ((BarData?) -> Unit)? = null,
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val textMeasurer = rememberTextMeasurer()

    val maxValue = data.maxOfOrNull { it.value } ?: 1f

    // Staggered bar height animations
    val animatedHeights = data.mapIndexed { index, bar ->
        animateFloatAsState(
            targetValue = bar.value / maxValue,
            animationSpec = tween(
                durationMillis = 700,
                delayMillis = index * 70,
                easing = FastOutSlowInEasing,
            ),
            label = "bar_height_$index",
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(data) {
                detectTapGestures { tapOffset ->
                    val labelZoneHeight = 32.dp.toPx()
                    val chartHeight = size.height - labelZoneHeight
                    val totalWidth = size.width.toFloat()
                    val barSpacing = 10.dp.toPx()
                    val barWidth = (totalWidth - barSpacing * (data.size + 1)) / data.size

                    val tappedIndex = data.indices.firstOrNull { i ->
                        val left = barSpacing + i * (barWidth + barSpacing)
                        tapOffset.x in left..(left + barWidth) && tapOffset.y < chartHeight
                    } ?: -1

                    selectedIndex = if (selectedIndex == tappedIndex) -1 else tappedIndex
                    onBarSelected?.invoke(if (selectedIndex >= 0) data[selectedIndex] else null)
                }
            },
    ) {
        val labelZoneHeight = 32.dp.toPx()
        val topPadding = 32.dp.toPx()
        val chartHeight = size.height - labelZoneHeight - topPadding
        val totalWidth = size.width
        val barSpacing = 10.dp.toPx()
        val barWidth = (totalWidth - barSpacing * (data.size + 1)) / data.size
        val cornerRadius = 6.dp.toPx()

        // Grid lines
        val gridLevels = listOf(0.25f, 0.5f, 0.75f, 1f)
        gridLevels.forEach { level ->
            val y = topPadding + chartHeight * (1f - level)
            drawLine(
                color = Color.White.copy(alpha = 0.07f),
                start = Offset(0f, y),
                end = Offset(totalWidth, y),
                strokeWidth = 1.dp.toPx(),
            )
            // Y-axis label
            val labelText = formatValue(maxValue * level)
            val measured = textMeasurer.measure(
                labelText,
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 9.sp,
                ),
            )
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(4.dp.toPx(), y - measured.size.height - 2.dp.toPx()),
            )
        }

        // Bars
        data.forEachIndexed { index, bar ->
            val animatedRatio by animatedHeights[index]
            val left = barSpacing + index * (barWidth + barSpacing)
            val barPixelHeight = chartHeight * animatedRatio
            val top = topPadding + chartHeight - barPixelHeight
            val bottom = topPadding + chartHeight

            val isSelected = index == selectedIndex
            val barBrush = if (isSelected) {
                Brush.linearGradient(
                    colors = listOf(Color(0xFFCCB3FF), Color(0xFF9B7FE8)),
                    start = Offset(left, top),
                    end = Offset(left, bottom),
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF9B7FE8), Color(0xFF533483)),
                    start = Offset(left, top),
                    end = Offset(left, bottom),
                )
            }

            // Top-rounded bar path
            if (barPixelHeight > cornerRadius) {
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(left, top, left + barWidth, bottom),
                            topLeft = CornerRadius(cornerRadius),
                            topRight = CornerRadius(cornerRadius),
                            bottomLeft = CornerRadius(0f),
                            bottomRight = CornerRadius(0f),
                        ),
                    )
                }
                drawPath(path, brush = barBrush)
            }

            // X-axis label
            val labelMeasured = textMeasurer.measure(
                bar.label,
                style = TextStyle(
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                ),
            )
            drawText(
                textLayoutResult = labelMeasured,
                topLeft = Offset(
                    x = left + barWidth / 2f - labelMeasured.size.width / 2f,
                    y = topPadding + chartHeight + 8.dp.toPx(),
                ),
            )

            // Tooltip on selected bar
            if (isSelected && barPixelHeight > 0f) {
                drawTooltip(
                    value = bar.value,
                    barLeft = left,
                    barWidth = barWidth,
                    barTop = top,
                    cornerRadius = cornerRadius,
                    textMeasurer = textMeasurer,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun DrawScope.drawTooltip(
    value: Float,
    barLeft: Float,
    barWidth: Float,
    barTop: Float,
    cornerRadius: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
) {
    val text = formatValue(value)
    val measured = textMeasurer.measure(
        text,
        style = TextStyle(
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        ),
    )
    val padding = 6.dp.toPx()
    val tooltipW = measured.size.width + padding * 2
    val tooltipH = measured.size.height + padding * 2
    val tooltipX = (barLeft + barWidth / 2f - tooltipW / 2f).coerceIn(0f, size.width - tooltipW)
    val tooltipY = barTop - tooltipH - 8.dp.toPx()

    if (tooltipY >= 0f) {
        drawRoundRect(
            color = Color(0xFF3A2A5A),
            topLeft = Offset(tooltipX, tooltipY),
            size = Size(tooltipW, tooltipH),
            cornerRadius = CornerRadius(cornerRadius),
        )
        drawText(
            textLayoutResult = measured,
            topLeft = Offset(tooltipX + padding, tooltipY + padding),
        )
    }
}

private fun formatValue(value: Float): String = when {
    value >= 1_000_000f -> "${"%.1f".format(value / 1_000_000f)}M"
    value >= 1_000f -> "${"%.1f".format(value / 1_000f)}K"
    else -> value.toInt().toString()
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF0E0E0E)
@Composable
private fun BarChartPreview() {
    BarChartComponent(
        data = sampleWeekData,
        modifier = Modifier
            .fillMaxSize(),
    )
}