package co.id.scanberry.scanberryv2app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import co.id.scanberry.scanberryv2app.NavigationGraph
import co.id.scanberry.scanberryv2app.ui.theme.ScanBerryTheme
import co.id.scanberry.scanberryv2app.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

// CompositionLocal for app-wide locale
val LocalAppLocale = compositionLocalOf<Locale> { Locale.getDefault() }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsVm: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // 1) Read your state inside a @Composable scope
            val lang by settingsVm.language.collectAsState()
            val isDark by settingsVm.isDarkMode.collectAsState()

            // 2) Material Locale object
            val locale = remember(lang) {
                if (lang == "en") Locale.ENGLISH else Locale("id", "ID")
            }

            // 3) Read these ONCE in composable scope:
            val context = LocalContext.current               // :contentReference[oaicite:0]{index=0}
            val configuration = LocalConfiguration.current   // :contentReference[oaicite:1]{index=1}

            CompositionLocalProvider(LocalAppLocale provides locale) {
                // 4) Use SideEffect so that this runs after composition,
                //    but refer only to pre-read vals (no LocalContext.current calls inside!)
                SideEffect {
                    configuration.setLocale(locale)
                    context.resources.updateConfiguration(
                        configuration,
                        context.resources.displayMetrics
                    )
                }

                // 5) Apply theme & navigation
                ScanBerryTheme(darkTheme = isDark) {
                    NavigationGraph()
                }
            }
        }
    }
}
