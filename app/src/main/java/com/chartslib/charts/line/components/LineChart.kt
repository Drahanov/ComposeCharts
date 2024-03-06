package com.chartslib.charts.line.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
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
    horizontalLines: List<HorizontalLine> = HorizontalLine.Builder().setSteps(5).setLabels { it.toString() }.build()
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
            modifier = Modifier.fillMaxSize().clipToBounds(),
            cartesianSysPrefs = CartesianSystemPreferences(horizontalLines, verticalLines)
        ) { start, width, height, drawScope ->

            val xPointsMax = lines.points.maxOf { it.x }
            val yPointsMax = lines.points.maxOf { it.y }

            val xPointsMin = lines.points.minOf { it.x }
            val yPointsMin = lines.points.minOf { it.y }

            val widthPerColumn = width / (xPointsMax - xPointsMin)

            drawScope.run {
                drawLine()
            }
        }
    }
}