package com.chartslib.charts.donut.models

import androidx.compose.ui.graphics.Color
import java.io.Serializable
import java.util.UUID

data class DonutSegmentModel(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val value: Int,
    val isSelected: Boolean = false,
    val color: Color = Color.Unspecified
): Serializable