package com.chartslib.charts.bar.models

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @param steps how many steps have this axis (x or y).
 * @param shouldShow is lines visible.
 * @param brush lines style.
 * @param width line width
 */

const val UNSPECIFIED_HEIGHT = Float.MIN_VALUE

data class MeasuringLines(
    val shouldShow: Boolean = true,
    val brush: Brush = SolidColor(Color.LightGray),
    val steps: Int,
    val width: Dp = 1.dp
)

data class UtilityLines(
    val horizontalLines: List<HorizontalLine>,
    val verticalLines: List<VerticalLine>,
    val verticalLabelsPreferences: LabelSizePreferences = LabelSizePreferences(style = TextStyle(fontSize = 10.sp), maxWidth = 10.dp),
    val horizontalLabelsPreferences: LabelSizePreferences = LabelSizePreferences(style = TextStyle(fontSize = 10.sp), maxWidth = 30.dp)
)

data class LabelSizePreferences (
    val style: TextStyle,
    val maxWidth: Dp,
    val maxLines: Int = 1,
    val maxHeight: Dp = UNSPECIFIED_HEIGHT.dp,
    val labelAndChartPadding: Dp = 5.dp
)

//sealed class HorizontalLinesPattern {
//    data class EveryDp(
//        val everyDp: Dp,
//        val lineDefault: HorizontalLine = HorizontalLine(),
//
//        val firstLine: HorizontalLine = lineDefault,
//        val lastLine: HorizontalLine = lineDefault,
//        val lines: (Int) -> HorizontalLine? = { lineDefault },
//    ) : HorizontalLinesPattern()
//
//    data class FixedSize(
//        val lines: List<HorizontalLine>
//    ) : HorizontalLinesPattern()
//}

//sealed class VerticalLinesPattern {
//    data class EveryDp(
//        val everyDp: Dp,
//        val lineDefault: VerticalLine = VerticalLine(),
//
//        val firstLine: VerticalLine = lineDefault,
//        val lastLine: VerticalLine = lineDefault,
//        val lines: (Int) -> VerticalLine? = { lineDefault },
//    ) : VerticalLinesPattern()
//
//    data class FixedSize(
//        val lines: List<VerticalLine>
//    ) : VerticalLinesPattern()
//}

data class HorizontalLine(
    val isLineVisible: Boolean = true,
    val lineBrush: Brush = SolidColor(Color.LightGray),
    val lineWidth: Dp = 1.dp,
    val alignment: HorizontalLineAlignment = HorizontalLineAlignment.CENTERED,
    val label: String = "",
)

data class VerticalLine(
    val isLineVisible: Boolean = true,
    val lineBrush: Brush = SolidColor(Color.LightGray),
    val lineWidth: Dp = 1.dp,
    val alignment: VerticalLineAlignment = VerticalLineAlignment.CENTERED,
    val label: String = "",
)


enum class HorizontalLineAlignment {
    ABOVE_LINE,
    UNDER_LINE,
    CENTERED
}

enum class VerticalLineAlignment {
    BEFORE_LINE,
    AFTER_LINE,
    CENTERED
}