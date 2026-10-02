package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = OrangePrimaryDark,
  onPrimary = Color.Black,
  primaryContainer = OrangeContainerDark,
  onPrimaryContainer = OrangePrimaryDark,
  secondary = GreenSecondaryDark,
  onSecondary = Color.Black,
  tertiary = HoneyTertiaryDark,
  background = DarkBackground,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
  primary = OrangePrimary,
  onPrimary = Color.White,
  primaryContainer = OrangeContainer,
  onPrimaryContainer = Color(0xFF431C00),
  secondary = GreenSecondary,
  onSecondary = Color.White,
  tertiary = HoneyTertiary,
  background = CreamBackground,
  onBackground = TextPrimary,
  surface = CreamSurface,
  onSurface = TextPrimary,
  surfaceVariant = CreamSurfaceVariant,
  onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep warm custom school brand colors
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
