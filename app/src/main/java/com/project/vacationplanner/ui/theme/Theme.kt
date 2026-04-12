package com.project.vacationplanner.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkColors = darkColorScheme(
    primary = AccentBlue,
    onPrimary = White,
    primaryContainer = CardDark,
    onPrimaryContainer = White,
    background = Black,
    onBackground = White,
    surface = BlackSurface,
    onSurface = White,
    surfaceVariant = CardDark,
    onSurfaceVariant = WhiteSecondary,
    outline = DividerColor,
    error = AccentRed,
    onError = White,
)

val VacationShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun VacationPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = VacationTypography,
        shapes = VacationShapes,
        content = content,
    )
}