package com.drahanov.chartlex.charts.bar.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import com.drahanov.chartlex.pxToDp

@Composable
fun ScrollIndicator(
    indicatorWidth: Float,
    position: Float,
    indicatorHeight: Dp,
    scrollIndicatorBrush: Brush,
    labelHeight: Float,
    startX: Float
) {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = pxToDp(px = startX))
            .clipToBounds()
    ) {
        val height = size.height - labelHeight
        val width = size.width - startX
        val res = width / 100 * (width / indicatorWidth * 100)

        translate(left = position / 100 * (width / indicatorWidth * 100) * -1) {
            drawRoundRect(
                topLeft = Offset(x = 0f, y = height - indicatorHeight.toPx()),
                brush = scrollIndicatorBrush,
                size = Size(height = indicatorHeight.toPx(), width = res),
                cornerRadius = CornerRadius(5f),
            )
        }
    }
}
