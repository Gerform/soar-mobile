package tech.soc.soar.presentation.alertdetails

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
fun AlertDetailsScreen(
    alertId: Long,
    onHomeClick: () -> Unit
) {
    AppScreenScaffold(
        isHomeClickable = true,
        showLogout = false,
        onHomeClick = onHomeClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Alert #$alertId",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}