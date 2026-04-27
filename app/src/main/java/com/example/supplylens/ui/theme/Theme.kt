package com.supplylens.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary        = CyanAccent,
    secondary      = OrangeAccent,
    background     = NavyBg,
    surface        = SurfaceCard,
    onBackground   = TextPrimary,
    onSurface      = TextPrimary,
    error          = RedDanger
)

@Composable
fun SupplyLensTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}