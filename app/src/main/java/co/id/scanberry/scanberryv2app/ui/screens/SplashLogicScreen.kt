package co.id.scanberry.scanberryv2app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import co.id.scanberry.scanberryv2app.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashLogicScreen(nav: NavController, settingsVm: SettingsViewModel) {
    val isFirstRun by settingsVm.firstRun.collectAsState()

    LaunchedEffect(isFirstRun) {
        delay(1200)
        nav.navigate(if (isFirstRun) "onboarding" else "home") {
            popUpTo("splash") { inclusive = true }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
