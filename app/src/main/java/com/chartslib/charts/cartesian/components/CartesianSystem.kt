package com.chartslib.charts.cartesian.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.floor

/**
 * The CartesianSystem function is responsible for creating a coordinate system
 * with horizontal and vertical lines, as well as applying labels to these lines.
 *
 * @param [modifier] compose modifier.
 * @param [cartesianSysPrefs] an object that contains all the settings for
 * constructing the coordinate system (eg, line sizes, label style, etc.).
 */
@Composable
fun CartesianSystem(
    modifier: Modifier,
    cartesianSysPrefs: CartesianSystemPreferences,
    content: (topLeft: Offset, width: Float, height: Float, drawScope: DrawScope) -> Unit = { _, _, _, _ -> }
) {
    Box(modifier = modifier) {
        val textMeasurer = rememberTextMeasurer()

        Canvas(modifier = Modifier.fillMaxSize()) {

            val horizontalLines = cartesianSysPrefs.horizontalLines.reversed()
            val verticalLines = cartesianSysPrefs.verticalLines

            val measuredVerticalLabels = measureLabels(
                this,
                horizontalLines.map { it.label },
                textMeasurer,
                cartesianSysPrefs.verticalLabelsPreferences
            )

            val measuredHorizontalLabels = measureLabels(
                this,
                verticalLines.map { it.label },
                textMeasurer,
                cartesianSysPrefs.horizontalLabelsPreferences
            )

            /**
             * Here we measure the width of the labels located on the vertical y.
             * Biggest width will be start space for vertical labels.
             * We also check whether it is there first element of horizontal axis with parameter [VerticalLineAlignment.CENTERED] or [VerticalLineAlignment.BEFORE_LINE]
             * and if this number is greater than the largest vertically, then this will be the start space,
             * cos it will extend to the left by its full length if it is [VerticalLineAlignment.BEFORE_LINE] and by half if it is [VerticalLineAlignment.CENTERED].
             */
            var startExtraLabelSpace =
                if (measuredVerticalLabels.isNotEmpty()) measuredVerticalLabels.maxBy { it.value.size.width }.value.size.width else 0
            if (verticalLines[0].labelAlignment == VerticalLineAlignment.CENTERED) {
                if (measuredHorizontalLabels[0]!!.size.width / 2 > startExtraLabelSpace) {
                    startExtraLabelSpace = measuredHorizontalLabels[0]!!.size.width / 2
                }
            } else if (verticalLines[0].labelAlignment == VerticalLineAlignment.BEFORE_LINE) {
                if (measuredHorizontalLabels[0]!!.size.width / 2 > startExtraLabelSpace) {
                    startExtraLabelSpace = measuredHorizontalLabels[0]!!.size.width
                }
            }


            val topExtraLabelSpace =
                when (horizontalLines.first().labelAlignment) {
                    HorizontalLineAlignment.ABOVE_LINE -> {
                        measuredVerticalLabels[0]!!.size.height.toFloat()
                    }

                    HorizontalLineAlignment.CENTERED -> {
                        measuredVerticalLabels[0]!!.size.height.toFloat() / 2
                    }

                    else -> {
                        0f
                    }
                }

            var bottomExtraLabelSpace =
                when (horizontalLines.last().labelAlignment) {
                    HorizontalLineAlignment.UNDER_LINE -> {
                        measuredVerticalLabels[measuredVerticalLabels.size - 1]!!.size.height.toFloat()
                    }

                    HorizontalLineAlignment.CENTERED -> {
                        measuredVerticalLabels[measuredVerticalLabels.size - 1]!!.size.height.toFloat() / 2
                    }

                    else -> {
                        0f
                    }
                }

            if (measuredHorizontalLabels.maxOf { it.value.size.height } > bottomExtraLabelSpace) {
                bottomExtraLabelSpace =
                    measuredHorizontalLabels.maxOf { it.value.size.height }.toFloat()
            }

            val endExtraLabels =
                if (verticalLines.last().labelAlignment == VerticalLineAlignment.CENTERED) {
                    measuredHorizontalLabels[measuredHorizontalLabels.size - 1]!!.size.width / 2
                } else if (verticalLines.last().labelAlignment == VerticalLineAlignment.AFTER_LINE) {
                    measuredHorizontalLabels[measuredHorizontalLabels.size - 1]!!.size.width
                } else {
                    0
                }

            val width =
                size.width - startExtraLabelSpace - cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() - endExtraLabels
            val height =
                size.height - topExtraLabelSpace - bottomExtraLabelSpace - cartesianSysPrefs.horizontalLabelsPreferences.labelAndChartPadding.toPx()

            val contentTopAndBottom = drawHorizontalLines(
                lines = horizontalLines,
                startOffset = Offset(
                    startExtraLabelSpace.toFloat() + cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx(),
                    topExtraLabelSpace
                ),
                drawScope = this,
                width = width,
                height = height,
                measuredTexts = measuredVerticalLabels,
                labelPadding = cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx(),
                extraPadding = cartesianSysPrefs.horizontalExtraPadding
            )

            val contentStartAndEnd = drawVerticalLines(
                lines = verticalLines,
                startOffset = Offset(
                    startExtraLabelSpace.toFloat() + cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx(),
                    topExtraLabelSpace
                ),
                drawScope = this,
                width = width,
                height = height,
                measuredTexts = measuredHorizontalLabels,
                labelPadding = cartesianSysPrefs.horizontalLabelsPreferences.labelAndChartPadding.toPx(),
                extraPadding = cartesianSysPrefs.verticalExtraPadding
            )

            content.invoke(
                Offset(contentStartAndEnd.first, contentTopAndBottom.first),
                width,
                height,
                this
            )
        }
    }
}

