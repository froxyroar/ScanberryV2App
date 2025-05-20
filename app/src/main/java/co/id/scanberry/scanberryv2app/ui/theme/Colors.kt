package co.id.scanberry.scanberryv2app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val StrawberryRed       = Color(0xFFD32F2F)
val StrawberryRedDark   = Color(0xFFB71C1C)
val StrawberryPink      = Color(0xFFFF6F60)
val Grey100             = Color(0xFFF5F1ED)
val Grey900             = Color(0xFF222322)

val LightColorScheme = lightColorScheme(
    primary         = StrawberryRed,
    onPrimary       = Color.White,
    background      = Grey100,
    onBackground    = Grey900,
    surface         = Color.White,
    onSurface       = Grey900,
    secondary       = StrawberryPink
)

val DarkColorScheme = darkColorScheme(
    primary         = StrawberryPink,
    onPrimary       = Color.Black,
    background      = Grey900,
    onBackground    = Grey100,
    surface         = Grey900,
    onSurface       = Color.White,
    secondary       = StrawberryRed
)
