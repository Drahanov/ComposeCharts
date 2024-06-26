package com.drahanov.chartlex.charts.donut.models

import androidx.compose.ui.graphics.Color
import java.io.Serializable
import java.util.UUID

data class DonutSegmentModel(
    val value: Int,
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val isSelected: Boolean = false,
    val color: Color = Color.Unspecified
): Serializable