package com.drahanov.chartlex.charts.donut.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.drahanov.chartlex.charts.donut.models.DonutAnimation
import com.drahanov.chartlex.charts.donut.models.DonutAnimationType
import com.drahanov.chartlex.charts.donut.models.DonutSegmentModel
import com.drahanov.chartlex.makeColorTransparent
import java.util.Random
import kotlin.math.PI
import kotlin.math.atan2


@Composable
fun DonutChart(
    modifier: Modifier,
    segments: List<DonutSegmentModel>,
    animation: DonutAnimation = DonutAnimation(),
    onSegmentSelected: (Long) -> Unit
) {

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        var startAngle = 0f
        val anglePerValue = 360f / segments.sumOf { it.value }

        for (segment in segments) {
            val angleToDraw = segment.value * anglePerValue

            DonutSegment(
                isSelected = segment.isSelected,
                currentStartAngle = startAngle,
                angleToDraw = angleToDraw,
                animation = animation,
                segmentColor = segment.color
            )
            startAngle += angleToDraw
        }

        DonutChartGestureDetector(segments) {
            onSegmentSelected.invoke(it)
        }
    }
}

@Composable
fun DonutSegment(
    isSelected: Boolean,
    currentStartAngle: Float,
    angleToDraw: Float,
    animation: DonutAnimation,
    segmentColor: Color
) {
    val transition =
        updateTransition(targetState = isSelected && animation.enabled, label = "")
    val scaleValue = transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                animation.transactionSpec
            } else {
                animation.transactionSpec
            }
        },
        label = ""
    ) { targetState ->
        if (targetState && animation.animationType == DonutAnimationType.PART) 1.1f else 1f
    }

    val scaleTopShadowValue = transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                animation.transactionSpec
            } else {
                animation.transactionSpec
            }
        },
        label = ""
    ) { targetState ->
        if (targetState && animation.animationType == DonutAnimationType.SHADOW) 1.1f else 1f
    }

    val scaleBottomShadowValue = transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                animation.transactionSpec
            } else {
                animation.transactionSpec
            }
        },
        label = ""
    ) { targetState ->
        if (targetState && animation.animationType == DonutAnimationType.SHADOW) 0.85f else 1f
    }

    val rnd = Random()
    val color = rememberSaveable {
        mutableStateOf(
            android.graphics.Color.argb(
                255,
                rnd.nextInt(256),
                rnd.nextInt(256),
                rnd.nextInt(256)
            )
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val radius = if (width > height) height / 3 else width / 3
        val circleThickness = radius / 2f

        val color = if (segmentColor == Color.Unspecified) Color(color.value) else segmentColor
        scale(scaleTopShadowValue.value) {
            drawArc(
                style = Stroke(width = circleThickness - (circleThickness / 100 * 10)),
                color = Color(makeColorTransparent(color.toArgb().toLong(), 50)),
                startAngle = currentStartAngle,
                sweepAngle = angleToDraw,
                useCenter = false,
                size = Size(width = radius * 2f, height = radius * 2f),
                topLeft = Offset((width - radius * 2f) / 2f, (height - radius * 2f) / 2f)
            )
        }
        scale(scaleBottomShadowValue.value) {
            drawArc(
                style = Stroke(width = circleThickness - (circleThickness / 100 * 10)),
                color = Color(makeColorTransparent(color.toArgb().toLong(), 50)),
                startAngle = currentStartAngle,
                sweepAngle = angleToDraw,
                useCenter = false,
                size = Size(width = radius * 2f, height = radius * 2f),
                topLeft = Offset((width - radius * 2f) / 2f, (height - radius * 2f) / 2f)
            )
        }

        scale(scaleValue.value) {
            drawArc(
                style = Stroke(width = circleThickness - (circleThickness / 100 * 10)),
                color = color,
                startAngle = currentStartAngle,
                sweepAngle = angleToDraw,
                useCenter = false,
                size = Size(width = radius * 2f, height = radius * 2f),
                topLeft = Offset((width - radius * 2f) / 2f, (height - radius * 2f) / 2f)
            )
        }
    }
}

@Composable
fun DonutChartGestureDetector(segments: List<DonutSegmentModel>, onSegmentSelected: (Long) -> Unit) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(true) {
                detectTapGestures(
                    onTap = { offset ->
                        val circleCenter = Offset(x = size.width / 2f, y = size.height / 2f)

                        val tapAngleInDegrees =
                            (-atan2(
                                x = circleCenter.y - offset.y,
                                y = circleCenter.x - offset.x
                            ) * (180f / PI).toFloat() - 90f).mod(360f)

                        val anglePerValue = 360f / segments.sumOf {
                            it.value
                        }
                        var currAngle = 0f

                        segments.forEachIndexed { index, pieChartInput ->
                            currAngle += pieChartInput.value * anglePerValue
                            if (tapAngleInDegrees < currAngle) {
                                onSegmentSelected.invoke(segments[index].id)
                                return@detectTapGestures
                            }
                        }

                    }
                )
            }
    ) {}
}