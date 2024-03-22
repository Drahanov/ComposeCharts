package com.chartslib.charts.line.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chartslib.charts.cartesian.components.CartesianSystem
import com.chartslib.charts.cartesian.components.CartesianSystemPreferences
import com.chartslib.charts.cartesian.components.HorizontalLine
import com.chartslib.charts.cartesian.components.SizePreferences
import com.chartslib.charts.cartesian.components.VerticalLine
import com.chartslib.charts.line.models.LineModel
import com.chartslib.charts.line.models.Point
import com.chartslib.ui.theme.Azure

sealed class LineChartWidth {
    data object MatchParent : LineChartWidth()
    data class DpPerValue(val dp: Dp) : LineChartWidth()
}

@Composable
fun LineChart(
    modifier: Modifier,
    lines: List<LineModel>,
    lineChartSizePreferences: LineChartWidth = LineChartWidth.MatchParent,
    dotRadius: Dp = 3.dp,
    cartesianSystemPreferences: CartesianSystemPreferences = CartesianSystemPreferences(
        horizontalLines = HorizontalLine.Builder().setSteps(3).build(),
        verticalLines = VerticalLine.Builder().setUnspecifiedLinesAmount(3)
            .setSpecifiedLinesAmount(lines.sumOf { it.points.size }) { index ->
                val list = lines.map { it.points }
                val points = mutableListOf<Point>()
                for (i in list) {
                    points.addAll(i)
                }

                VerticalLine(
                    lineBrush = SolidColor(Color.Red),
                    positionInPercentage = (points[index].x / points.maxOf { it.x }) * 100
                )
            }
            .setLabels { it.toString() }.build(),
        sizePreferences = if (lineChartSizePreferences is LineChartWidth.DpPerValue) {
            SizePreferences.SpecificSize((lines.maxOf { it.points.maxOf { it.x } } * lineChartSizePreferences.dp.value).dp)
        } else SizePreferences.FixedToWidth
    ),
) {
    Box(modifier = modifier.clipToBounds()) {
        CartesianSystem(
            modifier = Modifier
                .fillMaxSize(),
            cartesianSysPrefs = cartesianSystemPreferences,
        ) { start, width, height, drawScope ->
            val xPointsMax = lines.maxOf { it.points.maxOf { it.x } }
            val yPointsMax = lines.maxOf { it.points.maxOf { it.y } }

            val xPointsMin =  lines.minOf { it.points.minOf { it.x } }
            val yPointsMin =  lines.minOf { it.points.minOf { it.y } }

            for (line in lines) {

                val widthPerValue = width / (xPointsMax - xPointsMin)
                val heightPerValue = height / (yPointsMax - yPointsMin)
                var currentX = start.x + line.points[0].x * widthPerValue

                drawScope.run {
                    for (index in line.points.indices) {
                        if (index == line.points.size - 1) break

                        val nextValue = line.points[index + 1].x - line.points[index].x
                        val toDraw = nextValue * widthPerValue

                        val nextYValue = line.points[index + 1].y

                        val currentLineVerticalPosition =
                            height - ((line.points[index].y - yPointsMin) * heightPerValue) + start.y
                        val nextLineVerticalPosition =
                            height - ((nextYValue - yPointsMin) * heightPerValue) + start.y

                        drawLine(
                            brush = SolidColor(Azure),
                            start = Offset(currentX, currentLineVerticalPosition),
                            end = Offset(currentX + toDraw, nextLineVerticalPosition),
                            strokeWidth = 4f
                        )

                        if (index == 0)
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
}