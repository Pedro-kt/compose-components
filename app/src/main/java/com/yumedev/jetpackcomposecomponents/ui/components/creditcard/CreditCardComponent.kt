package com.yumedev.jetpackcomposecomponents.ui.components.creditcard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// Model
// ---------------------------------------------------------------------------

data class CardData(
    val cardNumber: String = "4532 1234 5678 9012",
    val cardHolder: String = "ALEJANDRO BUSTAMANTE",
    val expiryDate: String = "12/28",
    val cvv: String = "123",
    val network: CardNetwork = CardNetwork.VISA,
)

enum class CardNetwork { VISA, MASTERCARD, AMEX }

// ---------------------------------------------------------------------------
// Public composable
// ---------------------------------------------------------------------------

@Composable
fun CreditCardComponent(
    cardData: CardData = CardData(),
    modifier: Modifier = Modifier,
) {
    var isFlipped by remember { mutableStateOf(false) }
    var isDataHidden by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "card_rotation",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .height(210.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 14f * density
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { isFlipped = !isFlipped },
        ) {
            if (rotation <= 90f) {
                CardFront(cardData = cardData, isDataHidden = isDataHidden)
            } else {
                CardBack(
                    cardData = cardData,
                    isDataHidden = isDataHidden,
                    modifier = Modifier.graphicsLayer { rotationY = 180f },
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = { isFlipped = !isFlipped },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF9B7FE8),
                ),
            ) {
                Text(if (!isFlipped) "Show back" else "Show front")
            }
            OutlinedButton(
                onClick = { isDataHidden = !isDataHidden },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF9B7FE8),
                ),
            ) {
                Text(if (isDataHidden) "Show data" else "Hide data")
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Card faces
// ---------------------------------------------------------------------------

@Composable
internal fun CardFront(
    cardData: CardData,
    isDataHidden: Boolean,
    modifier: Modifier = Modifier,
    gradient: Brush = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF1A1A2E),
            0.5f to Color(0xFF16213E),
            1.0f to Color(0xFF533483),
        ),
    ),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .shadow(elevation = 20.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(brush = gradient)
            .padding(horizontal = 22.dp, vertical = 18.dp),
    ) {
        DecorationCircles()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Row 1: chip + contactless
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChipIcon()
                ContactlessIcon()
            }

            // Row 2: card number
            Text(
                text = maskedNumber(cardData.cardNumber, isDataHidden),
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.4.sp,
                style = TextStyle(fontFeatureSettings = "tnum"),
            )

            // Row 3: holder | expiry | network
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    CardLabel("HOLDER")
                    CardValue(if (isDataHidden) "**** **********" else cardData.cardHolder)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CardLabel("EXPIRES")
                    CardValue(if (isDataHidden) "**/**" else cardData.expiryDate)
                }
                NetworkLogo(cardData.network)
            }
        }
    }
}

@Composable
private fun CardBack(
    cardData: CardData,
    isDataHidden: Boolean,
    modifier: Modifier = Modifier,
) {
    val gradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF0D0D1A),
            0.5f to Color(0xFF1A1A2E),
            1.0f to Color(0xFF2D1B5E),
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .shadow(elevation = 20.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(brush = gradient),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(28.dp))

            // Magnetic stripe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color(0xFF0A0A0A)),
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Signature strip + CVV
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SignatureStrip(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                CvvBox(cvv = cardData.cvv, isHidden = isDataHidden)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "This card is property\nof the issuing bank.",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 7.sp,
                    lineHeight = 11.sp,
                )
                NetworkLogo(cardData.network)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Sub-components
// ---------------------------------------------------------------------------

@Composable
internal fun DecorationCircles() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = Color.White.copy(alpha = 0.04f),
            radius = size.width * 0.65f,
            center = Offset(size.width * 0.9f, -size.height * 0.25f),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.04f),
            radius = size.width * 0.45f,
            center = Offset(-size.width * 0.05f, size.height * 1.15f),
        )
    }
}

