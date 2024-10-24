package com.example.notez.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable



private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    surface = darkSurf,
    error = errorwhite,
    onPrimary = onPrimary,
    onSecondary = onPrimary,
    onTertiary = onPrimary,
    onSurface = ondarkSurf,
    onError = onerrorWhite,
    background = darkBackground,
    onBackground = ondarkSurf

)
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    surface = lightSurf,
    error = errorJet,
    onPrimary = onPrimary,
    onSecondary = onPrimary,
    onTertiary = onPrimary,
    onSurface = onlightSurf,
    onError = onerrorJet,
    background = lightBackground,
    onBackground = onlightSurf


)

@Composable
fun NotezTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // You can customize this if needed
        content =  {
            Surface(
                color = colorScheme.background, // Set the background color
                content = content
            )
        }
    )
}