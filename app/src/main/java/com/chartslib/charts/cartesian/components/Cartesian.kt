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
import androidx.compose.ui.unit.dp
import kotlin.math.floor
import kotlin.math.max

@Composable
fun Cartesian(
    modifier: Modifier,
    horizontalLines: List<HorizontalLine>,
    verticalLines: List<VerticalLine>,
    verticalLabelsPreferences: LabelPreferences = LabelPreferences(),
    horizontalLabelsPreferences: LabelPreferences = LabelPreferences(),
    sizePreferences: SizePreferences = SizePreferences.FixedToWidth,
    initialGridLines: InitialGridLines = InitialGridLines(),
    content: (topLeft: Offset, width: Float, height: Float, drawScope: DrawScope) -> Unit = { _, _, _, _ -> }
) {
    Box(modifier = modifier) {
        val textMeasurer = rememberTextMeasurer()
        val chartWidth = remember {
            mutableStateOf(0f)
        }
        val position = remember {
            mutableStateOf(0f)
        }
        Canvas(
            modifier = Modifier
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
                }
        ) {
            val measuredVerticalLabels = measureLabels1(
                this,
                horizontalLines.map { it.label },
                textMeasurer = textMeasurer,
                verticalLabelsPreferences
            )
            val measuredVerticalInitialLabels = measureLabels1(
                drawScope = this,
                labels = listOf(initialGridLines.end.label, initialGridLines.start.label),
                textMeasurer = textMeasurer,
                labelPrefs = verticalLabelsPreferences
            )

            val measuredHorizontalLabels = measureLabels1(
                this,
                verticalLines.map { it.label },
                textMeasurer,
                horizontalLabelsPreferences
            )
            val measuredHorizontalInitialLabels = measureLabels1(
                this,
                listOf(initialGridLines.top.label, initialGridLines.bottom.label),
                textMeasurer,
                horizontalLabelsPreferences
            )

            val startLabelsSpace =
                (if (measuredVerticalLabels.isNotEmpty() && measuredVerticalInitialLabels.isNotEmpty())
                    max(
                        measuredVerticalLabels.maxBy { it.value.size.width }.value.size.width,
                        measuredVerticalInitialLabels.maxBy { it.value.size.width }.value.size.width
                    ) else 0).toFloat()

            val bottomLabelSpace =
                if (measuredHorizontalLabels.isNotEmpty() && measuredHorizontalInitialLabels.isNotEmpty()) max(
                    measuredHorizontalLabels.maxBy { it.value.size.height }.value.size.height,
                    measuredHorizontalInitialLabels.maxBy { it.value.size.height }.value.size.height
                ) else 0

            val cartesianHeight =
                size.height - bottomLabelSpace - initialGridLines.bottom.lineThickness.toPx() - initialGridLines.top.lineThickness.toPx() - horizontalLabelsPreferences.labelAndChartPadding.toPx()
            val cartesianWidth = when (sizePreferences) {
                is SizePreferences.FixedToWidth -> size.width - startLabelsSpace - initialGridLines.start.lineThickness.toPx() - initialGridLines.end.lineThickness.toPx() - verticalLabelsPreferences.labelAndChartPadding.toPx()
                is SizePreferences.SpecificSize -> sizePreferences.contentSize.toPx()
            }
            chartWidth.value = cartesianWidth + startLabelsSpace + verticalLabelsPreferences.labelAndChartPadding.toPx() + initialGridLines.start.lineThickness.toPx() + initialGridLines.end.lineThickness.toPx()

            val initialStartLineTopLeft =
                Offset(startLabelsSpace + verticalLabelsPreferences.labelAndChartPadding.toPx(), 0f)
            drawVerticalLine(
                line = initialGridLines.start,
                drawScope = this,
                topLeft = initialStartLineTopLeft,
                height = cartesianHeight
            )

            drawVerticalLine(
                line = initialGridLines.end,
                drawScope = this,
                topLeft = Offset(size.width - initialGridLines.end.lineThickness.toPx(), 0f),
                height = cartesianHeight
            )

            val initialTopLineTopLeft =
                Offset(startLabelsSpace + verticalLabelsPreferences.labelAndChartPadding.toPx(), 0f)

            clipRect(left = initialTopLineTopLeft.x) {
                drawHorizontalLine(
                    line = initialGridLines.top,
                    drawScope = this,
                    topLeft = initialTopLineTopLeft,
                    width = cartesianWidth - initialGridLines.top.lineThickness.toPx(),
                    position = position.value
                )
            }


            val initialBottomLineTopLeft = Offset(
                startLabelsSpace + verticalLabelsPreferences.labelAndChartPadding.toPx(),
                cartesianHeight
            )
            clipRect(left = initialTopLineTopLeft.x) {
                drawHorizontalLine(
                    line = initialGridLines.bottom,
                    drawScope = this,
                    topLeft = initialBottomLineTopLeft,
                    width = cartesianWidth,
                    position = position.value
                )
            }


            val verticalLinesTopLeft =
                Offset(
                    startLabelsSpace + initialGridLines.start.lineThickness.toPx() + verticalLabelsPreferences.labelAndChartPadding.toPx(),
                    0f
                )
            val verticalLinesWidth = cartesianWidth

            clipRect(left = verticalLinesTopLeft.x, right = size.width - initialGridLines.end.lineThickness.toPx()) {
                drawVerticalLines(
                    lines = verticalLines,
                    startOffset = verticalLinesTopLeft,
                    drawScope = this,
                    height = cartesianHeight,
                    width = verticalLinesWidth,
                    position = position.value
                )

            }


            val horizontalLabelsTopLeft =
                Offset(
                    startLabelsSpace + initialGridLines.start.lineThickness.toPx() + verticalLabelsPreferences.labelAndChartPadding.toPx(),
                    cartesianHeight + horizontalLabelsPreferences.labelAndChartPadding.toPx()
                )
            val verticalLabelsTopLeft =
                Offset(
                    0f,
                    initialGridLines.top.lineThickness.toPx()
                )
            clipRect(left = initialTopLineTopLeft.x) {
                drawHorizontalLabels(
                    lines = verticalLines,
                    startOffset = horizontalLabelsTopLeft,
                    drawScope = this,
                    measuredTexts = measuredHorizontalLabels,
                    width = verticalLinesWidth,
                    position = position.value
                )
            }


            val horizontalLinesTopLeft =
                Offset(
                    startLabelsSpace + verticalLabelsPreferences.labelAndChartPadding.toPx(),
                    initialGridLines.top.lineThickness.toPx()
                )
            val horizontalLinesHeight =
                cartesianHeight

            clipRect(left = verticalLinesTopLeft.x) {
                drawHorizontalLines(
                    lines = horizontalLines,
                    startOffset = horizontalLinesTopLeft,
                    drawScope = this,
                    height = horizontalLinesHeight,
                    width = cartesianWidth,
                    position = position.value
                )
            }

            drawVerticalLabels(
                lines = horizontalLines,
                startOffset = verticalLabelsTopLeft,
                drawScope = this,
                measuredTexts = measuredVerticalLabels,
                height = horizontalLinesHeight
            )

            clipRect(left = verticalLinesTopLeft.x) {
                translate(left = position.value) {
                    content.invoke(
                        Offset(verticalLinesTopLeft.x, horizontalLinesTopLeft.y),
                        verticalLinesWidth,
                        horizontalLinesHeight,
                        this
                    )
                }
            }
        }
    }
}

