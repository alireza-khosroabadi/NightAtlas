package dev.nightatlas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val night =
    darkColorScheme(
        background = Color(0xFF101A23),
        surface = Color(0xFF101A23),
        surfaceContainer = Color(0xFF192832),
        surfaceVariant = Color(0xFF223640),
        primary = Color(0xFFF0C585),
        primaryContainer = Color(0xFF483822),
        onPrimary = Color(0xFF38290F),
        secondary = Color(0xFF9FD3CA),
        secondaryContainer = Color(0xFF304B4E),
        onSecondaryContainer = Color(0xFFC5EEE6),
        onBackground = Color(0xFFE4EAEB),
        onSurface = Color(0xFFE4EAEB),
    )

@Composable
fun NightTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = night, content = content)
}
