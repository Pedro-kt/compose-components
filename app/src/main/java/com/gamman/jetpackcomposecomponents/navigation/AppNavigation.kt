package com.gamman.jetpackcomposecomponents.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gamman.jetpackcomposecomponents.ui.screens.BarChartScreen
import com.gamman.jetpackcomposecomponents.ui.screens.LoadingButtonScreen
import com.gamman.jetpackcomposecomponents.ui.screens.CircularGaugeScreen
import com.gamman.jetpackcomposecomponents.ui.screens.CreditCardScreen
import com.gamman.jetpackcomposecomponents.ui.screens.ExpandableFabScreen
import com.gamman.jetpackcomposecomponents.ui.screens.HomeScreen
import com.gamman.jetpackcomposecomponents.ui.screens.OnboardingPagerScreen
import com.gamman.jetpackcomposecomponents.ui.screens.MusicPlayerScreen
import com.gamman.jetpackcomposecomponents.ui.screens.PinOtpScreen
import com.gamman.jetpackcomposecomponents.ui.screens.StoriesProgressScreen
import com.gamman.jetpackcomposecomponents.ui.screens.TravelCardScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigate = { navController.navigate(it.route) })
        }
        composable(Screen.CreditCard.route) {
            CreditCardScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.ExpandableFab.route) {
            ExpandableFabScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.CircularGauge.route) {
            CircularGaugeScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.BarChart.route) {
            BarChartScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.LoadingButton.route) {
            LoadingButtonScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.OnboardingPager.route) {
            OnboardingPagerScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.TravelCard.route) {
            TravelCardScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.StoriesProgress.route) {
            StoriesProgressScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.PinOtp.route) {
            PinOtpScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.MusicPlayer.route) {
            MusicPlayerScreen(onBack = { navController.popBackStack() })
        }
    }
}