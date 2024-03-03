package com.chartslib.charts.cartesian.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
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

@Composable
fun CartesianSystem(
    modifier: Modifier,
    cartesianSysPrefs: CartesianSystemPreferences
) {
    Box(modifier = modifier) {
        val textMeasurer = rememberTextMeasurer()

        Canvas(modifier = Modifier.fillMaxSize()) {

            val horizontalLines = cartesianSysPrefs.horizontalLines
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
             * We also check whether it is there first element of vertical x with parameter [VerticalLineAlignment.CENTERED] or [VerticalLineAlignment.BEFORE_LINE]
             * and if it width is greater than the width of the labels of vertical y, then this will be the space on the left
             */
            var startExtraLabelSpace =
                measuredVerticalLabels.maxBy { it.value.size.width }.value.size.width
            if (verticalLines[0].alignment == VerticalLineAlignment.CENTERED) {
                if (measuredHorizontalLabels[0]!!.size.width / 2 > startExtraLabelSpace) {
                    startExtraLabelSpace = measuredHorizontalLabels[0]!!.size.width / 2
                }
            } else if (verticalLines[0].alignment == VerticalLineAlignment.BEFORE_LINE) {
                if (measuredHorizontalLabels[0]!!.size.width / 2 > startExtraLabelSpace) {
                    startExtraLabelSpace = measuredHorizontalLabels[0]!!.size.width
                }
            }


            val topExtraLabelSpace =
                when (horizontalLines.first().alignment) {
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
                when (horizontalLines.last().alignment) {
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
                if (verticalLines.last().alignment == VerticalLineAlignment.CENTERED) {
                    measuredHorizontalLabels[measuredHorizontalLabels.size - 1]!!.size.width / 2
                } else if (verticalLines.last().alignment == VerticalLineAlignment.AFTER_LINE) {
                    measuredHorizontalLabels[measuredHorizontalLabels.size - 1]!!.size.width
                } else {
                    0
                }

            val width =
                size.width - startExtraLabelSpace - cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() - endExtraLabels
            val height =
                size.height - topExtraLabelSpace - bottomExtraLabelSpace - cartesianSysPrefs.horizontalLabelsPreferences.labelAndChartPadding.toPx()

            drawHorizontalLines(
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

            drawVerticalLines(
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
) {
    drawScope.run {
        val sumOfLinesThickness = getSumOfLinesThickness(drawScope, lines.map { it.lineWidth })

        var horizontalLineYStart = startOffset.y + extraPadding.top.toPx()
        val lineWidth = width - extraPadding.start.toPx() - extraPadding.end.toPx()
        val lineX = startOffset.x + extraPadding.start.toPx()

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
                            height = line.lineWidth.toPx(),
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
                                height = line.lineWidth.toPx(),
                                width = if (countOfDashes == 1) lineWidth else dashLength
                            )
                        )

                        xPosition += dashLength + space
                    }
                }
            }

            val labelY =
                when (line.alignment) {
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

            val step =
                (height - sumOfLinesThickness - extraPadding.top.toPx() - extraPadding.bottom.toPx()) / (lines.size - 1) + line.lineWidth.toPx()
            horizontalLineYStart += step
        }
    }
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
) {
    drawScope.run {
        var verticalLineXStart = startOffset.x + extraPadding.start.toPx()
        val lineY = startOffset.y + extraPadding.top.toPx()
        val lineHeight = height - extraPadding.top.toPx() - extraPadding.bottom.toPx()

        val sumOfLinesThickness = getSumOfLinesThickness(drawScope, lines.map { it.lineWidth })
        for (line in lines) {
            if (line.lineStyle is LineStyle.StrokeLine)
                drawRect(
                    topLeft = Offset(verticalLineXStart, lineY),
                    brush = line.lineBrush,
                    size = Size(
                        line.lineWidth.toPx(),
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
                            width = line.lineWidth.toPx()
                        )
                    )

                    yPosition += dashLength + space
                }
            }

            val labelX =
                when (line.alignment) {
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

            verticalLineXStart += (width - sumOfLinesThickness - extraPadding.start.toPx() - extraPadding.end.toPx()) / (lines.size - 1) + line.lineWidth.toPx()
        }
    }
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