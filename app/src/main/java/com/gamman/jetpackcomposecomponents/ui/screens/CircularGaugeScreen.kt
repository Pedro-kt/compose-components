package com.gamman.jetpackcomposecomponents.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamman.jetpackcomposecomponents.ui.components.circulargauge.CircularGaugeComponent
import kotlin.random.Random

@Composable
fun CircularGaugeScreen(onBack: () -> Unit) {
    var cpu by remember { mutableFloatStateOf(65f) }
    var memory by remember { mutableFloatStateOf(42f) }
    var battery by remember { mutableFloatStateOf(88f) }
    var network by remember { mutableFloatStateOf(28f) }

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
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2x2 Gauge grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                CircularGaugeComponent(value = cpu, label = "CPU")
                CircularGaugeComponent(value = memory, label = "Memory")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                CircularGaugeComponent(value = battery, label = "Battery")
                CircularGaugeComponent(value = network, label = "Network")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sliders
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            GaugeSlider(label = "CPU", value = cpu, onValueChange = { cpu = it })
            GaugeSlider(label = "Memory", value = memory, onValueChange = { memory = it })
            GaugeSlider(label = "Battery", value = battery, onValueChange = { battery = it })
            GaugeSlider(label = "Network", value = network, onValueChange = { network = it })
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            OutlinedButton(
                onClick = {
                    cpu = Random.nextFloat() * 100f
                    memory = Random.nextFloat() * 100f
                    battery = Random.nextFloat() * 100f
                    network = Random.nextFloat() * 100f
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF9B7FE8)),
            ) {
                Text("Randomize values")
            }
        }
    }
}

@Composable
private fun GaugeSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 0.dp),
            fontWeight = FontWeight.Medium,
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF9B7FE8),
                activeTrackColor = Color(0xFF533483),
                inactiveTrackColor = Color(0xFF2A2A3A),
            ),
        )
        Text(
            text = "${value.toInt()}%",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
        )
    }
}