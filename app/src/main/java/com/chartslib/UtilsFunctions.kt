package com.chartslib

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils


@Composable
fun pxToDp(px: Float): Dp {
    val density = LocalDensity.current.density
    return (px / density).dp
}

fun makeColorTransparent(color: Long, alphaPercentage: Int): Long {
    val colorInt = Color(color).toArgb()
    val alpha = (ColorUtils.setAlphaComponent(colorInt, (255 * alphaPercentage / 100)))
    return alpha.toLong()
}