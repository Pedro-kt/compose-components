package com.yumedev.jetpackcomposecomponents.ui.components.onboardingpager

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Model
// ---------------------------------------------------------------------------

data class OnboardingPage(
    val title: String,
    val subtitle: String,
)

internal val defaultPages = listOf(
    OnboardingPage(
        title = "Welcome",
        subtitle = "A smarter way to manage\nyour everyday tasks.",
    ),
    OnboardingPage(
        title = "Explore",
        subtitle = "Find what you need,\nexactly when you need it.",
    ),
    OnboardingPage(
        title = "Connect",
        subtitle = "Stay close to the people\nthat matter most.",
    ),
    OnboardingPage(
        title = "You're ready",
        subtitle = "Let's get started.",
    ),
)

private val Background = Color(0xFF0E0E0E)

// ---------------------------------------------------------------------------
// Public composable
// ---------------------------------------------------------------------------

@Composable
fun OnboardingPagerComponent(
    modifier: Modifier = Modifier,
    pages: List<OnboardingPage> = defaultPages,
    onFinish: () -> Unit = {},
) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { pageIndex ->
            PageContent(
                page = pages[pageIndex],
                pageIndex = pageIndex,
                pagerState = pagerState,
            )
        }

        // Skip
        val skipAlpha by animateFloatAsState(
            targetValue = if (isLastPage) 0f else 1f,
            animationSpec = tween(300),
            label = "skip_alpha",
        )
        TextButton(
            onClick = onFinish,
            enabled = !isLastPage,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 52.dp, end = 8.dp)
                .alpha(skipAlpha),
        ) {
            Text(
                text = "Skip",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp)
                .padding(bottom = 52.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            PageIndicators(pagerState = pagerState, pageCount = pages.size)

            Button(
                onClick = {
                    if (!isLastPage) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ),
                shape = RoundedCornerShape(14.dp),
            ) {
                AnimatedContent(
                    targetState = isLastPage,
                    transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(180)) },
                    label = "button_text",
                ) { last ->
                    Text(
                        text = if (last) "Get Started" else "Next",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Page content
// ---------------------------------------------------------------------------

@Composable
private fun PageContent(
    page: OnboardingPage,
    pageIndex: Int,
    pagerState: PagerState,
) {
    val isCurrentPage = pagerState.currentPage == pageIndex

    val contentAlpha by animateFloatAsState(
        targetValue = if (isCurrentPage) 1f else 0f,
        animationSpec = tween(360),
        label = "content_alpha_$pageIndex",
    )
    val contentOffsetY by animateFloatAsState(
        targetValue = if (isCurrentPage) 0f else 32f,
        animationSpec = tween(360),
        label = "content_offset_$pageIndex",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
    ) {
        // Faint page number — decorative background text
        Text(
            text = "0${pageIndex + 1}",
            fontSize = 160.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.03f),
            modifier = Modifier.align(Alignment.TopStart).padding(top = 80.dp),
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 140.dp)
                .graphicsLayer {
                    alpha = contentAlpha
                    translationY = contentOffsetY
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = page.title,
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 42.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = page.subtitle,
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 15.sp,
                lineHeight = 23.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Indicators
// ---------------------------------------------------------------------------

@Composable
private fun PageIndicators(pagerState: PagerState, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isActive = pagerState.currentPage == index
            val width by animateDpAsState(
                targetValue = if (isActive) 24.dp else 6.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "dot_width_$index",
            )
            val alpha by animateFloatAsState(
                targetValue = if (isActive) 1f else 0.25f,
                animationSpec = tween(300),
                label = "dot_alpha_$index",
            )
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(width)
                    .alpha(alpha)
                    .background(Color.White, RoundedCornerShape(3.dp)),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF0E0E0E)
@Composable
private fun OnboardingPagerPreview() {
    OnboardingPagerComponent()
}