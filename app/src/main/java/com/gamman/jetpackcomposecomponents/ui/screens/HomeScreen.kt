package com.gamman.jetpackcomposecomponents.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamman.jetpackcomposecomponents.navigation.Screen

// ---------------------------------------------------------------------------
// Catalog - add one entry per new component
// ---------------------------------------------------------------------------

data class ComponentEntry(
    val screen: Screen,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val gradient: List<Color>,
)

private val catalog = listOf(
    ComponentEntry(
        screen = Screen.CreditCard,
        name = "Credit Card",
        description = "Flip 3D - maskable data",
        icon = Icons.Default.CreditCard,
        gradient = listOf(Color(0xFF1A1A2E), Color(0xFF533483)),
    ),
    ComponentEntry(
        screen = Screen.ExpandableFab,
        name = "Expandable FAB",
        description = "Staggered actions - scrim - rotation",
        icon = Icons.Default.Add,
        gradient = listOf(Color(0xFF0D1B2A), Color(0xFF1B4332)),
    ),
    ComponentEntry(
        screen = Screen.CircularGauge,
        name = "Circular Gauge",
        description = "Dashboard - animated arc - count-up",
        icon = Icons.Default.Speed,
        gradient = listOf(Color(0xFF1A0E2E), Color(0xFF0E2A3A)),
    ),
    ComponentEntry(
        screen = Screen.BarChart,
        name = "Bar Chart",
        description = "Stagger - tap tooltip - week/year",
        icon = Icons.Default.BarChart,
        gradient = listOf(Color(0xFF0E1A2E), Color(0xFF2E1A0E)),
    ),
    ComponentEntry(
        screen = Screen.LoadingButton,
        name = "Loading Button",
        description = "Morphing - spinner - success/error",
        icon = Icons.Default.SmartButton,
        gradient = listOf(Color(0xFF1A0E2E), Color(0xFF2E0E1A)),
    ),
    ComponentEntry(
        screen = Screen.OnboardingPager,
        name = "Onboarding Pager",
        description = "Pager - animated indicators - transitions",
        icon = Icons.Default.ViewCarousel,
        gradient = listOf(Color(0xFF0D0D1A), Color(0xFF1A1A4E)),
    ),
    ComponentEntry(
        screen = Screen.TravelCard,
        name = "Travel Card",
        description = "Hero image - shared element - detail expand",
        icon = Icons.Default.Explore,
        gradient = listOf(Color(0xFF0D1B2A), Color(0xFF0A3D3D)),
    ),
    ComponentEntry(
        screen = Screen.StoriesProgress,
        name = "Stories Progress",
        description = "Segmented timer - tap navigate - hold pause",
        icon = Icons.Default.Slideshow,
        gradient = listOf(Color(0xFF1A0A1A), Color(0xFF3A1A3A)),
    ),
    ComponentEntry(
        screen = Screen.PinOtp,
        name = "PIN / OTP Input",
        description = "Pop entry - shake error - checkmark success",
        icon = Icons.Default.Password,
        gradient = listOf(Color(0xFF0E0E2E), Color(0xFF2E0E3A)),
    ),
    ComponentEntry(
        screen = Screen.MusicPlayer,
        name = "Music Player",
        description = "Vinyl rotation - seek bar - animated controls",
        icon = Icons.Default.MusicNote,
        gradient = listOf(Color(0xFF1A0A2E), Color(0xFF0E1A3A)),
    ),
)

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@Composable
fun HomeScreen(onNavigate: (Screen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E0E)),
    ) {
        // Header
        Column(modifier = Modifier.padding(start = 24.dp, top = 56.dp, bottom = 8.dp)) {
            Text(
                text = "Components",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(catalog) { entry ->
                ComponentCard(entry = entry, onClick = { onNavigate(entry.screen) })
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Card
// ---------------------------------------------------------------------------

@Composable
private fun ComponentCard(entry: ComponentEntry, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(entry.gradient))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(16.dp),
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = entry.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = entry.name,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 17.sp,
            )
            Text(
                text = entry.description,
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 10.sp,
                lineHeight = 14.sp,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0E0E0E)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(onNavigate = {})
}