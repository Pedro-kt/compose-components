package com.yumedev.jetpackcomposecomponents.navigation

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yumedev.jetpackcomposecomponents.ui.screens.AuroraShaderScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.BarChartScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.LoadingButtonScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.CircularGaugeScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.CreditCardScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.ExpandableFabScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.HomeScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.OnboardingPagerScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.MusicPlayerScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.PinOtpScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.StoriesProgressScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.TravelCardScreen
import com.yumedev.jetpackcomposecomponents.ui.screens.WalletStackScreen

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
        composable(Screen.WalletStack.route) {
            WalletStackScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AuroraShader.route) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                AuroraShaderScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}