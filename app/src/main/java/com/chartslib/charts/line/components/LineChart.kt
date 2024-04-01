package com.chartslib.charts.line.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chartslib.charts.cartesian.components.Cartesian
import com.chartslib.charts.cartesian.components.CartesianSystem
import com.chartslib.charts.cartesian.components.CartesianSystemPreferences
import com.chartslib.charts.cartesian.components.HorizontalLine
import com.chartslib.charts.cartesian.components.Padding
import com.chartslib.charts.cartesian.components.SizePreferences
import com.chartslib.charts.cartesian.components.VerticalLine
import com.chartslib.charts.line.models.LineModel
import com.chartslib.charts.line.models.LineType
import com.chartslib.charts.line.models.Point

sealed class LineChartWidth {
    data object MatchParent : LineChartWidth()
    data class DpPerValue(val dp: Dp) : LineChartWidth()
}

@Composable
fun LineChart(
    modifier: Modifier,
    lines: List<LineModel>,
    lineChartSizePreferences: LineChartWidth = LineChartWidth.DpPerValue(2000.dp),
    dotRadius: Dp = 3.dp,
    cartesianSystemPreferences: CartesianSystemPreferences = CartesianSystemPreferences(
        horizontalLines = HorizontalLine.Builder().setSteps(3).build(),
        verticalLines = VerticalLine.Builder().setUnspecifiedLinesAmount(2)
            .setSpecifiedLinesAmount(lines.sumOf { it.points.size }) { index ->
                val list = lines.map { it.points }
                val points = mutableListOf<Point>()
                for (i in list) {
                    points.addAll(i)
                }

                VerticalLine(
                    lineBrush = SolidColor(Color.LightGray),
                    lineThickness = 0.5.dp,
                    label = "H",
                    positionInPercentage = (points[index].x / points.maxOf { it.x }) * 100
                )
            }
            .setLabels { it.toString() }.build(),
        sizePreferences = if (lineChartSizePreferences is LineChartWidth.DpPerValue) {
            SizePreferences.SpecificSize((lines.maxOf { it.points.maxOf { it.x } } * lineChartSizePreferences.dp.value).dp)
        } else SizePreferences.FixedToWidth,
        horizontalExtraPadding = Padding(
            top = dotRadius,
            bottom = dotRadius,
            start = dotRadius,
            end = dotRadius
        ),
        verticalExtraPadding = Padding(
            top = dotRadius,
            bottom = dotRadius,
            start = dotRadius,
            end = dotRadius
        )
    ),
) {
    Box(modifier = modifier) {
        Cartesian(
            modifier = Modifier.fillMaxSize(),
            horizontalLines = cartesianSystemPreferences.horizontalLines,
            verticalLines = cartesianSystemPreferences.verticalLines,
            sizePreferences = SizePreferences.FixedToWidth
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
                            moveTo(line.points.first().x * widthPerValue + start.x,  height - (line.points.first().y - yPointsMin) * heightPerValue + start.y)
                        }

                        for (index in line.points.indices) {
                            if (index == 0) continue
                            val curX = line.points[index].x * widthPerValue + start.x
                            val curY = height - (line.points[index].y - yPointsMin) * heightPerValue + start.y

                            val prevX = line.points[index - 1].x * widthPerValue + start.x
                            val prevY = height - (line.points[index - 1].y - yPointsMin) * heightPerValue + start.y

                            val connPoint1 = Offset((curX + prevX) / 2, prevY)
                            val connPoint2 = Offset((curX + prevX) / 2, curY)
                            drawCircle(
                                brush = SolidColor(line.color),
                                center = Offset(curX, curY),
                                radius = dotRadius.toPx()
                            )
                            path.cubicTo(connPoint1.x, connPoint1.y, connPoint2.x, connPoint2.y, curX, curY)

                        }
                        drawPath(
                            path, line.color, style = Stroke(width = 5f)
                        )

                    }
                }
            }

        }
    }
//    Box(modifier = modifier) {
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            val path = Path().apply {
//                moveTo(0f, 50f)
//                quadraticBezierTo(75f, 0f, 150f, 50f)
//                quadraticBezierTo(225f, 100f, 300f, 50f)
//                quadraticBezierTo(375f, 0f, 450f, 50f)
//            }
//
//            drawPath(
//                path, Color.Black, style = Stroke(width = 5f)
//            )
//        }
//    }
}


@Composable
fun DrawCurvedLine(points: List<Point>) {
    Canvas(modifier = Modifier) {
        if (points.size < 2) return@Canvas

        val path = Path()
        path.moveTo(points.first().x, points.first().y)

        for (i in 1 until points.size) {
            val startPoint = points[i - 1]
            val endPoint = points[i]
            val controlPointX = startPoint.x + (endPoint.x - startPoint.x) / 2
            path.quadraticBezierTo(
                controlPointX,
                startPoint.y,
                endPoint.x,
                endPoint.y
            )
        }

        drawPath(path, Color.Black, style = Stroke(width = 5f))
    }
}

@Composable
fun CurvedLineExample() {
    val points = listOf(
        Point(50f, 50f),
        Point(100f, 150f),
        Point(150f, 100f),
        Point(200f, 200f)
    )

    DrawCurvedLine(points)
}