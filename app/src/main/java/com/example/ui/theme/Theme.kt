package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HighDensityColorScheme = lightColorScheme(
    primary = HdPurplePrimary,
    onPrimary = HdTextWhite,
    primaryContainer = HdPurpleContainer,
    onPrimaryContainer = HdPurpleOnContainer,
    secondary = HdJade,
    onSecondary = HdTextWhite,
    secondaryContainer = HdJadeLight,
    onSecondaryContainer = HdJadeText,
    tertiary = HdAzure,
    onTertiary = HdTextWhite,
    tertiaryContainer = HdAzureLight,
    onTertiaryContainer = HdAzure,
    background = HdBackground,
    onBackground = HdTextPrimary,
    surface = HdSurface,
    onSurface = HdTextPrimary,
    surfaceVariant = HdSurfaceVariant,
    onSurfaceVariant = HdTextSecondary,
    outline = HdBorder,
    outlineVariant = HdBorderStrong,
    error = HdCrimson,
    onError = HdTextWhite,
    errorContainer = HdCrimsonLight,
    onErrorContainer = HdCrimsonText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HighDensityColorScheme,
        typography = Typography,
        content = content
    )
}
