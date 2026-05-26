package tech.soc.soar.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedAmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) {
        Color.Black
    } else {
        Color.White
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        AmbientOrbs(
            isDarkTheme = isDarkTheme,
            backgroundColor = backgroundColor
        )

        content()
    }
}

@Composable
private fun AmbientOrbs(
    isDarkTheme: Boolean,
    backgroundColor: Color
) {
    val transition = rememberInfiniteTransition(
        label = "ambient_background_transition"
    )

    val blueProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 13000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blue_orb_progress"
    )

    val redProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 16000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "red_orb_progress"
    )

    val secondBlueProgress by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 19000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "second_blue_orb_progress"
    )

    val redTopProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 21000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "red_top_orb_progress"
    )
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .blur(80.dp)
    ) {
        drawRect(
            color = backgroundColor,
            size = size
        )

        val blueColor = if (isDarkTheme) {
            Color(0xFF4EA8FF).copy(alpha = 0.32f)
        } else {
            Color(0xFF7EC8FF).copy(alpha = 0.42f)
        }

        val redColor = if (isDarkTheme) {
            Color(0xFFFF6B8A).copy(alpha = 0.24f)
        } else {
            Color(0xFFFFA1B5).copy(alpha = 0.34f)
        }

        val softBlueColor = if (isDarkTheme) {
            Color(0xFF2F80ED).copy(alpha = 0.18f)
        } else {
            Color(0xFFBDE7FF).copy(alpha = 0.35f)
        }

        val softRedColor = if (isDarkTheme) {
            Color(0xFFFF4D6D).copy(alpha = 0.16f)
        } else {
            Color(0xFFFFC2CC).copy(alpha = 0.28f)
        }

        drawAmbientOrb(
            center = Offset(
                x = lerp(
                    start = size.width * 0.12f,
                    end = size.width * 0.78f,
                    fraction = blueProgress
                ),
                y = lerp(
                    start = size.height * 0.16f,
                    end = size.height * 0.38f,
                    fraction = blueProgress
                )
            ),
            radius = size.minDimension * lerp(
                start = 0.42f,
                end = 0.56f,
                fraction = blueProgress
            ),
            color = blueColor,
            scale = lerp(
                start = 1.0f,
                end = 1.18f,
                fraction = blueProgress
            )
        )

        drawAmbientOrb(
            center = Offset(
                x = lerp(
                    start = size.width * 0.88f,
                    end = size.width * 0.25f,
                    fraction = redProgress
                ),
                y = lerp(
                    start = size.height * 0.72f,
                    end = size.height * 0.48f,
                    fraction = redProgress
                )
            ),
            radius = size.minDimension * lerp(
                start = 0.36f,
                end = 0.52f,
                fraction = redProgress
            ),
            color = redColor,
            scale = lerp(
                start = 1.0f,
                end = 1.22f,
                fraction = redProgress
            )
        )

        drawAmbientOrb(
            center = Offset(
                x = lerp(
                    start = size.width * 0.18f,
                    end = size.width * 0.64f,
                    fraction = secondBlueProgress
                ),
                y = lerp(
                    start = size.height * 0.88f,
                    end = size.height * 0.66f,
                    fraction = secondBlueProgress
                )
            ),
            radius = size.minDimension * lerp(
                start = 0.28f,
                end = 0.44f,
                fraction = secondBlueProgress
            ),
            color = softBlueColor,
            scale = lerp(
                start = 1.0f,
                end = 1.16f,
                fraction = secondBlueProgress
            )
        )

        drawAmbientOrb(
            center = Offset(
                x = lerp(
                    start = size.width * 0.72f,
                    end = size.width * 0.36f,
                    fraction = redTopProgress
                ),
                y = lerp(
                    start = size.height * 0.08f,
                    end = size.height * 0.28f,
                    fraction = redTopProgress
                )
            ),
            radius = size.minDimension * lerp(
                start = 0.24f,
                end = 0.38f,
                fraction = redTopProgress
            ),
            color = softRedColor,
            scale = lerp(
                start = 1.0f,
                end = 1.14f,
                fraction = redTopProgress
            )
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAmbientOrb(
    center: Offset,
    radius: Float,
    color: Color,
    scale: Float
) {
    scale(
        scale = scale,
        pivot = center
    ) {
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    color,
                    color.copy(alpha = color.alpha * 0.45f),
                    Color.Transparent
                ),
                center = center,
                radius = radius
            ),
            topLeft = Offset(
                x = center.x - radius,
                y = center.y - radius
            ),
            size = Size(
                width = radius * 2f,
                height = radius * 2f
            )
        )
    }
}

private fun lerp(
    start: Float,
    end: Float,
    fraction: Float
): Float {
    return start + (end - start) * fraction
}