package com.chartslib.charts.bar.models

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

/**
 * @param steps how many steps have this axis (x or y).
 * @param maxValue max value used for correct display of the columns.
 * @param label text under column in x axis or left text for y.
 * @param labelStyle stale of all labels
 * @param measuringLines lines behind columns. Can be used as supportive views or for design reasons
 */
data class Axis(
    val steps: Int,
    val maxValue: Float,
    val label: (Int) -> String,
    val labelStyle: TextStyle = TextStyle(fontSize = 8.sp),
    val measuringLines: MeasuringLines = MeasuringLines(steps = steps)
)