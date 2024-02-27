package com.chartslib.charts.bar.models

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
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
    val horizontalLines: HorizontalLinesPattern,
    val verticalLines: VerticalLinesPattern,
)

sealed class HorizontalLinesPattern {
    data class EveryDp(
        val everyDp: Dp,
        val lines: List<HorizontalLine>,
        val specialLines: Map<Int, HorizontalLine>
    ) : HorizontalLinesPattern()

    data class FixedSize(
        val lines: List<HorizontalLine>
    ) : HorizontalLinesPattern()
}

sealed class VerticalLinesPattern {
    data class EveryDp(
        val everyDp: Dp,
        val lines: List<VerticalLine>,
        val specialLines: Map<Int, VerticalLine>
    ) : VerticalLinesPattern()

    data class FixedSize(
        val lines: List<VerticalLine>
    ) : VerticalLinesPattern()
}

data class HorizontalLine(
    val isShown: Boolean = true,
    val brush: Brush = SolidColor(Color.LightGray),
    val width: Dp = 1.dp,
    val label: String = "",
    val labelStyle: TextStyle = TextStyle(),
    val maxLabelLines: Int = 1,
    val alignment: HorizontalLineAlignment = HorizontalLineAlignment.CENTERED
)

data class VerticalLine(
    val isShown: Boolean = true,
    val brush: Brush = SolidColor(Color.LightGray),
    val width: Dp = 1.dp,
    val label: String = "",
    val labelStyle: TextStyle,
    val maxLabelLines: Int = 1,
    val alignment: VerticalLineAlignment = VerticalLineAlignment.CENTERED
)

enum class VerticalLineAlignment {
    AFTER_LINE,
    BEFORE_LINE,
    CENTERED
}

enum class HorizontalLineAlignment {
    ABOVE_LINE,
    UNDER_LINE,
    CENTERED
}