@Composable
internal fun ChipIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(44.dp, 32.dp)) {
        val gold = Color(0xFFD4AF37)
        val goldDark = Color(0xFFAA8800)

        drawRoundRect(color = gold, cornerRadius = CornerRadius(3.dp.toPx()), size = size)

        // Grid lines
        val strokeW = 1.dp.toPx()
        drawLine(goldDark, Offset(size.width * 0.33f, 0f), Offset(size.width * 0.33f, size.height), strokeWidth = strokeW)
        drawLine(goldDark, Offset(size.width * 0.67f, 0f), Offset(size.width * 0.67f, size.height), strokeWidth = strokeW)
        drawLine(goldDark, Offset(0f, size.height * 0.37f), Offset(size.width, size.height * 0.37f), strokeWidth = strokeW)
        drawLine(goldDark, Offset(0f, size.height * 0.63f), Offset(size.width, size.height * 0.63f), strokeWidth = strokeW)

        // Center square
        drawRoundRect(
            color = goldDark,
            topLeft = Offset(size.width * 0.33f, size.height * 0.37f),
            size = Size(size.width * 0.34f, size.height * 0.26f),
            cornerRadius = CornerRadius(1.5.dp.toPx()),
        )
    }
}

@Composable
internal fun ContactlessIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(26.dp, 26.dp)) {
        val strokeW = 2.dp.toPx()
        listOf(0.22f, 0.40f, 0.58f).forEach { ratio ->
            val r = size.width * ratio
            drawArc(
                color = Color.White.copy(alpha = 0.8f),
                startAngle = 225f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = strokeW),
            )
        }
        drawCircle(color = Color.White.copy(alpha = 0.8f), radius = strokeW * 0.8f, center = center)
    }
}

@Composable
internal fun NetworkLogo(network: CardNetwork, modifier: Modifier = Modifier) {
    when (network) {
        CardNetwork.VISA -> Text(
            text = "VISA",
            modifier = modifier,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            letterSpacing = 1.sp,
        )

        CardNetwork.MASTERCARD -> Canvas(
            modifier = modifier.size(48.dp, 30.dp),
        ) {
            drawCircle(Color(0xFFEB001B), radius = size.height * 0.46f, center = Offset(size.width * 0.37f, size.height * 0.5f))
            drawCircle(Color(0xFFF79E1B), radius = size.height * 0.46f, center = Offset(size.width * 0.63f, size.height * 0.5f))
            // Overlap blending via a semitransparent circle in the center
            drawCircle(Color(0xFFFF5500).copy(alpha = 0.7f), radius = size.height * 0.23f, center = Offset(size.width * 0.5f, size.height * 0.5f))
        }

        CardNetwork.AMEX -> Text(
            text = "AMEX",
            modifier = modifier,
            color = Color(0xFF6EC6FF),
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
        )
    }
}

@Composable
private fun SignatureStrip(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(36.dp)
            .background(
                Brush.linearGradient(listOf(Color(0xFFE8E8E8), Color(0xFFF5F5F5), Color(0xFFE0E0E0))),
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = "AUTHORIZED SIGNATURE",
            modifier = Modifier.padding(start = 8.dp),
            color = Color.Gray,
            fontSize = 7.sp,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
private fun CvvBox(cvv: String, isHidden: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 36.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isHidden) "***" else cvv,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
internal fun CardLabel(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.55f),
        fontSize = 8.sp,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
internal fun CardValue(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
    )
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

internal fun maskedNumber(cardNumber: String, isHidden: Boolean): String {
    if (!isHidden) return cardNumber
    val last4 = cardNumber.filter { it.isDigit() }.takeLast(4)
    return "**** **** **** $last4"
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun CreditCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center,
    ) {
        CreditCardComponent()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, name = "Mastercard")
@Composable
private fun CreditCardMastercardPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center,
    ) {
        CreditCardComponent(
            cardData = CardData(
                cardNumber = "5412 7534 1234 5678",
                cardHolder = "ALEJANDRO BUSTAMANTE",
                expiryDate = "08/27",
                cvv = "456",
                network = CardNetwork.MASTERCARD,
            ),
        )
    }
}