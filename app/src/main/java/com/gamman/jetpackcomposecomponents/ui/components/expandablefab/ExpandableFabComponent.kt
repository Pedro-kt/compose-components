package com.gamman.jetpackcomposecomponents.ui.components.expandablefab

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// Model
// ---------------------------------------------------------------------------

data class FabAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit = {},
)

val defaultFabActions = listOf(
    FabAction(Icons.Default.Share, "Share"),
    FabAction(Icons.Default.CameraAlt, "Camera"),
    FabAction(Icons.Default.Favorite, "Favorite"),
    FabAction(Icons.Default.Send, "Send"),
)

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

@Composable
fun ExpandableFabComponent(
    actions: List<FabAction> = defaultFabActions,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Main FAB icon rotation: 0 -> 45 degrees
    val iconRotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "fab_icon_rotation",
    )

    // Per-item staggered alpha + translationY
    val itemAnimations = actions.mapIndexed { index, _ ->
        val staggerDelay = if (isExpanded) index * 60 else (actions.lastIndex - index) * 60
        val alpha by animateFloatAsState(
            targetValue = if (isExpanded) 1f else 0f,
            animationSpec = tween(durationMillis = 250, delayMillis = staggerDelay, easing = FastOutSlowInEasing),
            label = "fab_item_alpha_$index",
        )
        val offsetY by animateFloatAsState(
            targetValue = if (isExpanded) 0f else 40f,
            animationSpec = tween(durationMillis = 300, delayMillis = staggerDelay, easing = FastOutSlowInEasing),
            label = "fab_item_offset_$index",
        )
        alpha to offsetY
    }

    // Scrim alpha
    val scrimAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 0.5f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "scrim_alpha",
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Scrim
        if (scrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = scrimAlpha }
                    .background(Color.Black)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { isExpanded = false },
            )
        }

        // FAB stack - bottom right
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Mini actions (rendered bottom-to-top via reversed)
            actions.reversed().forEachIndexed { reversedIndex, action ->
                val originalIndex = actions.lastIndex - reversedIndex
                val (alpha, offsetY) = itemAnimations[originalIndex]

                if (alpha > 0f) {
                    FabActionItem(
                        action = action,
                        modifier = Modifier.graphicsLayer {
                            this.alpha = alpha
                            translationY = offsetY
                        },
                    )
                }
            }

            // Main FAB
            MainFab(
                iconRotation = iconRotation,
                onClick = { isExpanded = !isExpanded },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Sub-components
// ---------------------------------------------------------------------------

@Composable
private fun MainFab(
    iconRotation: Float,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .shadow(elevation = 12.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF533483), Color(0xFF9B7FE8)),
                ),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Expandir acciones",
            tint = Color.White,
            modifier = Modifier
                .size(30.dp)
                .graphicsLayer { rotationZ = iconRotation },
        )
    }
}

@Composable
private fun FabActionItem(
    action: FabAction,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Label
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1C1C2E))
                .border(1.dp, Color(0xFF533483).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = action.label,
                color = Color.White,
                fontSize = 13.sp,
            )
        }

        // Mini FAB
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color(0xFF1C1C2E))
                .border(1.dp, Color(0xFF533483).copy(alpha = 0.5f), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = action.onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = Color(0xFF9B7FE8),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF0E0E0E)
@Composable
private fun ExpandableFabPreview() {
    ExpandableFabComponent()
}