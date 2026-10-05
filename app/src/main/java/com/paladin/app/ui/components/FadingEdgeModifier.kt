package com.paladin.app.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Optically fades out content as it approaches the bottom edge of the container,
 * preventing harsh visual cuts when items scroll towards the boundary.
 */
fun Modifier.fadingBottomEdge(
    fadeHeight: Dp = 16.dp
): Modifier = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        val fadePx = fadeHeight.toPx()
        val height = size.height
        if (fadePx > 0f && height > fadePx) {
            val stopAt = (1f - (fadePx / height)).coerceIn(0f, 1f)
            drawRect(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color.Black,
                        stopAt to Color.Black,
                        1.0f to Color.Transparent
                    ),
                    startY = 0f,
                    endY = height
                ),
                blendMode = BlendMode.DstIn
            )
        }
    }
