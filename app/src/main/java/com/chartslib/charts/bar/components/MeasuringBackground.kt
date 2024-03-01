package com.chartslib.charts.bar.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chartslib.charts.bar.models.HorizontalLine
import com.chartslib.charts.bar.models.HorizontalLineAlignment
import com.chartslib.charts.bar.models.LabelSizePreferences
import com.chartslib.charts.bar.models.MeasuringLines
import com.chartslib.charts.bar.models.UNSPECIFIED_HEIGHT
import com.chartslib.charts.bar.models.UtilityLines
import com.chartslib.charts.bar.models.VerticalLine
import com.chartslib.charts.bar.models.VerticalLineAlignment

@Composable
fun MeasuringBackground(
    modifier: Modifier = Modifier,
    measureLinesX: MeasuringLines,
    measureLinesY: MeasuringLines,
    yLabelsMaxWidth: Dp,
    yLabels: (Int) -> String,
    xLabels: (Int) -> String,
    xLabelTextStyle: TextStyle,
    yLabelTextStyle: TextStyle,
    biggestYLabel: String,
    columnWidth: Float
) {
    Box(modifier = modifier) {
        val context = LocalContext.current
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        val textMeasurer = rememberTextMeasurer()

        val xDecorationLineWidth = with(LocalDensity.current) { measureLinesX.width.toPx() }
        val yDecorationLineWidth = with(LocalDensity.current) { measureLinesY.width.toPx() }

        //count x labels font height
        val xLabelPaint = Paint().apply {
            textSize = xLabelTextStyle.fontSize.value * scaledDensity
        }
        val fm = xLabelPaint.fontMetrics
        val xLabelFontHeight = fm.descent - fm.ascent

        //count y labels font width
        val yLabelPaint = Paint().apply {
            textSize = yLabelTextStyle.fontSize.value * scaledDensity
        }

        //getting biggest possible width of y label
        val paintMeasure = yLabelPaint.measureText(biggestYLabel + 1)
        val yLabelWidth =
            if (paintMeasure > with(LocalDensity.current) { yLabelsMaxWidth.toPx() })
                with(LocalDensity.current) { yLabelsMaxWidth.toPx() }
            else paintMeasure

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width - yLabelWidth
            val height = size.height - xLabelFontHeight

            //draw vertical measuring lines
            var verticalLineX = yLabelWidth
            for (verticalLine in 0..measureLinesY.steps) {
                drawRect(
                    topLeft = Offset(verticalLineX, 0f),
                    brush = measureLinesY.brush,
                    size = Size(yDecorationLineWidth, height)
                )
                verticalLineX += (width - (yDecorationLineWidth)) / measureLinesY.steps
            }

            var horizontalTextStart = 0f
            for (text in 0 until measureLinesY.steps) {
                val text = xLabels(text)
                val textSize = Paint().apply {
                    textSize =
                        xLabelTextStyle.fontSize.value * scaledDensity
                }.measureText(text)

                val measuredText = textMeasurer.measure(
                    AnnotatedString(text),
                    constraints = Constraints.fixed(
                        width = columnWidth.toInt(),
                        height = xLabelFontHeight.toInt()
                    ),
                    overflow = TextOverflow.Ellipsis,
                    style = xLabelTextStyle
                )

                drawText(
                    measuredText, topLeft = Offset(
                        horizontalTextStart,
                        height
                    )
                )
                horizontalTextStart += columnWidth

            }

            //draw horizontal measuringLines
            var horizontalLineY = 0f
            for (horizontalLine in measureLinesX.steps + 1 downTo 0) {
                drawRect(
                    topLeft = Offset(xLabelFontHeight, horizontalLineY),
                    brush = measureLinesX.brush,
                    size = Size(height = xDecorationLineWidth, width = width)
                )
                val text = yLabels(horizontalLine - 1)

                val measuredText =
                    textMeasurer.measure(
                        AnnotatedString(text),
                        constraints = Constraints.fixed(
                            width = xLabelFontHeight.toInt(),
                            height = xLabelFontHeight.toInt()
                        ),
                        overflow = TextOverflow.Ellipsis,
                        style = yLabelTextStyle
                    )

                drawText(
                    measuredText, topLeft = Offset(
                        yLabelWidth - (Paint().apply {
                            textSize = yLabelTextStyle.fontSize.value * scaledDensity
                        }).measureText(text + 1),
                        if (horizontalLine == measureLinesX.steps + 1) horizontalLineY else horizontalLineY - xLabelFontHeight
                    )
                )
                horizontalLineY += (height - (xDecorationLineWidth)) / measureLinesX.steps
            }
        }
    }
}