private fun drawVerticalLines(
    lines: List<VerticalLine>,
    startOffset: Offset,
    drawScope: DrawScope,
    height: Float,
    width: Float,
    position: Float
) {
    drawScope.run {
        for (line in lines) {
            drawVerticalLine(
                line,
                drawScope,
                Offset(
                    startOffset.x + (width / 100) * line.positionInPercentage,
                    startOffset.y
                ),
                height,
                position
            )
        }
    }
}

private fun drawHorizontalLines(
    lines: List<HorizontalLine>,
    startOffset: Offset,
    drawScope: DrawScope,
    height: Float,
    width: Float,
    position: Float
) {
    drawScope.run {
        for (line in lines) {
            drawHorizontalLine(
                line, drawScope, Offset(
                    startOffset.x,
                    startOffset.y + (height / 100) * line.positionInPercentage
                ),
                width,
                position = position
            )
        }
    }
}

private fun drawVerticalLabels(
    lines: List<HorizontalLine>,
    drawScope: DrawScope,
    measuredTexts: HashMap<Int, TextLayoutResult>,
    startOffset: Offset,
    height: Float,
) {
    drawScope.run {
        for (line in lines) {
            val labelX =
                when (line.labelAlignment) {
                    HorizontalLineAlignment.ABOVE_LINE -> {
                        measuredTexts[0]!!.size.height.toFloat()
                    }

                    HorizontalLineAlignment.CENTERED -> {
                        measuredTexts[0]!!.size.height.toFloat() / 2
                    }

                    else -> {
                        0f
                    }
                }

            drawLabel(
                measuredTexts[lines.indexOf(line)]!!,
                topLeft = Offset(
                    startOffset.x,
                    startOffset.y + (height / 100) * line.positionInPercentage - labelX
                ),
                drawScope = drawScope,
                position = 0f
            )
        }
    }
}

