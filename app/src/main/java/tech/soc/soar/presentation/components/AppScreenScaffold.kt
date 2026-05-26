package tech.soc.soar.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppScreenScaffold(
    isHomeClickable: Boolean = false,
    showLogout: Boolean = false,
    isLoading: Boolean = false,
    onHomeClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    AnimatedAmbientBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppTopBar(
                isHomeClickable = isHomeClickable,
                showLogout = showLogout,
                isLoading = isLoading,
                onHomeClick = onHomeClick,
                onLogoutClick = onLogoutClick
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                        )
                    ),
                content = content
            )
        }
    }
}