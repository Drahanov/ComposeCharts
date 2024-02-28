package com.chartslib.charts.bar.components

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.chartslib.charts.bar.models.HorizontalLine
import com.chartslib.charts.bar.models.HorizontalLinesPattern
import com.chartslib.charts.bar.models.MeasuringLines
import com.chartslib.charts.bar.models.UtilityLines
import com.chartslib.charts.bar.models.VerticalLine
import com.chartslib.charts.bar.models.VerticalLinesPattern

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

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width - utilityLines.horizontalLabelsWidth.toPx()
            val height = size.height

            /**
             * Draw horizontal lines.
             * If [utilityLines.horizontalLines] lines is instance of [HorizontalLinesPattern.FixedSize]
             * we have to draw fixed size of lines which are evenly sprayed along the entire length of y line.
             */
            var horizontalLineYStart = 0f
            if (utilityLines.horizontalLines is HorizontalLinesPattern.FixedSize) {
                val lines = utilityLines.horizontalLines.lines

                var linesWidthSum = 0f
                var longestWord = ""
                for (line in lines) {
                    linesWidthSum += line.lineWidth.toPx()
                    longestWord = if (longestWord.length < line.label.length) line.label else longestWord
                }

//                val horizontalLabelWidth = measureBiggestLabelWidth(
//                    longestWord = longestWord,
//                    textStyle = utilityLines.horizontalLabelsTextStyle,
//                    maxWidth = utilityLines.horizontalLabelsMaxWidth,
//                    localDensity = density
//                )

                for (line in lines) {
                    val lineWidthPx = with(density) { line.lineWidth.toPx() }

                    drawRect(
                        topLeft = Offset(utilityLines.horizontalLabelsWidth.toPx(), horizontalLineYStart),
                        brush = line.lineBrush,
                        size = Size(height = lineWidthPx, width = width)
                    )

                    horizontalLineYStart += (height - linesWidthSum) / (lines.size - 1) + line.lineWidth.toPx()
                }
            } else if (utilityLines.horizontalLines is HorizontalLinesPattern.EveryDp) {
                /**
                 * If [utilityLines.horizontalLines] lines is instance of [HorizontalLinesPattern.EveryDp]
                 * we have to draw one line every N dp. The width of the entire available area is divided by the N dp value.
                 * Integer value obtained during division will be the number of drawn lines. The remainder will be thrown away.
                 * Please note that in this case it is possible that the distance between the lines will be slightly greater than the given value.
                 * For example if height of area is 310dp and value is 20dp than will be displayed 15 lines with 20.66 dp between them.
                 */
                val defaultLine = utilityLines.horizontalLines.lineDefault
                val firstLine = utilityLines.horizontalLines.firstLine
                val lastLine = utilityLines.horizontalLines.lastLine
                val specialLines = utilityLines.horizontalLines.lines

                val linesCount = (height / utilityLines.horizontalLines.everyDp.toPx()).toInt()
                var linesWidthSum = 0f
                for (lineIndex in 0..linesCount) {
                    val line: HorizontalLine =
                        if (lineIndex == 0)
                            firstLine
                        else if (lineIndex == linesCount)
                            lastLine
                        else if (specialLines(lineIndex) != null)
                            specialLines(lineIndex)!!
                        else defaultLine
                    linesWidthSum += line.lineWidth.toPx()
                }

                for (lineIndex in 0..linesCount) {
                    val line: HorizontalLine =
                        if (lineIndex == 0)
                            firstLine
                        else if (lineIndex == linesCount)
                            lastLine
                        else if (specialLines(lineIndex) != null)
                            specialLines(lineIndex)!!
                        else defaultLine

                    drawRect(
                        topLeft = Offset(utilityLines.horizontalLabelsWidth.toPx(), horizontalLineYStart),
                        brush = line.lineBrush,
                        size = Size(height = line.lineWidth.toPx(), width = width)
                    )

                    horizontalLineYStart += (height - linesWidthSum) / (linesCount) + line.lineWidth.toPx()
                }
            }

            /**
             * Draw vertical lines lines.
             * If [utilityLines.verticalLines] lines is instance of [VerticalLinesPattern.FixedSize]
             * we have to draw fixed size of lines which are evenly sprayed along the entire length of x line.
             */
            var verticalLineXStart = utilityLines.horizontalLabelsWidth.toPx()
            if (utilityLines.verticalLines is VerticalLinesPattern.FixedSize) {
                val lines = utilityLines.verticalLines.lines

                var linesWidthSum = 0f
                for (line in lines) {
                    linesWidthSum += line.lineWidth.toPx()
                }

                for (line in lines) {
                    val lineWidthPx = with(density) { line.lineWidth.toPx() }

                    drawRect(
                        topLeft = Offset(verticalLineXStart, 0f),
                        brush = line.lineBrush,
                        size = Size(lineWidthPx, height)
                    )
                    verticalLineXStart += (width - linesWidthSum) / (lines.size - 1) + line.lineWidth.toPx()
                }
            } else if (utilityLines.verticalLines is VerticalLinesPattern.EveryDp) {
                /**
                 * If [utilityLines.verticalLines] lines is instance of [VerticalLinesPattern.EveryDp]
                 * we have to draw one line every N dp. The width of the entire available area is divided by the N dp value.
                 * Integer value obtained during division will be the number of drawn lines. The remainder will be thrown away.
                 * Please note that in this case it is possible that the distance between the lines will be slightly greater than the given value.
                 * For example if width of area is 310dp and value is 20dp than will be displayed 15 lines with 20.66 dp between them.
                 */
                val defaultLine = utilityLines.verticalLines.lineDefault
                val firstLine = utilityLines.verticalLines.firstLine
                val lastLine = utilityLines.verticalLines.lastLine
                val specialLines = utilityLines.verticalLines.lines

                val linesCount = (height / utilityLines.verticalLines.everyDp.toPx()).toInt()
                var linesWidthSum = 0f
                for (lineIndex in 0..linesCount) {
                    val line: VerticalLine =
                        if (lineIndex == 0)
                            firstLine
                        else if (lineIndex == linesCount)
                            lastLine
                        else if (specialLines(lineIndex) != null)
                            specialLines(lineIndex)!!
                        else defaultLine
                    linesWidthSum += line.lineWidth.toPx()
                }

                for (lineIndex in 0..linesCount) {
                    val line: VerticalLine =
                        if (lineIndex == 0)
                            firstLine
                        else if (lineIndex == linesCount)
                            lastLine
                        else if (specialLines(lineIndex) != null)
                            specialLines(lineIndex)!!
                        else defaultLine

                    drawRect(
                        topLeft = Offset(verticalLineXStart, 0f),
                        brush = line.lineBrush,
                        size = Size(line.lineWidth.toPx(), height)
                    )
                    verticalLineXStart += (width - linesWidthSum) / (linesCount) + line.lineWidth.toPx()
                }
            }
        }
    }
}

//private fun measureBiggestLabelWidth(
//    longestWord: String,
//    textStyle: TextStyle,
//    maxWidth: Dp,
//    localDensity: Density
//): Float {
//    val paint = Paint().apply {
//        textSize = textStyle.fontSize.value * localDensity.density
//    }
//    val paintMeasure = paint.measureText(longestWord)
//
//    return if (with(localDensity) { maxWidth.toPx() } > paintMeasure)
//        paintMeasure
//    else with(localDensity) { maxWidth.toPx() }
//}