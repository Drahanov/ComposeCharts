package com.chartslib.charts.bar.models

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @param steps how many steps have this axis (x or y).
 * @param shouldShow is lines visible.
 * @param brush lines style.
 * @param width line width
 */
data class MeasuringLines(
    val shouldShow: Boolean = true,
    val brush: Brush = SolidColor(Color.LightGray),
    val steps: Int,
    val width: Dp = 1.dp
)

data class UtilityLines(

)

sealed class UtilityLinesPattern {
    data class EveryDp(val everyDp: Dp, ): UtilityLinesPattern()
}

data class HorizontalLine(
    val isShown: Boolean = true,
    val brush: Brush = SolidColor(Color.LightGray),
    val width: Dp = 1.dp,
    val label: String = "",
    val alignment: HorizontalLineAlignment = HorizontalLineAlignment.CENTERED
)

enum class HorizontalLineAlignment {
    ABOVE_LINE,
    UNDER_LINE,
    CENTERED
}

data class VerticalLine(
    val isShown: Boolean = true,
    val brush: Brush = SolidColor(Color.LightGray),
    val label: String = "",
    val width: Dp = 1.dp,
    val alignment: Float = VerticalLineAlignment.CENTERED.value
)

enum class VerticalLineAlignment(val value: Float) {
    AFTER_LINE(1f),
    BEFORE_LINE(2f),
    CENTERED(3f)
}