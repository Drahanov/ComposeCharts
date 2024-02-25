package com.chartslib.charts.bar.components

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.unit.Dp
import com.chartslib.charts.bar.models.MeasuringLines

@Composable
fun MeasuringBackground(
    modifier: Modifier = Modifier,
    measureLinesX: MeasuringLines,
    measureLinesY: MeasuringLines,
    yLabelsMaxWidth: Dp,
    yLabels: (Int) -> String,
    xLabelTextStyle: TextStyle,
    yLabelTextStyle: TextStyle,
    biggestYLabel: String,
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