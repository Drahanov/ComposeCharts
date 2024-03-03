package com.chartslib.charts.cartesian.components

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val UNSPECIFIED_HEIGHT = Float.MIN_VALUE

/**
 * [CartesianSystemPreferences] prefs cartesian system drawing.
 *
 * @param horizontalLines horizontal lines. Lines will divide the entire width of the graph among themselves.
 *
 * @param verticalLines vertical lines. Lines will divide the entire height of the graph among themselves.
 * @param verticalLabelsPreferences vertical labels prefs. Note! vertical labels linked to horizontal lines since they displayed on vertical axis.
 * @param horizontalLabelsPreferences horizontal labels prefs. Note! horizontal labels linked to vertical lines since they displayed on horizontal axis.
 * @param horizontalExtraPadding horizontal lines extra padding.
 * @param verticalExtraPadding vertical lines extra padding.
 */
data class CartesianSystemPreferences(
    val horizontalLines: List<HorizontalLine>,
    val verticalLines: List<VerticalLine>,

    val verticalLabelsPreferences: LabelSizePreferences = LabelSizePreferences(
        style = TextStyle(fontSize = 10.sp),
        maxWidth = 10.dp
    ),
    val horizontalLabelsPreferences: LabelSizePreferences = LabelSizePreferences(
        style = TextStyle(fontSize = 10.sp),
        maxWidth = 30.dp
    ),

    val horizontalExtraPadding: Padding = Padding(),
    val verticalExtraPadding: Padding = Padding()
)

/**
 * [HorizontalLine]  horizontal line configurations.
 *
 * @param isLineVisible is line visible.
 * @param lineBrush line color configuration.
 * @param lineThickness line thickness.
 * @param labelAlignment label position relative to this line.
 * @param label displayed text.
 * @param lineStyle line type.
 */
data class HorizontalLine(
    val isLineVisible: Boolean = true,
    val lineBrush: Brush = SolidColor(Color.LightGray),
    val lineThickness: Dp = 1.dp,
    val labelAlignment: HorizontalLineAlignment = HorizontalLineAlignment.CENTERED,
    val label: String = "",
    val lineStyle: LineStyle = LineStyle.DashedLine()
)

/**
 * [VerticalLine] vertical line configurations.
 *
 * @param isLineVisible is line visible.
 * @param lineBrush line color configuration.
 * @param lineThickness line thickness.
 * @param labelAlignment label position relative to this line.
 * @param label displayed text.
 * @param lineStyle line type.
 */
data class VerticalLine(
    val isLineVisible: Boolean = true,
    val lineBrush: Brush = SolidColor(Color.LightGray),
    val lineThickness: Dp = 1.dp,
    val labelAlignment: VerticalLineAlignment = VerticalLineAlignment.CENTERED,
    val label: String = "",
    val lineStyle: LineStyle = LineStyle.DashedLine()
)

/**
 * [HorizontalLineAlignment] indicates how the label will be positioned relative to the line to which it is attached.
 */
enum class HorizontalLineAlignment {
    ABOVE_LINE,
    UNDER_LINE,
    CENTERED
}

/**
 * [VerticalLineAlignment] indicates how the label will be positioned relative to the line to which it is attached.
 */
enum class VerticalLineAlignment {
    BEFORE_LINE,
    AFTER_LINE,
    CENTERED
}

/**
 * [LabelSizePreferences] label configurations.
 *
 * @param style text style.
 * @param maxWidth max width of text.
 * @param maxLines max lines.
 * @param maxHeight max height (if is equal UNSPECIFIED_HEIGHT height will be infinity.
 * @param labelAndChartPadding padding from line (top between first horizontal line and labels and start between vertical and labels).
 */
data class LabelSizePreferences(
    val style: TextStyle,
    val maxWidth: Dp,
    val maxLines: Int = 1,
    val maxHeight: Dp = UNSPECIFIED_HEIGHT.dp,
    val labelAndChartPadding: Dp = 5.dp,
)

data class Padding(
    val top: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    val start: Dp = 0.dp,
    val end: Dp = 0.dp,
)

/**
 * [LineStyle] Line style configurations.
 */
sealed class LineStyle {
    /**
     * [StrokeLine] line will be displayed as simple stroke.
     */
    data object StrokeLine : LineStyle()

    /**
     * [DashedLine] line will be displayed as dash line.
     *
     * @param dashLength is length of one dash.
     * @param spaceLength minimal size of space between dashes.
     * A line will always start with a dash and always end with a dash.
     * If, for example, the length of your line is 100dp, the length of the dash is 25dp, and the length of the space between them is 5dp,
     * then 3 dashes of 25dp and two dashes between them will be shown.
     * If there is a certain length left, it will be distributed between spaces. 100 - 25 - 25 - 25 - 5 - 5 = 15, so one space equal to 5dp + 15dp / 2 = 12.dp
     */
    data class DashedLine(val dashLength: Dp = 5.dp, val spaceLength: Dp = 5.dp) : LineStyle()
}