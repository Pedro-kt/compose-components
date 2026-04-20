package com.gamman.jetpackcomposecomponents.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object CreditCard : Screen("credit_card")
    data object ExpandableFab : Screen("expandable_fab")
    data object CircularGauge : Screen("circular_gauge")
    data object BarChart : Screen("bar_chart")
    data object LoadingButton : Screen("loading_button")
    data object OnboardingPager : Screen("onboarding_pager")
    data object TravelCard : Screen("travel_card")
    data object StoriesProgress : Screen("stories_progress")
    data object PinOtp : Screen("pin_otp")
    data object MusicPlayer : Screen("music_player")
}