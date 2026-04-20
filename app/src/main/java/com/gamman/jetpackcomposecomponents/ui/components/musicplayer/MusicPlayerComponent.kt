package com.gamman.jetpackcomposecomponents.ui.components.musicplayer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Model
// ---------------------------------------------------------------------------

private sealed class PlayerState {
    data object Idle : PlayerState()
    data object Playing : PlayerState()
    data object Paused : PlayerState()
}

private data class Track(
    val title: String,
    val artist: String,
    val durationSec: Int,
    val colorA: Color,
    val colorB: Color,
)

private val Playlist = listOf(
    Track("Blinding Lights",  "The Weeknd",   200, Color(0xFFE84393), Color(0xFF6C5CE7)),
    Track("As It Was",        "Harry Styles", 167, Color(0xFF6C5CE7), Color(0xFFA29BFE)),
    Track("Anti-Hero",        "Taylor Swift", 200, Color(0xFF00B894), Color(0xFF0984E3)),
    Track("Flowers",          "Miley Cyrus",  197, Color(0xFFFDCB6E), Color(0xFFE17055)),
    Track("Stay With Me",     "Sam Smith",    172, Color(0xFFFF6B6B), Color(0xFF4ECDC4)),
)

// ---------------------------------------------------------------------------
// Root
// ---------------------------------------------------------------------------

@Composable
fun MusicPlayerComponent(onNavigateBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf<PlayerState>(PlayerState.Idle) }
    var trackIndex by remember { mutableIntStateOf(0) }
    var seekVersion by remember { mutableIntStateOf(0) }
    val track = Playlist[trackIndex]

    val progress = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    val glowColor by animateColorAsState(
        targetValue = track.colorA.copy(alpha = 0.55f),
        animationSpec = tween(900),
        label = "glow",
    )

    // Progress ticker — restarts on state change, track change, or seek
    LaunchedEffect(state, trackIndex, seekVersion) {
        if (state !is PlayerState.Playing) return@LaunchedEffect
        val durationMs = ((1f - progress.value) * track.durationSec * 1000).toInt()
        if (durationMs <= 0) return@LaunchedEffect
        progress.animateTo(1f, tween(durationMillis = durationMs, easing = LinearEasing))
        if (progress.value >= 0.999f) {
            progress.snapTo(0f)
            trackIndex = (trackIndex + 1) % Playlist.size
        }
    }

    // Album rotation loop — cancelled automatically when state leaves Playing
    LaunchedEffect(state) {
        if (state !is PlayerState.Playing) return@LaunchedEffect
        while (true) {
            rotation.animateTo(rotation.value + 360f, tween(8000, easing = LinearEasing))
            rotation.snapTo(rotation.value % 360f)
        }
    }

    fun play() { state = PlayerState.Playing }
    fun pause() { state = PlayerState.Paused }
    fun prev() {
        scope.launch {
            progress.snapTo(0f)
            trackIndex = (trackIndex - 1 + Playlist.size) % Playlist.size
        }
    }
    fun next() {
        scope.launch {
            progress.snapTo(0f)
            trackIndex = (trackIndex + 1) % Playlist.size
        }
    }
    fun seekTo(p: Float) {
        scope.launch {
            progress.snapTo(p.coerceIn(0f, 1f))
            if (state is PlayerState.Playing) seekVersion++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
    ) {

        // Back button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 52.dp)
                .size(40.dp)
                .clip(CircleShape)
                .tapClickable(onNavigateBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-30).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PlayerCard(
                track = track,
                state = state,
                progress = progress.value,
                rotation = rotation.value,
                onPlay = ::play,
                onPause = ::pause,
                onPrev = ::prev,
                onNext = ::next,
                onSeek = ::seekTo,
            )

            Spacer(Modifier.height(22.dp))

            TrackDots(
                currentIndex = trackIndex,
                count = Playlist.size,
                accentColor = track.colorA,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Player card (the widget)
// ---------------------------------------------------------------------------

@Composable
private fun PlayerCard(
    track: Track,
    state: PlayerState,
    progress: Float,
    rotation: Float,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Float) -> Unit,
) {
    val cardColorA by animateColorAsState(
        targetValue = track.colorA.copy(alpha = 0.22f),
        animationSpec = tween(900),
        label = "cardA",
    )
    val cardColorB by animateColorAsState(
        targetValue = track.colorB.copy(alpha = 0.14f),
        animationSpec = tween(900),
        label = "cardB",
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF0E0E1A))
            .background(Brush.linearGradient(listOf(cardColorA, cardColorB)))
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AlbumArt(track = track, rotation = rotation)

            Spacer(Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(),
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = track.artist,
                    color = Color.White.copy(alpha = 0.60f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(),
                )
                Spacer(Modifier.height(14.dp))
                CompactSeekBar(
                    progress = progress,
                    accentColor = track.colorA,
                    onSeek = onSeek,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CompactControls(
                state = state,
                accentColor = track.colorA,
                onPlay = onPlay,
                onPause = onPause,
                onPrev = onPrev,
                onNext = onNext,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Album art (vinyl disc)
// ---------------------------------------------------------------------------

@Composable
private fun AlbumArt(track: Track, rotation: Float) {
    Box(
        modifier = Modifier
            .size(78.dp)
            .rotate(rotation)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(track.colorA, track.colorB))),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.22f)),
        )
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.58f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Seek bar (compact, no time labels)
// ---------------------------------------------------------------------------

@Composable
private fun CompactSeekBar(
    progress: Float,
    accentColor: Color,
    onSeek: (Float) -> Unit,
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }
    val displayProgress = if (isDragging) dragProgress else progress

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp),
    ) {
        val trackW = constraints.maxWidth.toFloat()

        Canvas(modifier = Modifier.fillMaxSize()) {
            val cy = size.height / 2
            val trackH = 3.dp.toPx()
            val thumbR = 6.dp.toPx()
            val filled = trackW * displayProgress

            drawRoundRect(
                color = Color.White.copy(alpha = 0.20f),
                topLeft = Offset(0f, cy - trackH / 2),
                size = Size(trackW, trackH),
                cornerRadius = CornerRadius(trackH / 2),
            )
            if (displayProgress > 0f) {
                drawRoundRect(
                    color = accentColor,
                    topLeft = Offset(0f, cy - trackH / 2),
                    size = Size(filled, trackH),
                    cornerRadius = CornerRadius(trackH / 2),
                )
            }
            drawCircle(color = Color.White, radius = thumbR, center = Offset(filled, cy))
        }

        // Transparent touch layer — handles tap-to-seek and drag-to-seek
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(trackW) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        dragProgress = (down.position.x / trackW).coerceIn(0f, 1f)
                        isDragging = true
                        var tracking = true
                        while (tracking) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            change.consume()
                            dragProgress = (change.position.x / trackW).coerceIn(0f, 1f)
                            tracking = change.pressed
                        }
                        isDragging = false
                        onSeek(dragProgress)
                    }
                },
        )
    }
}

