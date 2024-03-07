package com.chartslib.charts.line.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chartslib.charts.cartesian.components.CartesianSystem
import com.chartslib.charts.cartesian.components.CartesianSystemPreferences
import com.chartslib.charts.cartesian.components.HorizontalLine
import com.chartslib.charts.cartesian.components.VerticalLine
import com.chartslib.charts.line.models.LineModel
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
    Box(modifier = modifier) {
        CartesianSystem(
            modifier = Modifier
                .fillMaxSize(),
            cartesianSysPrefs = cartesianSystemPreferences
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
                        drawCircle(brush = SolidColor(Azure), center = Offset(currentX, currentLineVerticalPosition), radius = dotRadius.toPx())

                    drawCircle(brush = SolidColor(Azure), center = Offset(currentX + toDraw, nextLineVerticalPosition), radius = dotRadius.toPx())
                    currentX += toDraw
                }

            }
        }
    }
}