/**
 * Returns map of measured labels.
 */
private fun measureLabels(
    drawScope: DrawScope,
    labels: List<String>,
    textMeasurer: TextMeasurer,
    labelPrefs: LabelSizePreferences
): HashMap<Int, TextLayoutResult> {
    val measuredTexts = HashMap<Int, TextLayoutResult>()
    drawScope.run {
        for (label in labels) {
            val measuredText =
                textMeasurer.measure(
                    text = AnnotatedString(label),
                    style = labelPrefs.style,
                    maxLines = labelPrefs.maxLines,
                    constraints =
                    if (label.isNotEmpty()) Constraints(
                        maxWidth = labelPrefs.maxWidth.toPx().toInt(),
                        maxHeight = if (labelPrefs.maxHeight == UNSPECIFIED_HEIGHT.dp) Constraints.Infinity else labelPrefs.maxHeight.toPx()
                            .toInt()
                    ) else Constraints.fixed(width = 0, height = 0),
                    overflow = TextOverflow.Ellipsis,
                )
            measuredTexts[measuredTexts.size] = measuredText
        }
    }
    return measuredTexts
}

/**
 * Drawing horizontal lines.
 * Drawing fixed size of lines which are evenly sprayed along the entire length of x line.
 */
private fun drawHorizontalLines(
    lines: List<HorizontalLine>,
    startOffset: Offset,
    drawScope: DrawScope,
    width: Float,
    height: Float,
    measuredTexts: HashMap<Int, TextLayoutResult>,
    labelPadding: Float,
    extraPadding: Padding
): Pair<Float, Float> {
    var yContentTop = 0f
    var yContentBottom = 0f

    drawScope.run {
        val sumOfLinesThickness = getSumOfLinesThickness(drawScope, lines.map { it.lineThickness })

        var horizontalLineYStart = startOffset.y + extraPadding.top.toPx()
        val lineWidth = width - extraPadding.start.toPx() - extraPadding.end.toPx()
        val lineX = startOffset.x + extraPadding.start.toPx()
        yContentTop = horizontalLineYStart

        for (line in lines) {
            if (line.isLineVisible) {
                if (line.lineStyle is LineStyle.StrokeLine)
                    drawRect(
                        topLeft = Offset(
                            lineX,
                            horizontalLineYStart
                        ),
                        brush = line.lineBrush,
                        size = Size(
                            height = line.lineThickness.toPx(),
                            width = lineWidth
                        )
                    )
                else if (line.lineStyle is LineStyle.DashedLine) {
                    val dashLength = line.lineStyle.dashLength.toPx()
                    val spaceLength = line.lineStyle.spaceLength.toPx()

                    val countOfDashes = floor(lineWidth / (dashLength + spaceLength)).toInt()
                    val spacesTogether = lineWidth - countOfDashes * dashLength
                    val space = spacesTogether / (countOfDashes - 1)

                    var xPosition = lineX
                    for (i in 0..<countOfDashes) {
                        drawRect(
                            topLeft = Offset(
                                xPosition,
                                horizontalLineYStart
                            ),
                            brush = line.lineBrush,
                            size = Size(
                                height = line.lineThickness.toPx(),
                                width = if (countOfDashes == 1) lineWidth else dashLength
                            )
                        )

                        xPosition += dashLength + space
                    }
                }
            }

            val labelY =
                when (line.labelAlignment) {
                    HorizontalLineAlignment.ABOVE_LINE -> {
                        measuredTexts[lines.indexOf(line)]!!.size.height
                    }

                    HorizontalLineAlignment.CENTERED -> {
                        measuredTexts[lines.indexOf(line)]!!.size.height / 2
                    }

                    else -> {
                        0
                    }
                }

            drawText(
                measuredTexts[lines.indexOf(line)]!!,
                topLeft = Offset(
                    startOffset.x - measuredTexts[lines.indexOf(line)]!!.size.width.toFloat() - labelPadding,
                    horizontalLineYStart - labelY
                )
            )
            yContentBottom = horizontalLineYStart

            val step =
                (height - sumOfLinesThickness - extraPadding.top.toPx() - extraPadding.bottom.toPx()) / (lines.size - 1) + line.lineThickness.toPx()
            horizontalLineYStart += step
        }
    }
    return Pair(yContentTop, yContentBottom)
}

