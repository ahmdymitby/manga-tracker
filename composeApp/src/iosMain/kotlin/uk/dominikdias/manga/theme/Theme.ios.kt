package uk.dominikdias.manga.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun isDynamicColorSupported(): Boolean {
    return false
}

@Composable
actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) DarkColorScheme else LightColorScheme
}