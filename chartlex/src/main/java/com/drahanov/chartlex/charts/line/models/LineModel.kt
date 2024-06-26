package com.drahanov.chartlex.charts.line.models

import androidx.compose.ui.graphics.Color
import com.drahanov.chartlex.ui.theme.Azure
import java.util.UUID

data class LineModel (
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val points: List<Point>,
    val color: Color = Azure,
    val lineType: LineType = LineType.STRAIGHT
)

enum class LineType {
    CURVED,
    STRAIGHT
}