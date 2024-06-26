package com.drahanov.chartlex.charts.line.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.drahanov.chartlex.charts.cartesian.components.Cartesian
import com.drahanov.chartlex.charts.cartesian.components.Frame
import com.drahanov.chartlex.charts.cartesian.components.HorizontalLine
import com.drahanov.chartlex.charts.cartesian.components.InitialGridLines
import com.drahanov.chartlex.charts.cartesian.components.SizePreferences
import com.drahanov.chartlex.charts.cartesian.components.VerticalLine
import com.drahanov.chartlex.charts.line.models.LineModel
import com.drahanov.chartlex.charts.line.models.LineType

sealed class LineChartWidth {
    data object MatchParent : LineChartWidth()
    data class DpPerValue(val dp: Dp) : LineChartWidth()
}

@Composable
fun LineChart(
    modifier: Modifier,
    lines: List<LineModel>,
    lineChartSizePreferences: LineChartWidth = LineChartWidth.DpPerValue(100.dp),
    dotRadius: Dp = 3.dp,
    frame: Frame = Frame(),
    horizontalGridLines: List<HorizontalLine> = emptyList(),
    verticalGridLines: List<VerticalLine> = emptyList()
) {
    val chartSize = if (lineChartSizePreferences is LineChartWidth.DpPerValue) {
        SizePreferences.SpecificSize((lines.maxOf { it.points.maxOf { it.x } } * lineChartSizePreferences.dp.value).dp)
    } else SizePreferences.FixedToWidth

    val initialFrameLines = InitialGridLines(
        top = HorizontalLine(
            isLineVisible = frame.top.isLineVisible,
            lineBrush = frame.top.lineBrush,
            lineThickness = frame.top.lineThickness,
            lineStyle = frame.top.lineStyle,
            isMovable = false
        ),
        bottom = HorizontalLine(
            isLineVisible = frame.bottom.isLineVisible,
            lineBrush = frame.bottom.lineBrush,
            lineThickness = frame.bottom.lineThickness,
            lineStyle = frame.bottom.lineStyle,
            isMovable = false
        ),
        start = VerticalLine(
            isLineVisible = frame.start.isLineVisible,
            lineBrush = frame.start.lineBrush,
            lineThickness = frame.start.lineThickness,
            lineStyle = frame.start.lineStyle,
            isMovable = false
        ),
        end = VerticalLine(
            isLineVisible = frame.end.isLineVisible,
            lineBrush = frame.end.lineBrush,
            lineThickness = frame.end.lineThickness,
            lineStyle = frame.end.lineStyle,
            isMovable = false
        )
    )
    Box(modifier = modifier) {
        Cartesian(
            modifier = Modifier.fillMaxSize(),
            horizontalLines = horizontalGridLines,
            verticalLines = verticalGridLines,
            sizePreferences = chartSize,
            initialGridLines = initialFrameLines
        ) { start, width, height, drawScope ->

            val xPointsMax = lines.maxOf { it.points.maxOf { it.x } }
            val yPointsMax = lines.maxOf { it.points.maxOf { it.y } }

            val xPointsMin = lines.minOf { it.points.minOf { it.x } }
            val yPointsMin = lines.minOf { it.points.minOf { it.y } }

            for (line in lines) {
                val widthPerValue = width / (xPointsMax - xPointsMin)
                val heightPerValue = height / (yPointsMax - yPointsMin)
                var currentX = start.x + line.points[0].x * widthPerValue

                drawScope.run {
                    if (line.lineType == LineType.STRAIGHT)
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
                                brush = SolidColor(line.color),
                                start = Offset(currentX, currentLineVerticalPosition),
                                end = Offset(currentX + toDraw, nextLineVerticalPosition),
                                strokeWidth = 4f
                            )

                            drawCircle(
                                brush = SolidColor(line.color),
                                center = Offset(currentX, currentLineVerticalPosition),
                                radius = dotRadius.toPx()
                            )

                            drawCircle(
                                brush = SolidColor(line.color),
                                center = Offset(currentX + toDraw, nextLineVerticalPosition),
                                radius = dotRadius.toPx()
                            )

                            currentX += toDraw
                        }
                    else {
                        val path = Path().apply {
                            moveTo(
                                line.points.first().x * widthPerValue + start.x,
                                height - (line.points.first().y - yPointsMin) * heightPerValue + start.y
                            )
                        }

                        for (index in line.points.indices) {
                            if (index == 0) continue
                            val curX = line.points[index].x * widthPerValue + start.x
                            val curY =
                                height - (line.points[index].y - yPointsMin) * heightPerValue + start.y

                            val prevX = line.points[index - 1].x * widthPerValue + start.x
                            val prevY =
                                height - (line.points[index - 1].y - yPointsMin) * heightPerValue + start.y

                            val connPoint1 = Offset((curX + prevX) / 2, prevY)
                            val connPoint2 = Offset((curX + prevX) / 2, curY)
                            drawCircle(
                                brush = SolidColor(line.color),
                                center = Offset(curX, curY),
                                radius = dotRadius.toPx()
                            )
                            path.cubicTo(
                                connPoint1.x,
                                connPoint1.y,
                                connPoint2.x,
                                connPoint2.y,
                                curX,
                                curY
                            )

                        }
                        drawPath(
                            path, line.color, style = Stroke(width = 5f)
                        )

                    }
                }
            }

        }
    }
}