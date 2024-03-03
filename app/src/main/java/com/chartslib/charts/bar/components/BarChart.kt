package com.chartslib.charts.bar.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chartslib.charts.bar.models.BarChartConfiguration
import com.chartslib.pxToDp
import kotlin.math.ceil

@Composable
fun BarChart(
    chartConfiguration: BarChartConfiguration
) {
    val position = remember { mutableStateOf(0f) }
    val isMovable = remember { mutableStateOf(false) }
    val totalWidth = remember { mutableStateOf(0f) }
    val columnWidth = remember { mutableStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()

    val context = LocalContext.current
    val scaledDensity = context.resources.displayMetrics.scaledDensity

    val paint = Paint().apply {
        textSize = chartConfiguration.axisX.labelStyle.fontSize.value * scaledDensity
    }
    val fm = paint.fontMetrics
    val textHeight = fm.descent - fm.ascent

    var biggestLabelSize = ""
    for (horizontalLine in 0..chartConfiguration.axisY.measuringLines.steps) {
        if (chartConfiguration.axisY.label(horizontalLine).length > biggestLabelSize.length) {
            biggestLabelSize = chartConfiguration.axisY.label(horizontalLine)
        }
    }
    val paintMeasure = paint.measureText(biggestLabelSize + 1)
    val yLabelWeight =
        if (paintMeasure > with(LocalDensity.current) { 20.dp.toPx() }) with(
            LocalDensity.current
        ) { 20.dp.toPx() } else paintMeasure

    Box(
        modifier = chartConfiguration.modifier.clipToBounds()
    ) {
//        MeasuringBackground(
//            modifier = Modifier.fillMaxSize(),
//            measureLinesX = chartConfiguration.axisY.measuringLines,
//            measureLinesY = chartConfiguration.axisX.measuringLines,
//            yLabelsMaxWidth = 20.dp,
//            yLabels = chartConfiguration.axisY.label,
//            xLabels = chartConfiguration.axisX.label,
//            xLabelTextStyle = chartConfiguration.axisX.labelStyle,
//            yLabelTextStyle = chartConfiguration.axisY.labelStyle,
//            biggestYLabel = biggestLabelSize,
//            columnWidth = columnWidth.value
//        )

//        UtilityBackground(
//            utilityLines = chartConfiguration.utilityLines
//        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = pxToDp(yLabelWeight))
                .clipToBounds()
                .pointerInput(true) {
                    detectTapGestures { tapOffset ->
                        val selectedColumnIndex =
                            ceil((tapOffset.x + position.value * (-1)) / columnWidth.value) - 1
                        chartConfiguration.onSelected.invoke(chartConfiguration.columns[selectedColumnIndex.toInt()].id)
                    }
                }
                .pointerInput(true) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        val minPosition = 0f
                        val maxPosition =
                            ((chartConfiguration.minColumnWidth.toPx() * chartConfiguration.columns.size + chartConfiguration.minColumnWidth.toPx() * chartConfiguration.paddingBetweenColumns * chartConfiguration.columns.size) - (size.width - yLabelWeight)) * (-1)

                        if (isMovable.value) {
                            if (dragAmount < 0) {
                                if (position.value > maxPosition) {
                                    var positionAfterAdd = position.value + dragAmount
                                    if (positionAfterAdd < maxPosition) {
                                        positionAfterAdd = maxPosition
                                    }
                                    position.value = positionAfterAdd
                                }
                            } else if (position.value < minPosition) {
                                var positionAfterAdd = position.value + dragAmount
                                if (positionAfterAdd > minPosition) {
                                    positionAfterAdd = 0f
                                }
                                position.value = positionAfterAdd
                            }
                        }
                    }
                },
        ) {
            val width = size.width - yLabelWeight
            val height = size.height

            var perColumn = (width / chartConfiguration.columns.size)
            var spaceBetween = perColumn * chartConfiguration.paddingBetweenColumns

            if (!chartConfiguration.shouldMatchWidth) {
                if (perColumn - spaceBetween < chartConfiguration.minColumnWidth.toPx()) {
                    isMovable.value = true
                    perColumn =
                        chartConfiguration.minColumnWidth.toPx() + chartConfiguration.minColumnWidth.toPx() * chartConfiguration.paddingBetweenColumns
                    spaceBetween = perColumn * chartConfiguration.paddingBetweenColumns
                    totalWidth.value = perColumn * chartConfiguration.columns.size
                }
            }

            if (perColumn - spaceBetween > chartConfiguration.maxColumnWidth.toPx()) {
                perColumn =
                    chartConfiguration.maxColumnWidth.toPx() + chartConfiguration.maxColumnWidth.toPx() * chartConfiguration.paddingBetweenColumns
                spaceBetween = perColumn * chartConfiguration.paddingBetweenColumns
            }

            columnWidth.value = perColumn
            var start = spaceBetween / 2
            val heightPerValue = (height) / chartConfiguration.axisY.maxValue
            translate(left = position.value) {
                for (column in chartConfiguration.columns) {
//                    drawRoundRect(
//                        topLeft = Offset(start, height - (column.value * heightPerValue)),
//                        size = Size(
//                            perColumn - spaceBetween, column.value * heightPerValue - textHeight
//                        ),
//                        color = column.color,
//                        cornerRadius = CornerRadius(5f),
//                    )
//
//                    val text =
//                        chartConfiguration.axisX.label(chartConfiguration.columns.indexOf(column))
//                    val textSize = Paint().apply {
//                        textSize =
//                            chartConfiguration.axisX.labelStyle.fontSize.value * scaledDensity
//                    }.measureText(text)
//
//                    val measuredText = textMeasurer.measure(
//                        AnnotatedString(text),
//                        constraints = Constraints.fixed(
//                            width = ((perColumn - spaceBetween)).toInt(),
//                            height = textHeight.toInt()
//                        ),
//                        overflow = TextOverflow.Ellipsis,
//                        style = chartConfiguration.axisX.labelStyle
//                    )
//                    drawText(
//                        measuredText, topLeft = Offset(
//                            if (textSize < ((perColumn - spaceBetween)).toInt()) start + (perColumn - spaceBetween - textSize) / 2 else start,
//                            height - textHeight
//                        )
//                    )
                    start += perColumn
                }
            }
        }

        if (isMovable.value) ScrollIndicator(
            position = position.value,
            indicatorHeight = chartConfiguration.indicatorWidth,
            indicatorWidth = totalWidth.value,
            scrollIndicatorBrush = chartConfiguration.indicatorColor,
            labelHeight = textHeight,
            startX = yLabelWeight
        )
    }
}