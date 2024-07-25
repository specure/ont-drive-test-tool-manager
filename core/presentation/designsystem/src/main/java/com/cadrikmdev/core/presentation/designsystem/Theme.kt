package com.cadrikmdev.core.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = SignalTrackerManagerGreen,
    background = SignalTrackerManagerBlack,
    surface = SignalTrackerManagerDarkGray,
    secondary = SignalTrackerManagerWhite,
    tertiary = SignalTrackerManagerWhite,
    primaryContainer = SignalTrackerManagerGreen30,
    onPrimary = SignalTrackerManagerBlack,
    onBackground = SignalTrackerManagerWhite,
    onSurface = SignalTrackerManagerWhite,
    onSurfaceVariant = SignalTrackerManagerGray,
    error = SignalTrackerManagerDarkRed,
    errorContainer = SignalTrackerManagerDarkRed5,
)

@Composable
fun SignalTrackerManagerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}