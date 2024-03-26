package com.chartslib.charts.cartesian.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
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
    content: (topLeft: Offset, width: Float, height: Float, drawScope: DrawScope) -> Unit = { _, _, _, _ -> },
) {
    Box(modifier = modifier) {
        val textMeasurer = rememberTextMeasurer()
        val chartWidth = remember {
            mutableStateOf(0f)
        }
        val position = remember {
            mutableStateOf(0f)
        }

        Canvas(modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(true) {
                detectHorizontalDragGestures { change, dragAmount ->
                    val maxPosition = (chartWidth.value - size.width) * (-1)
                    if (dragAmount < 0) {
                        if (position.value > maxPosition) {
                            var positionAfterAdd = position.value + dragAmount * 1.5f
                            if (positionAfterAdd < maxPosition) {
                                positionAfterAdd = maxPosition
                            }
                            position.value = positionAfterAdd
                        }
                    } else if (position.value < 0f) {
                        var positionAfterAdd = position.value + dragAmount * 1.5f
                        if (positionAfterAdd > 0f) {
                            positionAfterAdd = 0f
                        }
                        position.value = positionAfterAdd
                    }
                }
            }) {
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
                when (cartesianSysPrefs.sizePreferences) {
                    is SizePreferences.FixedToWidth -> size.width - startExtraLabelSpace - cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() - endExtraLabels - 0.5.dp.toPx()
                    is SizePreferences.SpecificSize -> if ((cartesianSysPrefs.sizePreferences.contentSize.toPx()) < size.width - startExtraLabelSpace - cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() - endExtraLabels)
                        size.width - startExtraLabelSpace - cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() - endExtraLabels
                    else cartesianSysPrefs.sizePreferences.contentSize.toPx()
                }
            chartWidth.value = width + endExtraLabels + startExtraLabelSpace

            val height =
                size.height - topExtraLabelSpace - bottomExtraLabelSpace - cartesianSysPrefs.horizontalLabelsPreferences.labelAndChartPadding.toPx()

            val sumOfLinesThicknessH =
                getSumOfLinesThickness(this, horizontalLines.map { it.lineThickness })

            val sumOfLinesThicknessV =
                getSumOfLinesThickness(
                    this,
                    verticalLines.filter { it.positionInPercentage == UNSPECIFIED_POSITION }
                        .map { it.lineThickness })


            val horizontalLineStartY =
                topExtraLabelSpace + cartesianSysPrefs.horizontalExtraPadding.top.toPx()
            val horizontalLinesHeight =
                (height - sumOfLinesThicknessH - cartesianSysPrefs.horizontalExtraPadding.top.toPx() - cartesianSysPrefs.horizontalExtraPadding.bottom.toPx()) + sumOfLinesThicknessH

            val verticalLinesStartX =
                startExtraLabelSpace.toFloat() + cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() + cartesianSysPrefs.verticalExtraPadding.start.toPx()
            val verticalLinesHeight =
                height - cartesianSysPrefs.verticalExtraPadding.top.toPx() - cartesianSysPrefs.verticalExtraPadding.bottom.toPx()
            val verticalLinesWidth =
                (width - sumOfLinesThicknessV - cartesianSysPrefs.verticalExtraPadding.start.toPx() - cartesianSysPrefs.verticalExtraPadding.end.toPx()) + sumOfLinesThicknessV

            val stepH =
                (height - sumOfLinesThicknessH - cartesianSysPrefs.horizontalExtraPadding.top.toPx() - cartesianSysPrefs.horizontalExtraPadding.bottom.toPx()) / (horizontalLines.size - 1)

            val stepV =
                (width - sumOfLinesThicknessV - cartesianSysPrefs.verticalExtraPadding.start.toPx() - cartesianSysPrefs.verticalExtraPadding.end.toPx() - cartesianSysPrefs.fixedGridLines.end.lineThickness.toPx() - cartesianSysPrefs.fixedGridLines.start.lineThickness.toPx()) / (verticalLines.filter { it.positionInPercentage == UNSPECIFIED_POSITION }.size - 1)

            drawHorizontalLines(
                lines = horizontalLines,
                startOffset = Offset(
                    startExtraLabelSpace.toFloat() + cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() + cartesianSysPrefs.horizontalExtraPadding.start.toPx(),
                    horizontalLineStartY
                ),
                drawScope = this,
                width = width - cartesianSysPrefs.horizontalExtraPadding.start.toPx() - cartesianSysPrefs.horizontalExtraPadding.end.toPx(),
                step = stepH
            )

            drawVerticalLabels(
                lines = horizontalLines,
                startOffset = Offset(
                    0f,
                    topExtraLabelSpace + cartesianSysPrefs.horizontalExtraPadding.top.toPx()
                ),
                drawScope = this,
                measuredTexts = measuredVerticalLabels,
                step = stepH
            )


            //draw start fixed line

            clipRect(left = verticalLinesStartX) {
                translate(left = position.value) {

                    drawVerticalLine(
                        cartesianSysPrefs.fixedGridLines.start,
                        this,
                        Offset(verticalLinesStartX, topExtraLabelSpace + cartesianSysPrefs.verticalExtraPadding.top.toPx()),
                        verticalLinesHeight
                    )

                    //draw end fixed line
                    drawVerticalLine(
                        cartesianSysPrefs.fixedGridLines.end,
                        this,
                        Offset(verticalLinesStartX + verticalLinesWidth - cartesianSysPrefs.fixedGridLines.end.lineThickness.toPx(), topExtraLabelSpace + cartesianSysPrefs.verticalExtraPadding.top.toPx()),
                        verticalLinesHeight
                    )

                    drawVerticalLines(
                        lines = verticalLines,
                        startOffset = Offset(
                            verticalLinesStartX + cartesianSysPrefs.fixedGridLines.start.lineThickness.toPx(),
                            topExtraLabelSpace + cartesianSysPrefs.verticalExtraPadding.top.toPx()
                        ),
                        drawScope = this,
                        height = verticalLinesHeight,
                        step = stepV,
                        width = verticalLinesWidth - cartesianSysPrefs.fixedGridLines.end.lineThickness.toPx() - cartesianSysPrefs.fixedGridLines.start.lineThickness.toPx()
                    )

                    drawHorizontalLabels(
                        lines = verticalLines,
                        startOffset = Offset(
                            startExtraLabelSpace.toFloat() + cartesianSysPrefs.verticalLabelsPreferences.labelAndChartPadding.toPx() + cartesianSysPrefs.verticalExtraPadding.start.toPx() + cartesianSysPrefs.fixedGridLines.start.lineThickness.toPx(),
                            height + cartesianSysPrefs.horizontalLabelsPreferences.labelAndChartPadding.toPx()
                        ),
                        drawScope = this,
                        measuredTexts = measuredHorizontalLabels,
                        width = verticalLinesWidth - cartesianSysPrefs.fixedGridLines.end.lineThickness.toPx() - cartesianSysPrefs.fixedGridLines.start.lineThickness.toPx(),
                        step = stepV
                    )
                }
            }

            clipRect(left = verticalLinesStartX) {
                translate(left = position.value) {
                    content.invoke(
                        Offset(
                            verticalLinesStartX  + cartesianSysPrefs.fixedGridLines.start.lineThickness.toPx(),
                            horizontalLineStartY + horizontalLines.first().lineThickness.toPx()
                        ),
                        verticalLinesWidth -  cartesianSysPrefs.fixedGridLines.end.lineThickness.toPx() - cartesianSysPrefs.fixedGridLines.start.lineThickness.toPx(),
                        horizontalLinesHeight - horizontalLines.first().lineThickness.toPx() - horizontalLines.last().lineThickness.toPx(),
                        this
                    )
                }
            }

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


            drawLabel(drawScope,Offset(
                startOffset.x,
                horizontalLineYStart - labelY
            ),  measuredTexts[lines.indexOf(line)]!!)

            horizontalLineYStart += step + line.lineThickness.toPx()
        }
    }
}

