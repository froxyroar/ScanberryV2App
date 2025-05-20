package co.id.scanberry.scanberryv2app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import co.id.scanberry.scanberryv2app.ui.screens.GambarScreen
import co.id.scanberry.scanberryv2app.ui.screens.HomeScreen
import co.id.scanberry.scanberryv2app.ui.screens.OnboardingScreen
import co.id.scanberry.scanberryv2app.ui.screens.RealtimeScreen
import co.id.scanberry.scanberryv2app.viewmodel.SettingsViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import co.id.scanberry.scanberryv2app.ui.screens.SplashLogicScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationGraph() {
    val nav = rememberNavController()
    val settingsVm: SettingsViewModel = hiltViewModel()

    AnimatedNavHost(
        navController = nav,
        startDestination = "splash",
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
        popEnterTransition = { fadeIn(animationSpec = tween(500)) },
        popExitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        composable("splash") {
            SplashLogicScreen(nav, settingsVm)
        }
        composable("onboarding") {
            OnboardingScreen(nav)
        }
        composable("home") {
            HomeScreen(nav = nav, isDarkMode = settingsVm.isDarkMode.collectAsState().value)
        }
        composable("gambar") {
            GambarScreen(nav)
        }
        composable("realtime") {
            RealtimeScreen(nav)
        }
    }
}