private fun drawVerticalLine(
    line: VerticalLine,
    drawScope: DrawScope,
    topLeft: Offset,
    height: Float,
    position: Float = 0f
) {
    drawScope.run {
        if (line.isLineVisible) {
            if (line.lineStyle is LineStyle.StrokeLine) {
                translate(left = position) {
                    drawRect(
                        topLeft = topLeft,
                        brush = line.lineBrush,
                        size = Size(line.lineThickness.toPx(), height)
                    )
                }

            } else if (line.lineStyle is LineStyle.DashedLine) {
                val dashLength = line.lineStyle.dashLength.toPx()
                val spaceLength = line.lineStyle.spaceLength.toPx()

                val countOfDashes = floor(height / (dashLength + spaceLength)).toInt()
                val spacesTogether = height - countOfDashes * dashLength
                val space = spacesTogether / (countOfDashes - 1)

                var yPosition = topLeft.y
                for (i in 0..<countOfDashes) {
                    translate(left = position) {
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
                    }

                    yPosition += dashLength + space
                }
            }
        }
    }
}

private fun drawHorizontalLine(
    line: HorizontalLine,
    drawScope: DrawScope,
    topLeft: Offset,
    width: Float,
    position: Float = 0f
) {
    drawScope.run {
        if (line.isLineVisible) {
            if (line.lineStyle is LineStyle.StrokeLine) {
                translate(left = position) {
                    drawRect(
                        topLeft = topLeft,
                        brush = line.lineBrush,
                        size = Size(height = line.lineThickness.toPx(), width = width)
                    )
                }
            } else if (line.lineStyle is LineStyle.DashedLine) {
                val dashLength = line.lineStyle.dashLength.toPx()
                val spaceLength = line.lineStyle.spaceLength.toPx()

                val countOfDashes = floor(width / (dashLength + spaceLength)).toInt()
                val spacesTogether = width - countOfDashes * dashLength
                val space = spacesTogether / (countOfDashes - 1)

                var xPosition = topLeft.x
                for (i in 0..<countOfDashes) {
                    translate(left = position) {
                        drawRect(
                            topLeft = Offset(
                                xPosition,
                                topLeft.y
                            ),
                            brush = line.lineBrush,
                            size = Size(
                                height = line.lineThickness.toPx(),
                                width = if (countOfDashes == 1) width else dashLength
                            )
                        )
                    }

                    xPosition += dashLength + space
                }
            }
        }
    }
}

private fun drawLabel(
    measuredText: TextLayoutResult,
    topLeft: Offset,
    drawScope: DrawScope,
    position: Float
) {
    drawScope.run {
        translate(left = position) {
            drawText(measuredText, topLeft = topLeft)
        }
    }
}

private fun drawHorizontalLabels(
    lines: List<VerticalLine>,
    drawScope: DrawScope,
    measuredTexts: HashMap<Int, TextLayoutResult>,
    startOffset: Offset,
    width: Float,
    position: Float
) {
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

            drawLabel(
                measuredTexts[lines.indexOf(line)]!!,
                topLeft = Offset(
                    startOffset.x + (width / 100) * line.positionInPercentage - labelX,
                    startOffset.y
                ),
                drawScope = drawScope,
                position = position
            )
        }
    }
}

/**
 * Returns map of measured labels.
 */
private fun measureLabels1(
    drawScope: DrawScope,
    labels: List<String>,
    textMeasurer: TextMeasurer,
    labelPrefs: LabelPreferences
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
