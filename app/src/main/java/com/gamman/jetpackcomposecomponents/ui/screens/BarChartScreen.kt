package com.gamman.jetpackcomposecomponents.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import com.gamman.jetpackcomposecomponents.ui.components.barchart.BarChartComponent
import com.gamman.jetpackcomposecomponents.ui.components.barchart.BarData
import com.gamman.jetpackcomposecomponents.ui.components.barchart.sampleMonthData
import com.gamman.jetpackcomposecomponents.ui.components.barchart.sampleWeekData

private enum class ChartPeriod { WEEK, MONTH }

@Composable
fun BarChartScreen(onBack: () -> Unit) {
    var period by remember { mutableStateOf(ChartPeriod.WEEK) }
    var selectedBar by remember { mutableStateOf<BarData?>(null) }

    val data = if (period == ChartPeriod.WEEK) sampleWeekData else sampleMonthData

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
                text = "Sales",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        // Summary card
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(
                text = if (selectedBar != null) selectedBar!!.label else "Total",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 12.sp,
            )
            Text(
                text = if (selectedBar != null) {
                    "$ ${"%.0f".format(selectedBar!!.value)}"
                } else {
                    "$ ${"%.0f".format(data.sumOf { it.value.toDouble() })}"
                },
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Period toggle
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChartPeriod.entries.forEach { p ->
                val isSelected = period == p
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF533483) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else Color(0xFF2A2A3A),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            period = p
                            selectedBar = null
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (p == ChartPeriod.WEEK) "Week" else "Year",
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.45f),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart
        AnimatedContent(
            targetState = period,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
            label = "chart_transition",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) { currentPeriod ->
            BarChartComponent(
                data = if (currentPeriod == ChartPeriod.WEEK) sampleWeekData else sampleMonthData,
                onBarSelected = { selectedBar = it },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}