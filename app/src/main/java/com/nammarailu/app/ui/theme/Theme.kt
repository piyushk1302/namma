package com.nammarailu.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Brand Palette ─────────────────────────────────────────────────────────────
val NavyBlue       = Color(0xFF0D2B55)
val NavyLight      = Color(0xFF1A3F73)
val NavyCard       = Color(0xFF162E50)
val BrightYellow   = Color(0xFFFFC107)
val YellowSoft     = Color(0xFFFFD54F)

val EngineGray     = Color(0xFF424242)
val GeneralGreen   = Color(0xFF2E7D32)
val GeneralLight   = Color(0xFFE8F5E9)
val SleeperBlue    = Color(0xFF1565C0)
val SleeperLight   = Color(0xFFE3F2FD)
val LadiesRed      = Color(0xFFC62828)
val LadiesLight    = Color(0xFFFFEBEE)
val PantryOrange   = Color(0xFFE65100)

val SurfaceLight   = Color(0xFFF4F4F2)
val CardWhite      = Color(0xFFFFFFFF)
val TextDark       = Color(0xFF0D2B55)
val TextMuted      = Color(0xFF757575)
val DividerColor   = Color(0xFFF0F0F0)

val SuccessGreen   = Color(0xFF2E7D32)
val SuccessLight   = Color(0xFFE8F5E9)
val ErrorRed       = Color(0xFFC62828)
val ErrorLight     = Color(0xFFFFEBEE)
val WarningAmber   = Color(0xFFF57F17)

private val ColorScheme = darkColorScheme(
    primary        = BrightYellow,
    onPrimary      = NavyBlue,
    secondary      = NavyLight,
    onSecondary    = Color.White,
    background     = NavyBlue,
    onBackground   = Color.White,
    surface        = NavyLight,
    onSurface      = Color.White,
    surfaceVariant = NavyCard,
)

@Composable
fun NammaRailuTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColorScheme, content = content)
}
