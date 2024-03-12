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
import com.chartslib.charts.line.models.SizePreferences
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
    content: (topLeft: Offset, width: Float, height: Float, drawScope: DrawScope) -> Unit = { _, _, _, _ -> },
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
            var startExtraLabelSpace = if (measuredVerticalLabels.isNotEmpty()) measuredVerticalLabels.maxBy { it.value.size.width }.value.size.width else 0
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
                when (cartesianSysPrefs.sizePreferences) {
                    is SizePreferences.FixedToWidth -> size.width - startExtraLabelSpace - cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() - endExtraLabels
                    is SizePreferences.SpecificSize -> if ((cartesianSysPrefs.sizePreferences.stepSize.toPx() * verticalLines.size) < size.width - startExtraLabelSpace - cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() - endExtraLabels)
                        size.width - startExtraLabelSpace - cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() - endExtraLabels
                    else cartesianSysPrefs.sizePreferences.stepSize.toPx() * verticalLines.size
                }

            val height =
                size.height - topExtraLabelSpace - bottomExtraLabelSpace - cartesianSysPrefs.horizontalLabelsPreferences.labelAndChartPadding.toPx()

            val sumOfLinesThicknessH =
                getSumOfLinesThickness(this, horizontalLines.map { it.lineThickness })

            val sumOfLinesThicknessV =
                getSumOfLinesThickness(this, verticalLines.map { it.lineThickness })


            val stepH =
                (height - sumOfLinesThicknessH - cartesianSysPrefs.horizontalExtraPadding.top.toPx() - cartesianSysPrefs.horizontalExtraPadding.bottom.toPx()) / (horizontalLines.size - 1)

            val stepV =
                (width - sumOfLinesThicknessV - cartesianSysPrefs.verticalExtraPadding.start.toPx() - cartesianSysPrefs.verticalExtraPadding.end.toPx()) / (verticalLines.size - 1)


            val contentTopAndBottom = drawHorizontalLines(
                lines = horizontalLines,
                startOffset = Offset(
                    startExtraLabelSpace.toFloat() + cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() +  cartesianSysPrefs.horizontalExtraPadding.start.toPx(),
                    topExtraLabelSpace + cartesianSysPrefs.horizontalExtraPadding.top.toPx()
                ),
                drawScope = this,
                width = width -  cartesianSysPrefs.horizontalExtraPadding.start.toPx() -  cartesianSysPrefs.horizontalExtraPadding.end.toPx(),
                step = stepH
            )

            drawVerticalLabels(
                lines = horizontalLines,
                startOffset = Offset(0f, topExtraLabelSpace + cartesianSysPrefs.horizontalExtraPadding.top.toPx()),
                drawScope = this,
                measuredTexts = measuredVerticalLabels,
                step = stepH
            )

            drawHorizontalLabels(
                lines = verticalLines,
                startOffset = Offset(startExtraLabelSpace.toFloat() + cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() + cartesianSysPrefs.verticalExtraPadding.start.toPx(), height + cartesianSysPrefs.horizontalLabelsPreferences.labelAndChartPadding.toPx()),
                drawScope = this,
                measuredTexts = measuredHorizontalLabels,
                step = stepV
            )

            val contentStartAndEnd = drawVerticalLines(
                lines = verticalLines,
                startOffset = Offset(startExtraLabelSpace.toFloat() + cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() + cartesianSysPrefs.verticalExtraPadding.start.toPx(), topExtraLabelSpace + cartesianSysPrefs.verticalExtraPadding.top.toPx()),
                drawScope = this,
                height = height - cartesianSysPrefs.verticalExtraPadding.top.toPx() - cartesianSysPrefs.verticalExtraPadding.bottom.toPx(),
                step = stepV
            )

            content.invoke(
                Offset(contentStartAndEnd.first, contentTopAndBottom.first),
                contentStartAndEnd.second - contentStartAndEnd.first,
                contentTopAndBottom.second - contentTopAndBottom.first,
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
private fun drawVerticalLabels(
    lines: List<HorizontalLine>,
    drawScope: DrawScope,
    measuredTexts: HashMap<Int, TextLayoutResult>,
    startOffset: Offset,
    step: Float
) {
    var horizontalLineYStart = startOffset.y

    drawScope.run {
        for (line in lines) {
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
                    startOffset.x,
                    horizontalLineYStart - labelY
                )
            )

            horizontalLineYStart += step + line.lineThickness.toPx()
        }
    }
}

private fun drawHorizontalLines(
    lines: List<HorizontalLine>,
    startOffset: Offset,
    drawScope: DrawScope,
    width: Float,
    step: Float
): Pair<Float, Float> {
    var yContentTop = 0f
    var yContentBottom = 0f

    drawScope.run {
        var horizontalLineYStart = startOffset.y
        val lineX = startOffset.x
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
                            width = width
                        )
                    )
                else if (line.lineStyle is LineStyle.DashedLine) {
                    val dashLength = line.lineStyle.dashLength.toPx()
                    val spaceLength = line.lineStyle.spaceLength.toPx()

                    val countOfDashes = floor(width / (dashLength + spaceLength)).toInt()
                    val spacesTogether = width - countOfDashes * dashLength
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
                                width = if (countOfDashes == 1) width else dashLength
                            )
                        )

                        xPosition += dashLength + space
                    }
                }
            }

            yContentBottom = horizontalLineYStart
            horizontalLineYStart += step + line.lineThickness.toPx()
        }
    }
    return Pair(yContentTop, yContentBottom)
}

private fun drawHorizontalLabels(
    lines: List<VerticalLine>,
    drawScope: DrawScope,
    measuredTexts: HashMap<Int, TextLayoutResult>,
    startOffset: Offset,
    step: Float
) {
    var verticalLineXStart = startOffset.x

    drawScope.run {
        for (line in lines) {
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
                topLeft = Offset(verticalLineXStart - labelX, startOffset.y)
            )

            verticalLineXStart += step + line.lineThickness.toPx()
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
    height: Float,
    step: Float
): Pair<Float, Float> {
    var xContentStart = 0f
    var xContentEnd = 0f

    drawScope.run {
        var verticalLineXStart = startOffset.x
        xContentStart = verticalLineXStart

        for (line in lines) {
            if (line.isLineVisible)
                if (line.lineStyle is LineStyle.StrokeLine)
                    drawRect(
                        topLeft = Offset(verticalLineXStart, startOffset.y),
                        brush = line.lineBrush,
                        size = Size(
                            line.lineThickness.toPx(),
                            height
                        )
                    )
                else if (line.lineStyle is LineStyle.DashedLine) {
                    val dashLength = line.lineStyle.dashLength.toPx()
                    val spaceLength = line.lineStyle.spaceLength.toPx()

                    val countOfDashes = floor(height / (dashLength + spaceLength)).toInt()
                    val spacesTogether = height - countOfDashes * dashLength
                    val space = spacesTogether / (countOfDashes - 1)

                    var yPosition = startOffset.y
                    for (i in 0..<countOfDashes) {
                        drawRect(
                            topLeft = Offset(
                                verticalLineXStart,
                                yPosition
                            ),
                            brush = line.lineBrush,
                            size = Size(
                                height = if (countOfDashes == 1) height else dashLength,
                                width = line.lineThickness.toPx()
                            )
                        )

                        yPosition += dashLength + space
                    }
                }

            xContentEnd = verticalLineXStart
            verticalLineXStart += step + line.lineThickness.toPx()
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