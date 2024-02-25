package com.chartslib

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun pxToDp(px: Float): Dp {
    val density = LocalDensity.current.density
    return (px / density).dp
}