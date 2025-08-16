package com.song.ply.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimen.dimenText20,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimen.dimenText17,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),

    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimen.dimenText15,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp,
        color = Color.Black
    ),

    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = Dimen.dimenText15,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),

    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = Dimen.dimenText15,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimen.dimenText13,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimen.dimenText12,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),

    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = Dimen.dimenText12,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),

    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimen.dimenText10,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    ),
)