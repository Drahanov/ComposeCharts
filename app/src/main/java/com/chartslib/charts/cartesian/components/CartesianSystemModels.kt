package com.chartslib.charts.cartesian.components

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val UNSPECIFIED_HEIGHT = Float.MIN_VALUE

data class CartesianSystemPreferences(
    val horizontalLines: List<HorizontalLine>,
    val verticalLines: List<VerticalLine>,

    val verticalLabelsPreferences: LabelSizePreferences = LabelSizePreferences(
        style = TextStyle(
            fontSize = 10.sp
        ), maxWidth = 10.dp
    ),
    val horizontalLabelsPreferences: LabelSizePreferences = LabelSizePreferences(
        style = TextStyle(
            fontSize = 10.sp
        ), maxWidth = 30.dp
    ),

    val horizontalExtraPadding: Padding = Padding(),
    val verticalExtraPadding: Padding = Padding()
)

data class LabelSizePreferences(
    val style: TextStyle,
    val maxWidth: Dp,
    val maxLines: Int = 1,
    val maxHeight: Dp = UNSPECIFIED_HEIGHT.dp,
    val labelAndChartPadding: Dp = 5.dp,
)

data class HorizontalLine(
    val isLineVisible: Boolean = true,
    val lineBrush: Brush = SolidColor(Color.LightGray),
    val lineWidth: Dp = 1.dp,
    val alignment: HorizontalLineAlignment = HorizontalLineAlignment.CENTERED,
    val label: String = "",
    val lineStyle: LineStyle = LineStyle.DashedLine()
)

data class VerticalLine(
    val isLineVisible: Boolean = true,
    val lineBrush: Brush = SolidColor(Color.LightGray),
    val lineWidth: Dp = 1.dp,
    val alignment: VerticalLineAlignment = VerticalLineAlignment.CENTERED,
    val label: String = "",
    val lineStyle: LineStyle = LineStyle.DashedLine()
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

data class Padding(
    val top: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    val start: Dp = 0.dp,
    val end: Dp = 0.dp,
)

sealed class LineStyle {
    data object StrokeLine : LineStyle()
    data class DashedLine(val dashLength: Dp = 5.dp, val spaceLength: Dp = 5.dp) : LineStyle()
}