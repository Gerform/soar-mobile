package tech.soc.soar.presentation.space

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tech.soc.soar.presentation.components.AppScreenScaffold

@Composable
fun SpaceScreen(
    spaceName: String,
    state: SpaceUiState,
    onEvent: (SpaceEvent) -> Unit
) {
    AppScreenScaffold(
        isHomeClickable = true,
        showLogout = true,
        isLoading = state.isLoading,
        onHomeClick = {
            onEvent(SpaceEvent.HomeClicked)
        },
        onLogoutClick = {
            onEvent(SpaceEvent.LogoutClicked)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = spaceName,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}