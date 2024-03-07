package com.chartslib.charts.line.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chartslib.charts.cartesian.components.CartesianSystem
import com.chartslib.charts.cartesian.components.CartesianSystemPreferences
import com.chartslib.charts.cartesian.components.HorizontalLine
import com.chartslib.charts.cartesian.components.VerticalLine
import com.chartslib.charts.line.models.LineModel
import com.chartslib.charts.line.models.SizePreferences
import com.chartslib.ui.theme.Azure

@Composable
fun LineChart(
    modifier: Modifier,
    lines: LineModel,
    cartesianSystemPreferences: CartesianSystemPreferences = CartesianSystemPreferences(
        horizontalLines = HorizontalLine.Builder().setSteps(5).build(),
        verticalLines = VerticalLine.Builder().setSteps(lines.points.size)
            .setLabels { lines.points[it].x.toString() }.build(),
    ),
    dotRadius: Dp = 3.5.dp
) {
    val position = remember { mutableStateOf(0f) }
    val w = remember {
        mutableStateOf(0f)
    }
    Box(modifier = modifier.pointerInput(true) {
        detectHorizontalDragGestures { change, dragAmount ->
            val minPosition = 0f
            val maxPosition =
            if (cartesianSystemPreferences.sizePreferences is SizePreferences.SpecificSize) {
                (cartesianSystemPreferences.sizePreferences.stepSize.toPx() * lines.points.size + w.value) * (-1)
            } else 0f

            if (true) {
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
    }) {
        CartesianSystem(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            cartesianSysPrefs = cartesianSystemPreferences,
            position = position
        ) { start, width, height, drawScope ->
            w.value = width
            val xPointsMax = lines.points.maxOf { it.x }
            val yPointsMax = lines.points.maxOf { it.y }

            val xPointsMin = lines.points.minOf { it.x }
            val yPointsMin = lines.points.minOf { it.y }

            val widthPerValue = width / (xPointsMax - xPointsMin)
            val heightPerValue = height / (yPointsMax - yPointsMin)
            var currentX = start.x

            drawScope.run {
                for (line in lines.points.indices) {
                    if (line == lines.points.size - 1) break

                    val nextValue = lines.points[line + 1].x - lines.points[line].x
                    val toDraw = nextValue * widthPerValue

                    val nextYValue = lines.points[line + 1].y

                    val currentLineVerticalPosition =
                        height - ((lines.points[line].y - yPointsMin) * heightPerValue) + start.y
                    val nextLineVerticalPosition =
                        height - ((nextYValue - yPointsMin) * heightPerValue) + start.y

                    drawLine(
                        brush = SolidColor(Azure),
                        start = Offset(currentX, currentLineVerticalPosition),
                        end = Offset(currentX + toDraw, nextLineVerticalPosition),
                        strokeWidth = 4f
                    )

                    if (line == 0)
                        drawCircle(
                            brush = SolidColor(Azure),
                            center = Offset(currentX, currentLineVerticalPosition),
                            radius = dotRadius.toPx()
                        )

                    drawCircle(
                        brush = SolidColor(Azure),
                        center = Offset(currentX + toDraw, nextLineVerticalPosition),
                        radius = dotRadius.toPx()
                    )
                    currentX += toDraw
                }
            }
        }
    }
}