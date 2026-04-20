# Jetpack Compose Components

A portfolio of custom Android UI components built with Jetpack Compose, focused on animations, gestures, and polished interactions.

## Preview

<table>
  <tr>
    <td align="center"><img src="assets/animation_1.gif" alt="Loading Button" height="320"/><br/><sub>Loading Button</sub></td>
    <td align="center"><img src="assets/animation_2.gif" alt="Bar Chart" height="320"/><br/><sub>Bar Chart</sub></td>
    <td align="center"><img src="assets/animation_3.gif" alt="Circular Gauge" height="320"/><br/><sub>Circular Gauge</sub></td>
    <td align="center"><img src="assets/animation_4.gif" alt="Expandable FAB" height="320"/><br/><sub>Expandable FAB</sub></td>
    <td align="center"><img src="assets/animation_5.gif" alt="Credit Card" height="320"/><br/><sub>Credit Card</sub></td>
  </tr>
</table>

## Components

### Credit Card
3D flip animation between front and back faces. Features a Canvas-drawn chip and contactless icon, gradient backgrounds, and a toggle to mask sensitive data (card number, holder name, expiry, CVV).

### Expandable FAB
A floating action button that expands into a set of labeled mini-actions. Each item animates in with a staggered alpha and slide, the main icon rotates on open, and a scrim overlay closes everything on tap.

### Circular Gauge
A 270-degree arc gauge drawn entirely on Canvas. Displays an animated count-up value, tick marks, a sweep gradient, and color interpolation from cyan to amber to red based on the current value.

### Bar Chart
Animated bar chart with staggered entry per bar, top-rounded corners via `Path`, subtle grid lines, and tap-to-select with a tooltip showing the exact value. Supports switching between weekly and yearly datasets.

### Loading Button
A button that morphs from a full-width rectangle to a circle on tap. Goes through idle -> loading (animated spinner with variable sweep) -> success / error states, with distinct `AnimatedContent` transitions per step and press-scale feedback.

### Onboarding Pager
A minimalist onboarding flow built with `HorizontalPager`. Each page animates its title and subtitle in with a fade + slide-up on entry. The page indicators morph between dot and pill with a spring animation, and the primary button crossfades between "Next" and "Get Started" on the last page. The skip button fades out when there is nothing left to skip.

### Stories Progress Bar
A full-screen stories viewer inspired by Instagram Stories. Five Japan travel photos cycle automatically with a segmented progress bar at the top. Tap the right half to advance, tap the left half to go back, and hold to pause the timer. All images are preloaded on entry via Coil to eliminate blank frames between transitions. The crossfade prevents hard cuts between stories.

### Travel Card
A travel destination card inspired by Airbnb-style UIs. Tapping the card triggers a `SharedTransitionLayout` hero animation where the image transitions to a full-screen detail view. The detail content slides up from the bottom via `AnimatedVisibility` with `slideInVertically`, creating a bottom sheet feel. Includes a save toggle, rating display, amenity chips, and a Book Now CTA.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navigation:** Navigation Compose 2.8.9
- **Animations:** `animateFloatAsState`, `animateDpAsState`, `animateColorAsState`, `AnimatedContent`, `AnimatedVisibility`, `SharedTransitionLayout`, `Animatable`, `rememberInfiniteTransition`, `HorizontalPager`
- **Image loading:** Coil 3 (`AsyncImage`, preload, crossfade)
- **Drawing:** Compose `Canvas`, `Path`, `DrawScope`
- **Min SDK:** 24

## Project Structure

```
app/src/main/java/com/gamman/jetpackcomposecomponents/
├── navigation/
│   ├── Screen.kt           # Sealed class with all routes
│   └── AppNavigation.kt    # NavHost setup
├── ui/
│   ├── components/
│   │   ├── creditcard/
│   │   ├── expandablefab/
│   │   ├── circulargauge/
│   │   ├── barchart/
│   │   ├── loadingbutton/
│   │   ├── onboardingpager/
│   │   ├── storiesprogress/
│   │   └── travelcard/
│   └── screens/            # One screen wrapper per component
└── MainActivity.kt
```

## Adding a New Component

1. Create `ui/components/<name>/YourComponent.kt`
2. Create `ui/screens/YourScreen.kt`
3. Add a `data object` to `navigation/Screen.kt`
4. Register a `composable {}` block in `AppNavigation.kt`
5. Add a `ComponentEntry` to the catalog in `HomeScreen.kt`