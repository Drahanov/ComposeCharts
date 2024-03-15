package com.chartslib.charts.cartesian.components

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val UNSPECIFIED_HEIGHT = Float.MIN_VALUE
const val UNSPECIFIED_WIDTH = Float.MAX_VALUE

const val UNSPECIFIED_POSITION = Float.MIN_VALUE
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

    val verticalLabelsPreferences: LabelSizePreferences = LabelSizePreferences(),
    val horizontalLabelsPreferences: LabelSizePreferences = LabelSizePreferences(),

    val horizontalExtraPadding: Padding = Padding(),
    val verticalExtraPadding: Padding = Padding(),

    val sizePreferences: SizePreferences = SizePreferences.FixedToWidth
)

sealed class SizePreferences {
    object FixedToWidth: SizePreferences()
    data class SpecificSize(val contentSize: Dp): SizePreferences()
}

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
) {
    class Builder {
        private var steps: Int = 0
        private var specificLines: MutableList<HorizontalLine> = mutableListOf()
        private var labels: (Int) -> String = { it.toString() }
        private var visibleLines: (Int) -> Boolean = { true }
        private var lineStyles: (Int) -> LineStyle = { LineStyle.DashedLine() }
        private var labelAlignment: (Int) -> HorizontalLineAlignment = { HorizontalLineAlignment.CENTERED }
        private var linesThickness: (Int) -> Dp = { 1.dp }
        private var lineBrush: (Int) -> Brush = { SolidColor(Color.LightGray) }

        fun setSteps(steps: Int) = apply {
            this.steps = steps
        }

        fun setSpecificLines(lines: List<HorizontalLine>) = apply {
            this.specificLines.addAll(lines)
        }

        fun setLabels(labels: (Int) -> String) = apply {
            this.labels = labels
        }

        fun setVisibleLines(visibleLines: (Int) -> Boolean) = apply {
            this.visibleLines = visibleLines
        }

        fun setLineStyles(linesStyle: (Int) -> LineStyle) = apply {
            this.lineStyles = linesStyle
        }

        fun setLabelAlignment(labelAlignment: (Int) -> HorizontalLineAlignment) = apply {
            this.labelAlignment = labelAlignment
        }

        fun setLinesThickness(linesThickness: (Int) -> Dp) = apply {
            this.linesThickness = linesThickness
        }

        fun setLinesBrush(linesBrush: (Int) -> Brush) = apply {
            this.lineBrush = linesBrush
        }

        fun build(): List<HorizontalLine> {
            val lines = mutableListOf<HorizontalLine>()
            for (i in 0..steps - 1) {
                lines.add(
                    HorizontalLine(
                        label = "",
                        isLineVisible = visibleLines.invoke(i),
                        lineStyle = lineStyles.invoke(i),
                        lineThickness = linesThickness.invoke(i),
                        lineBrush = lineBrush.invoke(i),
                        labelAlignment = labelAlignment.invoke(i)
                    )
                )
            }
            lines.addAll(specificLines)
            return lines
        }
    }
}


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
    val lineStyle: LineStyle = LineStyle.DashedLine(),
    val positionInPercentage: Float = UNSPECIFIED_POSITION
) {
    class Builder {
        private var unspecifiedLinesAmount: Int = 0
        private var specificLines: MutableList<VerticalLine> = mutableListOf()
        private var labels: (Int) -> String = { it.toString() }

        fun setUnspecifiedLinesAmount(steps: Int) = apply {
            this.unspecifiedLinesAmount = steps
        }

        fun setSpecifiedLinesAmount(amount: Int, lines: (Int) -> VerticalLine) = apply {
            for (i in 0..<amount) {
                specificLines.add(lines.invoke(i))
            }
        }

        fun setLabels(labels: (Int) -> String) = apply {
            this.labels = labels
        }

        fun build(): List<VerticalLine> {
            val lines = mutableListOf<VerticalLine>()
            for (i in 0..<unspecifiedLinesAmount) {
                lines.add(VerticalLine(label = labels.invoke(i)))
            }

            lines.addAll(specificLines)
            return lines
        }
    }
}


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
    val style: TextStyle = TextStyle(fontSize = 10.sp),
    val maxLines: Int = 1,
    val maxWidth: Dp = UNSPECIFIED_WIDTH.dp,
    val maxHeight: Dp = UNSPECIFIED_HEIGHT.dp,
    val labelAndChartPadding: Dp = 0.dp,
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
    data object StrokeLine : LineStyle()
    data class DashedLine(val dashLength: Dp = 5.dp, val spaceLength: Dp = 5.dp) : LineStyle()
}