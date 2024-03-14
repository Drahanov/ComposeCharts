package com.chartslib.charts.line.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chartslib.charts.cartesian.components.CartesianSystem
import com.chartslib.charts.cartesian.components.CartesianSystemPreferences
import com.chartslib.charts.cartesian.components.HorizontalLine
import com.chartslib.charts.cartesian.components.SizePreferences
import com.chartslib.charts.cartesian.components.VerticalLine
import com.chartslib.charts.line.models.LineModel
import com.chartslib.ui.theme.Azure

sealed class LineChartWidth {
    data object MatchParent : LineChartWidth()
    data class DpPerValue(val dp: Dp) : LineChartWidth()
}

@Composable
fun LineChart(
    modifier: Modifier,
    lines: LineModel,
    lineChartSizePreferences: LineChartWidth = LineChartWidth.MatchParent,
    dotRadius: Dp = 0.dp,
) {
    val max = lines.points.maxOf { it.x }
    val specificLines = mutableListOf<VerticalLine>()
    for (line in 1..lines.points.size - 2) {
        val percent = (lines.points[line].x / max) * 100
        specificLines.add(VerticalLine(positionInPercentage = percent))
    }

    val cartesianSystemPreferences = CartesianSystemPreferences(
        horizontalLines = HorizontalLine.Builder().setSteps(3).build(),
        verticalLines = VerticalLine.Builder().setSteps(2)
            .setSpecificLines(specificLines)
            .setLabels { it.toString() }.build(),
        sizePreferences =
        if (lineChartSizePreferences is LineChartWidth.DpPerValue) {
            SizePreferences.SpecificSize((lines.points.maxOf { it.x } * lineChartSizePreferences.dp.value).dp)
        }

        else SizePreferences.FixedToWidth
    )

    Box(modifier = modifier) {
        CartesianSystem(
            modifier = Modifier
                .fillMaxSize(),
            cartesianSysPrefs = cartesianSystemPreferences,
        ) { start, width, height, drawScope ->
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