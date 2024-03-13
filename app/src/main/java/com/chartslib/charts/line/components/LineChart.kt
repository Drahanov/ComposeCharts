package com.chartslib.charts.line.components

import android.util.Log
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chartslib.charts.cartesian.components.CartesianSystem
import com.chartslib.charts.cartesian.components.CartesianSystemPreferences
import com.chartslib.charts.cartesian.components.HorizontalLine
import com.chartslib.charts.cartesian.components.LabelSizePreferences
import com.chartslib.charts.cartesian.components.Padding
import com.chartslib.charts.cartesian.components.SizePreferences
import com.chartslib.charts.cartesian.components.VerticalLine
import com.chartslib.charts.line.models.LineModel
import com.chartslib.ui.theme.Azure

@Composable
fun LineChart(
    modifier: Modifier,
    lines: LineModel,
    perValue: Dp = 30.dp,
    dotRadius: Dp = 3.5.dp,
) {
    Log.d("TAGTAG", LocalDensity.current.run { perValue.toPx() }.toString())
    val specificLines = mutableListOf<VerticalLine>()
    var currentPos = 0f
    for (line in 0..lines.points.size - 2) {
        val pxValue = LocalDensity.current.run { perValue.toPx() }
        val nextX = currentPos + lines.points[line + 1].x - lines.points[line].x
        currentPos = nextX
        specificLines.add(VerticalLine(position = nextX * pxValue, lineBrush = SolidColor(Color.Red)))
    }

    val cartesianSystemPreferences = CartesianSystemPreferences(
        horizontalLines = HorizontalLine.Builder().setSteps(3).build(),
        verticalLines = VerticalLine.Builder().setSteps(3)
            .setSpecificLines(specificLines)
            .setLabels { it.toString() }.build(),
        sizePreferences = SizePreferences.SpecificSize((lines.points.maxOf { it.x } * perValue.value).dp)
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