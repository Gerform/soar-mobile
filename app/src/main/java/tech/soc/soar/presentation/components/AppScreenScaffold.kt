package tech.soc.soar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                .navigationBarsPadding(),
            content = content
        )
    }
}