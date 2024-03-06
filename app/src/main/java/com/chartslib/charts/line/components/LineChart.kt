package com.chartslib.charts.line.components

import android.icu.text.ListFormatter.Width
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.chartslib.charts.bar.models.Axis
import com.chartslib.charts.cartesian.components.CartesianSystem
import com.chartslib.charts.cartesian.components.CartesianSystemPreferences
import com.chartslib.charts.cartesian.components.HorizontalLine
import com.chartslib.charts.cartesian.components.LabelSizePreferences
import com.chartslib.charts.cartesian.components.VerticalLine
import com.chartslib.charts.line.models.LineModel

@Composable
fun LineChart(
    modifier: Modifier,
    lines: LineModel,
    horizontalLines: List<HorizontalLine> = HorizontalLine.Builder().setSteps(5)
        .setLabels { it.toString() }.build()
//    verticalLabelsPreferences: LabelSizePreferences,
//    horizontalLabelsPreferences: LabelSizePreferences,
//    axisX: Axis = Axis(
//        steps = lines.points.size,
//        label = { it.toString() },
//        maxValue = lines.points.maxOf { it.x }
//    ),
//    axisY: Axis = Axis(
//        steps = lines.points.size,
//        maxValue = lines.points.maxOf { it.y },
//        label = { it.toString() }
//    ),
) {
//    val horizontalLines = HorizontalLine.Builder().setSteps(2).build()
    val verticalLines = VerticalLine.Builder().setSteps(2).build()

    Box(modifier = modifier) {
        CartesianSystem(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            cartesianSysPrefs = CartesianSystemPreferences(horizontalLines, verticalLines)
        ) { start, width, height, drawScope ->

            val xPointsMax = lines.points.maxOf { it.x }
            val yPointsMax = lines.points.maxOf { it.y }

            val xPointsMin = lines.points.minOf { it.x }
            val yPointsMin = lines.points.minOf { it.y }

            val widthPerValue = width / (xPointsMax - xPointsMin)
            val heightPerValue = height / (yPointsMax - yPointsMin)
            Log.d("HELLOD", widthPerValue.toString())
            Log.d("HELLOD", width.toString())

            var currentX = start.x

            drawScope.run {
                for (line in lines.points.indices) {
                    val nextValue = if (line != lines.points.size - 1) lines.points[line + 1].x - lines.points[line].x else 0f
                    val toDraw = nextValue * widthPerValue
                    drawLine(
                        brush = SolidColor(Color.Red),
                        start = Offset(currentX, 0f),
                        end = Offset(currentX + toDraw , 0f)
                    )

//                    drawRect(
//                        brush = SolidColor(Color.Red),
//                        topLeft = Offset(currentX, start.y),
//                        size = Size(height = 10f, width = toDraw
//                        ))
                    Log.d("TODRAW", (lines.points[line].y * heightPerValue).toString())
                    currentX = currentX + toDraw

                }

            }
        }
    }
}