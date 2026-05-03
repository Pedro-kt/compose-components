package com.yumedev.jetpackcomposecomponents.ui.components.walletstack

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.yumedev.jetpackcomposecomponents.ui.components.creditcard.CardData
import com.yumedev.jetpackcomposecomponents.ui.components.creditcard.CardFront
import com.yumedev.jetpackcomposecomponents.ui.components.creditcard.CardNetwork

// ---------------------------------------------------------------------------
// Model
// ---------------------------------------------------------------------------

private data class WalletEntry(
    val id: Int,
    val bank: String,
    val balance: String,
    val cardData: CardData,
    val gradient: Brush,
)

private val sampleEntries = listOf(
    WalletEntry(
        id = 0,
        bank = "Chase",
        balance = "$4,250.00",
        cardData = CardData(
            cardNumber = "4532 8821 6701 4123",
            cardHolder = "PETER DHOENE",
            expiryDate = "08/28",
            cvv = "371",
            network = CardNetwork.VISA,
        ),
        gradient = Brush.linearGradient(listOf(Color(0xFF0D47A1), Color(0xFF1565C0))),
    ),
    WalletEntry(
        id = 1,
        bank = "CitiBank",
        balance = "$1,830.50",
        cardData = CardData(
            cardNumber = "5412 9956 3301 8834",
            cardHolder = "PETER DHOENE",
            expiryDate = "03/27",
            cvv = "582",
            network = CardNetwork.MASTERCARD,
        ),
        gradient = Brush.linearGradient(listOf(Color(0xFF4A148C), Color(0xFF6A1B9A))),
    ),
    WalletEntry(
        id = 2,
        bank = "Wells Fargo",
        balance = "$765.20",
        cardData = CardData(
            cardNumber = "4916 2234 0981 6673",
            cardHolder = "PETER DHOENE",
            expiryDate = "06/29",
            cvv = "219",
            network = CardNetwork.VISA,
        ),
        gradient = Brush.linearGradient(listOf(Color(0xFF7F0000), Color(0xFFB71C1C))),
    ),
    WalletEntry(
        id = 3,
        bank = "HSBC",
        balance = "$9,100.75",
        cardData = CardData(
            cardNumber = "5500 1122 9087 2219",
            cardHolder = "PETER DHOENE",
            expiryDate = "11/26",
            cvv = "447",
            network = CardNetwork.MASTERCARD,
        ),
        gradient = Brush.linearGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))),
    ),
)

// ---------------------------------------------------------------------------
// Root
// ---------------------------------------------------------------------------

@Composable
fun WalletStackComponent(onNavigateBack: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }

    BackHandler(enabled = isExpanded) { isExpanded = false }

    val orderedEntries = remember(selectedIndex) {
        buildList {
            add(sampleEntries[selectedIndex])
            sampleEntries.forEachIndexed { i, entry -> if (i != selectedIndex) add(entry) }
        }
    }

    val cardHeight = 200.dp
    val collapsedPeek = 20.dp
    val expandedSpacing = 148.dp

    val collapsedContainerHeight = cardHeight + collapsedPeek * (sampleEntries.size - 1)
    val expandedContainerHeight = cardHeight + expandedSpacing * (sampleEntries.size - 1)

    val containerHeight by animateDpAsState(
        targetValue = if (isExpanded) expandedContainerHeight else collapsedContainerHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "container_height",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E0E)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 52.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(60.dp))

            Text(
                text = "My Wallet",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${sampleEntries.size} cards",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 13.sp,
            )

            Spacer(Modifier.height(28.dp))

            AnimatedContent(
                targetState = selectedIndex,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label = "balance_transition",
            ) { idx ->
                val entry = sampleEntries[idx]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = entry.balance,
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${entry.bank} •••• ${entry.cardData.cardNumber.takeLast(4)}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            // Card stack
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(containerHeight),
            ) {
                orderedEntries.forEachIndexed { displayIndex, entry ->
                    key(entry.id) {
                        val targetY = if (isExpanded) {
                            expandedSpacing * displayIndex
                        } else {
                            collapsedPeek * displayIndex
                        }

                        val animatedY by animateDpAsState(
                            targetValue = targetY,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                            label = "y_${entry.id}",
                        )

                        val targetScale = if (!isExpanded) 1f - displayIndex * 0.03f else 1f
                        val animatedScale by animateFloatAsState(
                            targetValue = targetScale,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                            label = "scale_${entry.id}",
                        )

                        val cardInteractionSource = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(cardHeight)
                                .zIndex((sampleEntries.size - displayIndex).toFloat())
                                .offset(y = animatedY)
                                .scale(animatedScale)
                                .clickable(
                                    interactionSource = cardInteractionSource,
                                    indication = null,
                                ) {
                                    if (isExpanded) {
                                        selectedIndex = sampleEntries.indexOf(entry)
                                        isExpanded = false
                                    } else {
                                        isExpanded = true
                                    }
                                },
                        ) {
                            CardFront(
                                cardData = entry.cardData,
                                isDataHidden = false,
                                gradient = entry.gradient,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Dot indicators
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                sampleEntries.indices.forEach { idx ->
                    val isActive = idx == selectedIndex
                    val dotWidth by animateDpAsState(
                        targetValue = if (isActive) 20.dp else 6.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "dot_$idx",
                    )
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(
                                if (isActive) Color.White else Color.White.copy(alpha = 0.3f),
                            ),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                label = "hint_transition",
            ) { expanded ->
                Text(
                    text = if (expanded) "Tap a card to select" else "Tap to expand",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 12.sp,
                )
            }

            Spacer(Modifier.height(48.dp))
        }

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
    }
}