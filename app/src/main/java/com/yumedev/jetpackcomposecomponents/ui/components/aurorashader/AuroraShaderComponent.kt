package com.yumedev.jetpackcomposecomponents.ui.components.aurorashader

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AuroraShaderComponent(onNavigateBack: () -> Unit) {
    var time by remember { mutableFloatStateOf(0f) }
    var pointer by remember { mutableStateOf(Offset(-9999f, -9999f)) }
    var pointerActive by remember { mutableStateOf(false) }

    val shader = remember { RuntimeShader(AURORA_AGSL) }

    LaunchedEffect(Unit) {
        var startNanos = 0L
        withFrameNanos { startNanos = it }
        while (true) {
            withFrameNanos { nanos ->
                time = (nanos - startNanos) / 1_000_000_000f
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "floatY",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue
                        when (event.type) {
                            PointerEventType.Press -> {
                                pointerActive = true
                                pointer = change.position
                            }
                            PointerEventType.Move -> {
                                pointer = change.position
                                change.consume()
                            }
                            PointerEventType.Release -> pointerActive = false
                        }
                    }
                }
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            shader.setFloatUniform("time", time)
            shader.setFloatUniform("resolution", size.width, size.height)
            shader.setFloatUniform("pointer", pointer.x, pointer.y)
            shader.setFloatUniform("pointerActive", if (pointerActive) 1f else 0f)
            drawRect(brush = ShaderBrush(shader))
        }

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.10f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .offset(y = floatY.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF07101F).copy(alpha = 0.75f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(28.dp))
                    .padding(horizontal = 28.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "TROMSØ, NORWAY",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.8.sp,
                    )
                    Text(
                        text = "11:42 PM",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "-8°",
                    color = Color.White,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Thin,
                    lineHeight = 80.sp,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Northern Lights Active",
                    color = Color(0xFF00E5A8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.4.sp,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.10f)),
                )

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    WeatherStat(label = "KP INDEX", value = "7.2")
                    WeatherStat(label = "VISIBILITY", value = "High")
                    WeatherStat(label = "WIND", value = "12 km/h")
                }
            }
        }

        Text(
            text = "Touch the sky to interact  ·  AGSL RuntimeShader",
            color = Color.White,
            fontSize = 11.sp,
            letterSpacing = 0.4.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.40f),
            fontSize = 9.sp,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// language=AGSL
private const val AURORA_AGSL = """
uniform float time;
uniform float2 resolution;
uniform float2 pointer;
uniform float pointerActive;

float hash(float2 p) {
    p = fract(p * float2(234.34, 435.345));
    p += dot(p, p + 34.23);
    return fract(p.x * p.y);
}

float noise(float2 p) {
    float2 i = floor(p);
    float2 f = fract(p);
    float2 u = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(hash(i), hash(i + float2(1, 0)), u.x),
        mix(hash(i + float2(0, 1)), hash(i + float2(1, 1)), u.x),
        u.y
    );
}

float fbm(float2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 5; i++) {
        v += a * noise(p);
        p *= 2.0;
        a *= 0.5;
    }
    return v;
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    float t = time * 0.25;
    float2 touchNorm = pointer / resolution;

    // Touch ripple distortion
    float2 delta = uv - touchNorm;
    float dist = length(delta);
    float2 dir = delta / max(dist, 0.001);
    float ripple = pointerActive * exp(-dist * 7.0) * sin(dist * 18.0 - time * 4.0) * 0.045;
    float2 uv2 = uv + dir * ripple;

    // FBM domain warp
    float warp = fbm(float2(uv2.x * 1.8 + t, uv2.y * 1.6 - t * 0.4)) * 0.35;
    float y = uv2.y + warp;

    // Layered sine waves
    float wx = uv2.x * 3.14159;
    float wave = sin(wx * 1.2 + t * 1.8) * 0.07
               + sin(wx * 2.5 - t * 1.1) * 0.04
               + sin(wx * 4.1 + t * 0.7) * 0.022;

    // Aurora bands
    float b1 = exp(-pow((y - 0.40 - wave) * 7.5, 2.0));
    float b2 = exp(-pow((y - 0.56 - wave * 0.7) * 11.0, 2.0)) * 0.55;
    float b3 = exp(-pow((y - 0.22 - wave * 1.1) * 13.0, 2.0)) * 0.28;
    float aurora = b1 + b2 + b3;

    // Color palette
    float shift = noise(float2(uv2.x * 3.5 + t * 0.8, 0.5));
    float3 teal   = float3(0.0,  0.88, 0.62);
    float3 green  = float3(0.08, 0.72, 0.28);
    float3 violet = float3(0.48, 0.05, 0.92);
    float3 blue   = float3(0.0,  0.28, 0.95);

    float3 col = mix(teal, green, sin(wx * 0.8 + t * 0.6) * 0.5 + 0.5);
    col = mix(col, violet, shift * 0.55);
    col = mix(col, blue, b2 * 0.7);
    col *= 1.0 + b1 * 0.4;

    // Touch glow
    float glow = pointerActive * exp(-dist * 4.5) * 0.5;
    col += float3(0.15, 0.45, 0.95) * glow;

    // Night sky gradient
    float3 sky = mix(float3(0.015, 0.0, 0.048), float3(0.0, 0.018, 0.07), uv.y);

    // Stars with twinkle
    float2 sUV = floor(uv * 220.0) / 220.0;
    float sr = hash(sUV * 91.3);
    float star = step(0.972, sr) * (0.35 + 0.65 * sin(time * (1.5 + sr * 6.0) + sr * 6.283));
    float3 stars = float3(star) * max(0.0, 1.0 - aurora * 2.5);

    float3 final = sky + col * aurora + stars;

    // Vignette
    float2 vig = uv * 2.0 - 1.0;
    final *= 1.0 - dot(vig, vig) * 0.28;

    return half4(half3(clamp(final, 0.0, 1.0)), 1.0);
}
"""