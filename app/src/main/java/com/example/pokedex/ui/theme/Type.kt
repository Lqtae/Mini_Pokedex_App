package com.example.pokedex.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.pokedex.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold),
)

// Apply Poppins to every Material 3 text style.
private val default = Typography()

val Typography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = Poppins),
    displayMedium = default.displayMedium.copy(fontFamily = Poppins),
    displaySmall = default.displaySmall.copy(fontFamily = Poppins),
    headlineLarge = default.headlineLarge.copy(fontFamily = Poppins),
    headlineMedium = default.headlineMedium.copy(fontFamily = Poppins),
    headlineSmall = default.headlineSmall.copy(fontFamily = Poppins),
    titleLarge = default.titleLarge.copy(fontFamily = Poppins),
    titleMedium = default.titleMedium.copy(fontFamily = Poppins),
    titleSmall = default.titleSmall.copy(fontFamily = Poppins),
    bodyLarge = default.bodyLarge.copy(fontFamily = Poppins),
    bodyMedium = default.bodyMedium.copy(fontFamily = Poppins),
    bodySmall = default.bodySmall.copy(fontFamily = Poppins),
    labelLarge = default.labelLarge.copy(fontFamily = Poppins),
    labelMedium = default.labelMedium.copy(fontFamily = Poppins),
    labelSmall = default.labelSmall.copy(fontFamily = Poppins),
)
