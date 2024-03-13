package com.chartslib.charts.line.models

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chartslib.charts.bar.models.Axis

data class LineChartConfiguration (
    val lines: LineModel,
    val axisX: Axis = Axis(
        steps = lines.points.size,
        label = { it.toString() },
        maxValue = lines.points.maxOf { it.x }
    ),
    val axisY: Axis = Axis(
        steps = lines.points.size,
        maxValue = lines.points.maxOf { it.y },
        label = { it.toString() }),
    val shouldMatchWidth: Boolean = true,
    val indicatorColor: Brush = SolidColor(Color.Gray),
    val showIndicator: Boolean = true,
    val indicatorWidth: Dp = 3.dp,
    val onSelected: (Long) -> Unit = {}
)

data class Point(
    val x: Float,
    val y: Float
)