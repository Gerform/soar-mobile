package tech.soc.soar.presentation.space

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tech.soc.soar.presentation.components.AppScreenScaffold
import tech.soc.soar.shared.domain.alert.model.AlertItem

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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = spaceName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (state.fromCache) {
                Text(
                    text = "Offline data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            when {
                state.isLoading && state.alerts.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.alerts.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No alerts",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = state.alerts,
                            key = { alert -> alert.id }
                        ) { alert ->
                            AlertListItem(
                                alert = alert,
                                onClick = {
                                    onEvent(
                                        SpaceEvent.AlertClicked(alert.id)
                                    )
                                }
                            )
                        }
                    }

                    if (state.showPagination) {
                        PaginationControls(
                            page = state.page,
                            hasNextPage = state.hasNextPage,
                            isLoading = state.isLoading,
                            onPreviousClick = {
                                onEvent(SpaceEvent.PreviousPageClicked)
                            },
                            onNextClick = {
                                onEvent(SpaceEvent.NextPageClicked)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertListItem(
    alert: AlertItem,
    onClick: () -> Unit
) {
    val containerColor = if (alert.isViewed) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = if (alert.isViewed) 1.dp else 4.dp,
        shadowElevation = if (alert.isViewed) 0.dp else 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = alert.reason,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "status: ${alert.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = alert.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PaginationControls(
    page: Int,
    hasNextPage: Boolean,
    isLoading: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onPreviousClick,
            enabled = page > 0 && !isLoading
        ) {
            Text("Previous")
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Page ${page + 1}",
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onNextClick,
            enabled = hasNextPage && !isLoading
        ) {
            Text("Next")
        }
    }
}