@Composable
fun UtilityBackground(
    utilityLines: UtilityLines
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer()

        Canvas(modifier = Modifier.fillMaxSize()) {

            val horizontalLines = utilityLines.horizontalLines
            val verticalLines = utilityLines.verticalLines

            val measuredVerticalLabels = measureLabels(
                this,
                horizontalLines.map { it.label },
                textMeasurer,
                utilityLines.verticalLabelsPreferences
            )

            val measuredHorizontalLabels = measureLabels(
                this,
                verticalLines.map { it.label },
                textMeasurer,
                utilityLines.horizontalLabelsPreferences
            )

            var startExtraLabelSpace = measuredVerticalLabels.maxBy { it.value.size.width }.value.size.width
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
                bottomExtraLabelSpace = measuredHorizontalLabels.maxOf { it.value.size.height }.toFloat()
            }

            val endExtraLabels = if (verticalLines.last().alignment == VerticalLineAlignment.CENTERED) {
                measuredHorizontalLabels[measuredHorizontalLabels.size - 1]!!.size.width / 2
            } else if (verticalLines.last().alignment == VerticalLineAlignment.AFTER_LINE) {
                measuredHorizontalLabels[measuredHorizontalLabels.size - 1]!!.size.width
            } else {
                0
            }

            val width =
                size.width - startExtraLabelSpace - utilityLines.verticalLabelsPreferences.labelAndChartPadding.toPx() - endExtraLabels
            val height = size.height - topExtraLabelSpace - bottomExtraLabelSpace - utilityLines.horizontalLabelsPreferences.labelAndChartPadding.toPx()

            drawHorizontalLines(
                lines = horizontalLines,
                startOffset = Offset(
                    startExtraLabelSpace.toFloat() + utilityLines.verticalLabelsPreferences.labelAndChartPadding.toPx(),
                    topExtraLabelSpace
                ),
                drawScope = this,
                width = width,
                height = height,
                measuredVerticalLabels,
                utilityLines.verticalLabelsPreferences.labelAndChartPadding.toPx()
            )

            drawVerticalLines(
                lines = verticalLines,
                startOffset = Offset(
                    startExtraLabelSpace.toFloat() + utilityLines.verticalLabelsPreferences.labelAndChartPadding.toPx(),
                    topExtraLabelSpace
                ),
                drawScope = this,
                width = width,
                height = height,
                measuredHorizontalLabels,
                utilityLines.horizontalLabelsPreferences.labelAndChartPadding.toPx()
            )

            /**
             * Draw horizontal lines.
             * If [utilityLines.horizontalLines] lines is instance of [HorizontalLinesPattern.FixedSize]
             * we have to draw fixed size of lines which are evenly sprayed along the entire length of y line.
             */
//            var horizontalLineYStart = 0f
//            if (utilityLines.horizontalLines is HorizontalLinesPattern.FixedSize) {
//                val lines = utilityLines.horizontalLines.lines
//
//                val measuredTexts = HashMap<Int, TextLayoutResult>()
//                var linesWidthSum = 0f
//                var labelWidth = 0f
//                for (line in lines) {
//                    linesWidthSum += line.lineWidth.toPx()
//                    val measuredText =
//                        textMeasurer.measure(
//                            text = AnnotatedString(line.label),
//                            style = utilityLines.verticalLabelsPreferences.style,
//                            maxLines = utilityLines.verticalLabelsPreferences.maxLines,
//                            constraints = Constraints(
//                                maxWidth = utilityLines.verticalLabelsPreferences.maxWidth.toPx().toInt()
//                            ),
//                            overflow = TextOverflow.Ellipsis,
//                        )
//                    measuredTexts.put(measuredTexts.size, measuredText)
//                    if (measuredText.size.width > labelWidth) labelWidth =
//                        measuredText.size.width.toFloat()
//                }
//
//                val internalWidth = width - labelWidth
//
//                drawRect(
//                    topLeft = Offset(0f, 0f),
//                    size = Size(height = height, width = labelWidth),
//                    brush = SolidColor(
//                        Color.Unspecified
//                    )
//                )
//                drawText(
//                    measuredTexts[2]!!,
//                    topLeft = Offset(0f, 0f),
//                )
//                for (line in lines) {
//                    val lineWidthPx = with(density) { line.lineWidth.toPx() }
//                    drawRect(
//                        topLeft = Offset(
//                            labelWidth,
//                            horizontalLineYStart
//                        ),
//                        brush = line.lineBrush,
//                        size = Size(height = lineWidthPx, width = internalWidth)
//                    )
//
//
//                    val step = (height - linesWidthSum) / (lines.size - 1) + line.lineWidth.toPx()
//                    val textPosition =
//                        if (utilityLines.horizontalLinesLabelAlignment == HorizontalLineAlignment.ABOVE_LINE) horizontalLineYStart
//                        else if (utilityLines.horizontalLinesLabelAlignment == HorizontalLineAlignment.CENTERED) horizontalLineYStart + step / 2
//                        else horizontalLineYStart + step
//
////                    drawText(measuredText, topLeft = Offset(0f, textPosition))
//                    horizontalLineYStart += step
//                }
//            } else if (utilityLines.horizontalLines is HorizontalLinesPattern.EveryDp) {
//                /**
//                 * If [utilityLines.horizontalLines] lines is instance of [HorizontalLinesPattern.EveryDp]
//                 * we have to draw one line every N dp. The width of the entire available area is divided by the N dp value.
//                 * Integer value obtained during division will be the number of drawn lines. The remainder will be thrown away.
//                 * Please note that in this case it is possible that the distance between the lines will be slightly greater than the given value.
//                 * For example if height of area is 310dp and value is 20dp than will be displayed 15 lines with 20.66 dp between them.
//                 */
//                val defaultLine = utilityLines.horizontalLines.lineDefault
//                val firstLine = utilityLines.horizontalLines.firstLine
//                val lastLine = utilityLines.horizontalLines.lastLine
//                val specialLines = utilityLines.horizontalLines.lines
//
//                val linesCount = (height / utilityLines.horizontalLines.everyDp.toPx()).toInt()
//                var linesWidthSum = 0f
//                for (lineIndex in 0..linesCount) {
//                    val line: HorizontalLine =
//                        if (lineIndex == 0)
//                            firstLine
//                        else if (lineIndex == linesCount)
//                            lastLine
//                        else if (specialLines(lineIndex) != null)
//                            specialLines(lineIndex)!!
//                        else defaultLine
//                    linesWidthSum += line.lineWidth.toPx()
//                }
//
//                for (lineIndex in 0..linesCount) {
//                    val line: HorizontalLine =
//                        if (lineIndex == 0)
//                            firstLine
//                        else if (lineIndex == linesCount)
//                            lastLine
//                        else if (specialLines(lineIndex) != null)
//                            specialLines(lineIndex)!!
//                        else defaultLine
//
//                    drawRect(
//                        topLeft = Offset(
//                            0f,
//                            horizontalLineYStart
//                        ),
//                        brush = line.lineBrush,
//                        size = Size(height = line.lineWidth.toPx(), width = width)
//                    )
//
//                    horizontalLineYStart += (height - linesWidthSum) / (linesCount) + line.lineWidth.toPx()
//                }
//            }

            /**
             * Draw vertical lines lines.
             * If [utilityLines.verticalLines] lines is instance of [VerticalLinesPattern.FixedSize]
             * we have to draw fixed size of lines which are evenly sprayed along the entire length of x line.
             */
//            var verticalLineXStart = 0f
//            if (utilityLines.verticalLines is VerticalLinesPattern.FixedSize) {
//                val lines = utilityLines.verticalLines.lines
//
//                var linesWidthSum = 0f
//                for (line in lines) {
//                    linesWidthSum += line.lineWidth.toPx()
//                }
//
//                for (line in lines) {
//                    val lineWidthPx = with(density) { line.lineWidth.toPx() }
//
//                    drawRect(
//                        topLeft = Offset(verticalLineXStart, 0f),
//                        brush = line.lineBrush,
//                        size = Size(lineWidthPx, height)
//                    )
//                    verticalLineXStart += (width - linesWidthSum) / (lines.size - 1) + line.lineWidth.toPx()
//                }
//            }
//            else if (utilityLines.verticalLines is VerticalLinesPattern.EveryDp) {
//                /**
//                 * If [utilityLines.verticalLines] lines is instance of [VerticalLinesPattern.EveryDp]
//                 * we have to draw one line every N dp. The width of the entire available area is divided by the N dp value.
//                 * Integer value obtained during division will be the number of drawn lines. The remainder will be thrown away.
//                 * Please note that in this case it is possible that the distance between the lines will be slightly greater than the given value.
//                 * For example if width of area is 310dp and value is 20dp than will be displayed 15 lines with 20.66 dp between them.
//                 */
//                val defaultLine = utilityLines.verticalLines.lineDefault
//                val firstLine = utilityLines.verticalLines.firstLine
//                val lastLine = utilityLines.verticalLines.lastLine
//                val specialLines = utilityLines.verticalLines.lines
//
//                val linesCount = (height / utilityLines.verticalLines.everyDp.toPx()).toInt()
//                var linesWidthSum = 0f
//                for (lineIndex in 0..linesCount) {
//                    val line: VerticalLine =
//                        if (lineIndex == 0)
//                            firstLine
//                        else if (lineIndex == linesCount)
//                            lastLine
//                        else if (specialLines(lineIndex) != null)
//                            specialLines(lineIndex)!!
//                        else defaultLine
//                    linesWidthSum += line.lineWidth.toPx()
//                }
//
//                for (lineIndex in 0..linesCount) {
//                    val line: VerticalLine =
//                        if (lineIndex == 0)
//                            firstLine
//                        else if (lineIndex == linesCount)
//                            lastLine
//                        else if (specialLines(lineIndex) != null)
//                            specialLines(lineIndex)!!
//                        else defaultLine
//
//                    drawRect(
//                        topLeft = Offset(verticalLineXStart, 0f),
//                        brush = line.lineBrush,
//                        size = Size(line.lineWidth.toPx(), height)
//                    )
//                    verticalLineXStart += (width - linesWidthSum) / (linesCount) + line.lineWidth.toPx()
//                }
//            }
        }
    }
}

