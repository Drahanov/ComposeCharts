package com.chartslib.charts.donut.models

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween

enum class DonutAnimationType {
    PART,
    SHADOW
}

data class DonutAnimation(
    val enabled: Boolean = true,
    val transactionSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 1000),
    val animationType: DonutAnimationType = DonutAnimationType.SHADOW,
)