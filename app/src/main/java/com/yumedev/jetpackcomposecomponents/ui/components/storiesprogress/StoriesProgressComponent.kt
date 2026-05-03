package com.yumedev.jetpackcomposecomponents.ui.components.storiesprogress

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.request.crossfade
import com.yumedev.jetpackcomposecomponents.R
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Model
// ---------------------------------------------------------------------------

private sealed class StoryBackground {
    data class Photo(@param:DrawableRes val imageRes: Int) : StoryBackground()
    data class Gradient(val colors: List<Color>) : StoryBackground()
}

private data class Story(
    val id: String,
    val background: StoryBackground,
    val location: String,
    val caption: String,
    val durationMs: Long = 5_000L,
)

private val stories = listOf(
    Story(
        id = "japan_1",
        background = StoryBackground.Photo(R.drawable.japan_1),
        location = "Fushimi Inari, Kyoto",
        caption = "Thousands of torii gates wind up the mountain, one after another.",
    ),
    Story(
        id = "japan_2",
        background = StoryBackground.Photo(R.drawable.japan_2),
        location = "Gion District, Kyoto",
        caption = "Wooden facades and paper lanterns — Kyoto after dark.",
    ),
    Story(
        id = "japan_3",
        background = StoryBackground.Photo(R.drawable.japan_3),
        location = "Suga Shrine, Tokyo",
        caption = "The stairs from Your Name. Still exactly as they appear in the film.",
    ),
    Story(
        id = "japan_4",
        background = StoryBackground.Photo(R.drawable.japan_4),
        location = "Mountain Shrine, Japan",
        caption = "Water runs through stone. A place that feels undiscovered.",
    ),
    Story(
        id = "japan_5",
        background = StoryBackground.Photo(R.drawable.japan_5),
        location = "Itsukushima, Miyajima",
        caption = "At high tide the torii appears to float. No photo does it justice.",
    ),
)

// ---------------------------------------------------------------------------
// Root
// ---------------------------------------------------------------------------

@Composable
fun StoriesProgressComponent(onNavigateBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }
    val context = LocalContext.current

    // Preload all images so transitions are instant with no blank flash
    LaunchedEffect(Unit) {
        stories.forEach { story ->
            val bg = story.background
            if (bg is StoryBackground.Photo) {
                context.imageLoader.enqueue(
                    ImageRequest.Builder(context).data(bg.imageRes).build()
                )
            }
        }
    }

    fun goToNext() {
        scope.launch {
            progress.snapTo(0f)
            currentIndex = (currentIndex + 1).coerceAtMost(stories.lastIndex)
        }
    }

    fun goToPrevious() {
        scope.launch {
            progress.snapTo(0f)
            currentIndex = (currentIndex - 1).coerceAtLeast(0)
        }
    }

    LaunchedEffect(currentIndex, isPaused) {
        if (!isPaused) {
            val remaining = (stories[currentIndex].durationMs * (1f - progress.value))
                .toLong()
                .coerceAtLeast(0L)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(remaining.toInt(), easing = LinearEasing),
            )
            // Auto-advance or loop back
            val next = (currentIndex + 1) % stories.size
            progress.snapTo(0f)
            currentIndex = next
        }
    }

    val story = stories[currentIndex]

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Background
        StoryBackgroundLayer(story.background)

        // Top scrim for readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x88000000), Color.Transparent)
                    )
                )
        )

        // Bottom scrim for caption readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xAA000000))
                    )
                )
        )

        // Touch handler — declared before UI so UI elements intercept their own taps first
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(currentIndex) {
                    detectTapGestures(
                        onPress = {
                            isPaused = true
                            tryAwaitRelease()
                            isPaused = false
                        },
                        onTap = { offset ->
                            if (offset.x < size.width / 2f) goToPrevious() else goToNext()
                        },
                    )
                }
        )

        // Top UI: progress bars + header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 12.dp),
        ) {
            ProgressBars(
                count = stories.size,
                currentIndex = currentIndex,
                currentProgress = progress.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            )
            Spacer(Modifier.height(12.dp))
            StoryHeader(
                story = story,
                onClose = onNavigateBack,
            )
        }

        // Caption at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = 48.dp),
        ) {
            Text(
                text = story.location,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = story.caption,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Progress bars
// ---------------------------------------------------------------------------

@Composable
private fun ProgressBars(
    count: Int,
    currentIndex: Int,
    currentProgress: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(count) { index ->
            val fill = when {
                index < currentIndex -> 1f
                index == currentIndex -> currentProgress
                else -> 0f
            }
            ProgressSegment(
                fill = fill,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProgressSegment(fill: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(3.dp)
            .clip(RoundedCornerShape(50)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.35f))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(fill)
                .fillMaxHeight()
                .background(Color.White)
        )
    }
}

// ---------------------------------------------------------------------------
// Header
// ---------------------------------------------------------------------------

@Composable
private fun StoryHeader(story: Story, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF5A5F)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "T",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "travels_daily",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "2h ago",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 11.sp,
            )
        }
        // Close button
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onClose() })
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Background layer
// ---------------------------------------------------------------------------

@Composable
private fun StoryBackgroundLayer(background: StoryBackground) {
    when (background) {
        is StoryBackground.Photo -> {
            val context = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(background.imageRes)
                    .crossfade(300)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        is StoryBackground.Gradient -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(background.colors)
                    )
            )
        }
    }
}