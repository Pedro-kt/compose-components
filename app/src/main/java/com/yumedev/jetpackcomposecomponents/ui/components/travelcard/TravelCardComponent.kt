package com.yumedev.jetpackcomposecomponents.ui.components.travelcard

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yumedev.jetpackcomposecomponents.R

// ---------------------------------------------------------------------------
// Model
// ---------------------------------------------------------------------------

private data class Destination(
    val id: String,
    val name: String,
    val location: String,
    val country: String,
    val rating: Float,
    val reviewCount: Int,
    val pricePerNight: Int,
    val description: String,
    @DrawableRes val imageRes: Int,
    val amenities: List<Pair<ImageVector, String>>,
)

private val sampleDestination = Destination(
    id = "old_town_tallinn",
    name = "Old Town",
    location = "Tallinn",
    country = "Estonia",
    rating = 4.9f,
    reviewCount = 3_214,
    pricePerNight = 95,
    description = "One of the best-preserved medieval cities in Northern Europe, Tallinn's Old Town is a UNESCO World Heritage Site. Wander through limestone towers, cobblestone alleys, and merchant houses that have stood since the 13th century. The Town Hall Square comes alive in every season, from summer terraces to the famous Christmas market. A destination that feels frozen in time in the best possible way.",
    imageRes = R.drawable.old_town,
    amenities = listOf(
        Icons.Default.Wifi to "Wi-Fi",
        Icons.Default.LocalDining to "Dining",
        Icons.Default.AccountBalance to "Museum",
        Icons.AutoMirrored.Filled.DirectionsWalk to "City Tour",
    ),
)

// ---------------------------------------------------------------------------
// Root
// ---------------------------------------------------------------------------

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TravelCardComponent(onNavigateBack: () -> Unit) {
    var isDetailVisible by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    BackHandler(enabled = isDetailVisible) {
        isDetailVisible = false
    }

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = isDetailVisible,
            transitionSpec = {
                fadeIn(tween(600)) togetherWith fadeOut(tween(400))
            },
            label = "travel_card_transition",
        ) { showDetail ->
            if (!showDetail) {
                CardScene(
                    destination = sampleDestination,
                    isSaved = isSaved,
                    onCardClick = { isDetailVisible = true },
                    onBack = onNavigateBack,
                    onToggleSave = { isSaved = !isSaved },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                )
            } else {
                DetailScene(
                    destination = sampleDestination,
                    isSaved = isSaved,
                    onBack = { isDetailVisible = false },
                    onToggleSave = { isSaved = !isSaved },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Card scene
// ---------------------------------------------------------------------------

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CardScene(
    destination: Destination,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
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
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }

        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(460.dp)
                    .align(Alignment.Center)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "destination_${destination.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCardClick,
                    ),
            ) {
                Image(
                    painter = painterResource(destination.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC000000)),
                                startY = 250f,
                            )
                        ),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                        .clickable(onClick = onToggleSave),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save destination",
                        tint = if (isSaved) Color(0xFFFF5A5F) else Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFFF5A5F),
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${destination.location}, ${destination.country}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = destination.name,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = destination.rating.toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = " (${formatReviewCount(destination.reviewCount)} reviews)",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 12.sp,
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "$${destination.pricePerNight}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = " /night",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Detail scene
// ---------------------------------------------------------------------------

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailScene(
    destination: Destination,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val scrollState = rememberScrollState()
    val heroHeight = 400.dp
    val sheetOverlap = 24.dp

    var sheetVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { sheetVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E0E)),
    ) {
        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heroHeight)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "destination_${destination.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
            ) {
                Image(
                    painter = painterResource(destination.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x88000000), Color.Transparent),
                            )
                        )
                )
            }
        }

        AnimatedVisibility(
            visible = sheetVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            ) + fadeIn(tween(500)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                Spacer(modifier = Modifier.height(heroHeight - sheetOverlap))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = sheetOverlap, topEnd = sheetOverlap))
                        .background(Color(0xFF0E0E0E))
                        .padding(24.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFFF5A5F),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${destination.location}, ${destination.country}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = destination.name,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < destination.rating.toInt()) Color(0xFFFFD700)
                                       else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${destination.rating}",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                        Text(
                            text = " · ${formatReviewCount(destination.reviewCount)} reviews",
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$${destination.pricePerNight}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "/ night",
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "About",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = destination.description,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Amenities",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        destination.amenities.forEach { (icon, label) ->
                            AmenityChip(icon = icon, label = label)
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5A5F)),
                    ) {
                        Text(
                            text = "Book Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }

        // Rendered last so it sits on top of the scroll column and receives touches
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable(onClick = onToggleSave),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Save destination",
                    tint = if (isSaved) Color(0xFFFF5A5F) else Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Amenity chip
// ---------------------------------------------------------------------------

@Composable
private fun AmenityChip(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.07f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 10.sp,
        )
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun formatReviewCount(count: Int): String =
    if (count >= 1_000) "${count / 1_000}.${(count % 1_000) / 100}k" else count.toString()