private fun drawHorizontalLines(
    lines: List<HorizontalLine>,
    startOffset: Offset,
    drawScope: DrawScope,
    width: Float,
    height: Float,
    measuredTexts: HashMap<Int, TextLayoutResult>,
    labelPadding: Float
) {
    drawScope.run {
        var horizontalLineYStart = startOffset.y
        val sumOfLinesThickness = getSumOfLinesThickness(drawScope, lines.map { it.lineWidth })
        for (line in lines) {
            if (line.isLineVisible) {
                drawRect(
                    topLeft = Offset(
                        startOffset.x,
                        horizontalLineYStart
                    ),
                    brush = line.lineBrush,
                    size = Size(height = line.lineWidth.toPx(), width = width)
                )
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

            val step = (height - sumOfLinesThickness) / (lines.size - 1) + line.lineWidth.toPx()
            horizontalLineYStart += step
        }
    }
}


private fun drawVerticalLines(
    lines: List<VerticalLine>,
    startOffset: Offset,
    drawScope: DrawScope,
    width: Float,
    height: Float,
    measuredTexts: HashMap<Int, TextLayoutResult>,
    labelPadding: Float
) {
    drawScope.run {
        var verticalLineXStart = startOffset.x
        val sumOfLinesThickness = getSumOfLinesThickness(drawScope, lines.map { it.lineWidth })
        for (line in lines) {
            drawRect(
                topLeft = Offset(verticalLineXStart, startOffset.y),
                brush = line.lineBrush,
                size = Size(line.lineWidth.toPx(), height)
            )

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

            verticalLineXStart += (width - sumOfLinesThickness) / (lines.size - 1) + line.lineWidth.toPx()
        }
    }
}

private fun getSumOfLinesThickness(drawScope: DrawScope, values: List<Dp>): Float {
    var result = 0f
    drawScope.run {
        for (value in values) {
            result += value.toPx()
        }
    }
    return result
}

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