// ---------------------------------------------------------------------------
// Controls (compact row)
// ---------------------------------------------------------------------------

@Composable
private fun CompactControls(
    state: PlayerState,
    accentColor: Color,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ControlButton(onClick = onPrev) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(26.dp),
            )
        }

        ControlButton(
            onClick = { if (state is PlayerState.Playing) onPause() else onPlay() },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accentColor),
        ) {
            AnimatedContent(
                targetState = state is PlayerState.Playing,
                transitionSpec = {
                    (fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.55f)) togetherWith
                        (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 1.5f))
                },
                label = "play_pause_icon",
            ) { isPlaying ->
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        ControlButton(onClick = onNext) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(26.dp),
            )
        }
    }
}

@Composable
private fun ControlButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    // rememberUpdatedState ensures pointerInput(Unit) always calls the latest onClick
    // without this, the lambda captured at first composition becomes stale after state changes
    val currentOnClick by rememberUpdatedState(onClick)
    Box(
        modifier = Modifier
            .scale(scale.value)
            .then(modifier)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch { scale.animateTo(0.82f, tween(60)) }
                        val released = tryAwaitRelease()
                        scope.launch {
                            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                        if (released) currentOnClick()
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

// ---------------------------------------------------------------------------
// Track indicator dots
// ---------------------------------------------------------------------------

@Composable
private fun TrackDots(currentIndex: Int, count: Int, accentColor: Color) {
    val accentAnimated by animateColorAsState(
        targetValue = accentColor,
        animationSpec = tween(600),
        label = "dot_color",
    )
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { i ->
            val isActive = i == currentIndex
            val dotWidth by animateDpAsState(
                targetValue = if (isActive) 20.dp else 6.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "dot_w_$i",
            )
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(dotWidth)
                    .clip(CircleShape)
                    .background(
                        if (isActive) accentAnimated else Color.White.copy(alpha = 0.28f),
                    ),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun Modifier.tapClickable(onClick: () -> Unit): Modifier =
    pointerInput(Unit) { detectTapGestures(onTap = { onClick() }) }

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun MusicPlayerPreview() {
    MusicPlayerComponent(onNavigateBack = {})
}