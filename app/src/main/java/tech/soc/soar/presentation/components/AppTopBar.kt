package tech.soc.soar.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                )
            ),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
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
                isTitle = true
            )

            Spacer(modifier = Modifier.weight(1f))

            if (showLogout) {
                TopBarTextAction(
                    text = "Logout",
                    enabled = !isLoading,
                    onClick = {
                        onLogoutClick?.invoke()
                    },
                    isTitle = false
                )
            }
        }
    }
}

@Composable
private fun TopBarTextAction(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    isTitle: Boolean
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
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = if (isTitle) {
                FontWeight.Normal
            } else {
                FontWeight.SemiBold
            }
        )
    }
}