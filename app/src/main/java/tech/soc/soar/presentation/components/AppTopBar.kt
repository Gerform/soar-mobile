package tech.soc.soar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppTopBar(
    isHomeClickable: Boolean = false,
    showLogout: Boolean = false,
    isLoading: Boolean = false,
    onHomeClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null
) {
    val isDarkTheme = isSystemInDarkTheme()

    val topBarBackground = if (isDarkTheme) {
        Color(0xFF050B18).copy(alpha = 0.82f)
    } else {
        Color(0xFFDCEEFF).copy(alpha = 0.74f)
    }

    val dividerColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.10f)
    } else {
        Color(0xFF0D47A1).copy(alpha = 0.12f)
    }

    val contentColor = if (isDarkTheme) {
        Color.White
    } else {
        Color(0xFF082B66)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                )
            ),
        color = topBarBackground,
        contentColor = contentColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarTextAction(
                    text = "SOAR",
                    enabled = isHomeClickable && !isLoading,
                    onClick = {
                        onHomeClick?.invoke()
                    },
                    isTitle = true,
                    contentColor = contentColor
                )

                Spacer(modifier = Modifier.weight(1f))

                if (showLogout) {
                    TopBarTextAction(
                        text = "Logout",
                        enabled = !isLoading,
                        onClick = {
                            onLogoutClick?.invoke()
                        },
                        isTitle = false,
                        contentColor = contentColor
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.7.dp)
                    .background(dividerColor)
            )
        }
    }
}

@Composable
private fun TopBarTextAction(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    isTitle: Boolean,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .then(
                if (enabled) {
                    Modifier.clickable {
                        onClick()
                    }
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = 10.dp,
                vertical = 8.dp
            )
    ) {
        Text(
            text = text,
            style = if (isTitle) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.bodyLarge
            },
            color = if (enabled || isTitle) {
                contentColor
            } else {
                contentColor.copy(alpha = 0.55f)
            },
            fontWeight = if (isTitle) {
                FontWeight.Normal
            } else {
                FontWeight.SemiBold
            }
        )
    }
}