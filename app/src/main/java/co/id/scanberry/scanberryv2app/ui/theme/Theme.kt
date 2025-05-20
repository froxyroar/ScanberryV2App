package co.id.scanberry.scanberryv2app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ScanBerryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content:    @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme  = colors,
        typography   = ScanBerryTypography,
        shapes       = ScanBerryShapes,
        content      = content
    )
}