/**
 * Drawing vertical lines.
 * Drawing fixed size of lines which are evenly sprayed along the entire length of y line.
 */
private fun drawVerticalLines(
    lines: List<VerticalLine>,
    startOffset: Offset,
    drawScope: DrawScope,
    width: Float,
    height: Float,
    measuredTexts: HashMap<Int, TextLayoutResult>,
    labelPadding: Float,
    extraPadding: Padding
): Pair<Float, Float> {
    var xContentStart = 0f
    var xContentEnd = 0f

    drawScope.run {
        var verticalLineXStart = startOffset.x + extraPadding.start.toPx()
        val lineY = startOffset.y + extraPadding.top.toPx()
        val lineHeight = height - extraPadding.top.toPx() - extraPadding.bottom.toPx()
        xContentStart = verticalLineXStart

        val sumOfLinesThickness =
            getSumOfLinesThickness(drawScope, lines.map { it.lineThickness })
        for (line in lines) {
            if (line.isLineVisible)
                if (line.lineStyle is LineStyle.StrokeLine)
                    drawRect(
                        topLeft = Offset(verticalLineXStart, lineY),
                        brush = line.lineBrush,
                        size = Size(
                            line.lineThickness.toPx(),
                            lineHeight
                        )
                    )
                else if (line.lineStyle is LineStyle.DashedLine) {
                    val dashLength = line.lineStyle.dashLength.toPx()
                    val spaceLength = line.lineStyle.spaceLength.toPx()

                    val countOfDashes = floor(lineHeight / (dashLength + spaceLength)).toInt()
                    val spacesTogether = lineHeight - countOfDashes * dashLength
                    val space = spacesTogether / (countOfDashes - 1)

                    var yPosition = lineY
                    for (i in 0..<countOfDashes) {
                        drawRect(
                            topLeft = Offset(
                                verticalLineXStart,
                                yPosition
                            ),
                            brush = line.lineBrush,
                            size = Size(
                                height = if (countOfDashes == 1) lineHeight else dashLength,
                                width = line.lineThickness.toPx()
                            )
                        )

                        yPosition += dashLength + space
                    }
                }

            val labelX =
                when (line.labelAlignment) {
                    VerticalLineAlignment.BEFORE_LINE -> {
                        measuredTexts[lines.indexOf(line)]!!.size.width
                    }

                    VerticalLineAlignment.CENTERED -> {
                        measuredTexts[lines.indexOf(line)]!!.size.width / 2
                    }

                    else -> {
                        0
                    }
                }

            drawText(
                measuredTexts[lines.indexOf(line)]!!,
                topLeft = Offset(verticalLineXStart - labelX, height + labelPadding)
            )
            xContentEnd = verticalLineXStart
            verticalLineXStart += (width - sumOfLinesThickness - extraPadding.start.toPx() - extraPadding.end.toPx()) / (lines.size - 1) + line.lineThickness.toPx()
        }
    }
    return Pair(xContentStart, xContentEnd)
}

/**
 * Calculating sum of thickness of @param [values]
 */
private fun getSumOfLinesThickness(drawScope: DrawScope, values: List<Dp>): Float {
    var result = 0f
    drawScope.run {
        for (value in values) {
            result += value.toPx()
        }
    }
    return result
}