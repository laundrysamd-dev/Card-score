package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkVibrantPrimary,
    onPrimary = DarkVibrantOnPrimary,
    primaryContainer = DarkVibrantPrimaryContainer,
    onPrimaryContainer = DarkVibrantOnPrimaryContainer,
    secondary = DarkVibrantSecondary,
    onSecondary = DarkVibrantOnSecondary,
    secondaryContainer = DarkVibrantSecondaryContainer,
    onSecondaryContainer = DarkVibrantOnSecondaryContainer,
    tertiary = DarkVibrantTertiary,
    onTertiary = DarkVibrantOnTertiary,
    tertiaryContainer = DarkVibrantTertiaryContainer,
    onTertiaryContainer = DarkVibrantOnTertiaryContainer,
    background = DarkVibrantBackground,
    onBackground = DarkVibrantOnBackground,
    surface = DarkVibrantSurface,
    onSurface = DarkVibrantOnSurface
  )

private val LightColorScheme =
  lightColorScheme(
    primary = VibrantPrimary,
    onPrimary = VibrantOnPrimary,
    primaryContainer = VibrantPrimaryContainer,
    onPrimaryContainer = VibrantOnPrimaryContainer,
    secondary = VibrantSecondary,
    onSecondary = VibrantOnSecondary,
    secondaryContainer = VibrantSecondaryContainer,
    onSecondaryContainer = VibrantOnSecondaryContainer,
    tertiary = VibrantTertiary,
    onTertiary = VibrantOnTertiary,
    tertiaryContainer = VibrantTertiaryContainer,
    onTertiaryContainer = VibrantOnTertiaryContainer,
    background = VibrantBackground,
    onBackground = VibrantOnBackground,
    surface = VibrantSurface,
    onSurface = VibrantOnSurface,
    outline = VibrantOutline,
    outlineVariant = VibrantOutlineVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is disabled by default to enforce Vibrant Palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