private fun drawLabel(
    drawScope: DrawScope,
    topLeft: Offset,
    textLayoutResult: TextLayoutResult
) {
    drawScope.run {
        drawText(
            textLayoutResult,
            topLeft = topLeft
        )
    }
}

private fun drawHorizontalLines(
    lines: List<HorizontalLine>,
    startOffset: Offset,
    drawScope: DrawScope,
    width: Float,
    step: Float
) {

    drawScope.run {
        var horizontalLineYStart = startOffset.y
        val lineX = startOffset.x

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

            horizontalLineYStart += step + line.lineThickness.toPx()
        }
    }
}

private fun drawHorizontalLabels(
    lines: List<VerticalLine>,
    drawScope: DrawScope,
    measuredTexts: HashMap<Int, TextLayoutResult>,
    startOffset: Offset,
    step: Float,
    width: Float
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
                topLeft = Offset((if (line.positionInPercentage != UNSPECIFIED_POSITION) startOffset.x + (width / 100) * line.positionInPercentage - line.lineThickness.toPx() / 2 else verticalLineXStart) - labelX, startOffset.y)
            )

            verticalLineXStart += step + line.lineThickness.toPx()
        }
    }
}

private fun drawFixedGridLines(
    drawScope: DrawScope,
    height: Float,
    lines: FixedGridLines,
    start: Offset
) {
    drawVerticalLine(
        lines.start,
        drawScope,
        start,
        height
    )
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
    width: Float,
    step: Float
) {
    drawScope.run {
        var verticalLineXStart = startOffset.x
        for (line in lines) {
            drawVerticalLine(
                line,
                drawScope,
                Offset(
                    if (line.positionInPercentage != UNSPECIFIED_POSITION) startOffset.x + (width / 100) * line.positionInPercentage - line.lineThickness.toPx() / 2 else verticalLineXStart,
                    startOffset.y
                ),
                height
            )
            verticalLineXStart += step + line.lineThickness.toPx()
        }
    }
}

private fun drawVerticalLine(
    line: VerticalLine,
    drawScope: DrawScope,
    topLeft: Offset,
    height: Float,
) {
    drawScope.run {
        if (line.isLineVisible) {
            if (line.lineStyle is LineStyle.StrokeLine)
                drawRect(
                    topLeft = topLeft,
                    brush = line.lineBrush,
                    size = Size(line.lineThickness.toPx(), height)
                )
            else if (line.lineStyle is LineStyle.DashedLine) {
                val dashLength = line.lineStyle.dashLength.toPx()
                val spaceLength = line.lineStyle.spaceLength.toPx()

                val countOfDashes = floor(height / (dashLength + spaceLength)).toInt()
                val spacesTogether = height - countOfDashes * dashLength
                val space = spacesTogether / (countOfDashes - 1)

                var yPosition = topLeft.y
                for (i in 0..<countOfDashes) {
                    drawRect(
                        topLeft = Offset(
                            topLeft.x,
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