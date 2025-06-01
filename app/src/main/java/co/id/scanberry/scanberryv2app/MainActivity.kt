package co.id.scanberry.scanberryv2app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import co.id.scanberry.scanberryv2app.ui.theme.ScanBerryTheme
import co.id.scanberry.scanberryv2app.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsVm: SettingsViewModel by viewModels()
    private val isUserThemeReady = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        splashScreen.setKeepOnScreenCondition { !isUserThemeReady.value }

        lifecycleScope.launch {
            settingsVm.isDarkMode.first()
            isUserThemeReady.value = true
        }

        setContent {
            val isDark by settingsVm.isDarkMode.collectAsState()
            val lang by settingsVm.language.collectAsState()

            val locale = remember(lang) {
                if (lang == "en") Locale.ENGLISH else Locale("id", "ID")
            }

            val context = LocalContext.current
            val configuration = LocalConfiguration.current
            SideEffect {
                configuration.setLocale(locale)
                @Suppress("DEPRECATION")
                context.resources.updateConfiguration(
                    configuration,
                    context.resources.displayMetrics
                )
            }

            // Menggunakan AnimatedContent untuk animasi yang lebih kaya
            AnimatedContent(
                targetState = lang,
                label = "LanguageChangeAnimation",
                transitionSpec = {
                    // Animasi masuk: geser dari kanan ke tengah + muncul (fade in)
                    val enter = slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))

                    // Animasi keluar: geser dari tengah ke kiri + menghilang (fade out)
                    val exit = slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))

                    // Gabungkan keduanya
                    enter togetherWith exit
                }
            )    @Suppress("UNUSED_PARAMETER")
            { targetLang -> // `targetLang` adalah state `lang` yang baru
                ScanBerryTheme(darkTheme = isDark) {
                    NavigationGraph()
                }
            }
        }
    }
}