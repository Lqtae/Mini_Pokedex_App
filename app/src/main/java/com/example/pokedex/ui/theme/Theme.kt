package com.example.pokedex.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val PokedexColorScheme = lightColorScheme(
    primary = PokeRed,
    onPrimary = Color.White,
    secondary = PokeBlue,
    onSecondary = Color.White,
    tertiary = PokeYellow,
    onTertiary = Color(0xFF1D1D1F),
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
)

@Composable
fun PokedexTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PokedexColorScheme,
        typography = Typography
    ) {
        // Make bare Text() (fontSize only, no style) inherit Poppins too.
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = Poppins),
            content = content
        )
    }
}
