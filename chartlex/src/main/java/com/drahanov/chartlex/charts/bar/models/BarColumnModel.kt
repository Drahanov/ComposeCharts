package com.drahanov.chartlex.charts.bar.models

import androidx.compose.ui.graphics.Color
import com.drahanov.chartlex.ui.theme.Azure
import java.io.Serializable
import java.util.UUID

/**
 * @param id id of specific column if you need.
 * @param value value of column.
 * @param isSelected can be used to change color or to show in legend.
 * @param color color of this column
 */
data class BarColumnModel(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val value: Float,
    val isSelected: Boolean = false,
    val color: Color = Azure
): Serializable