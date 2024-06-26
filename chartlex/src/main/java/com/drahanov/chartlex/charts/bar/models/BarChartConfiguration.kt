package com.drahanov.chartlex.charts.bar.models

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @param modifier modifier.
 * @param columns bars models.
 * @param axisX axis x configurations.
 * @param axisX axis Y configurations.
 * @param paddingBetweenColumns padding between columns in percent of column.
 * @param shouldMatchWidth if true will place all bars into composable width, else will check minColumnWidth parameter.
 * @param minColumnWidth minimal width of one column, if all columns with padding can't match composable width chart will become
 * scrollable.
 * @param maxColumnWidth maximal width of one bar.
 * @param showIndicator should show indicator if chart scrollable.
 * @param indicatorColor indicator style params.
 * @param indicatorWidth indicator width.
 * @param onSelected callback when bar pressed, returns bar id.
 */
data class BarChartConfiguration(
    val modifier: Modifier = Modifier,
    val columns: List<BarColumnModel>,
    val axisX: Axis = Axis(
        steps = columns.size,
        label = { it.toString() },
        maxValue = columns.size.toFloat()
    ),
    val axisY: Axis = Axis(
        steps = 5,
        maxValue = columns.maxOf { it.value },
        label = { it.toString() }),
    val paddingBetweenColumns: Float = .1f,
    val shouldMatchWidth: Boolean = true,
    val indicatorColor: Brush = SolidColor(Color.Gray),
    val showIndicator: Boolean = true,
    val indicatorWidth: Dp = 3.dp,
    var minColumnWidth: Dp = 20.dp,
    var maxColumnWidth: Dp = 50.dp,
    val onSelected: (Long) -> Unit = {}
)