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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.draw.clip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tech.soc.soar.presentation.components.AppScreenScaffold
import tech.soc.soar.shared.domain.alert.model.AlertItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
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
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            if (state.fromCache) {
                Text(
                    text = "Offline data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (state.error != null && state.alerts.isNotEmpty()) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = {
                    onEvent(SpaceEvent.RefreshTriggered)
                },
                modifier = Modifier.weight(1f)
            ) {
                when {
                    state.isLoading && state.alerts.isEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 120.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    state.error != null && state.alerts.isEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 120.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Text(
                                    text = state.error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    state.alerts.isEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 120.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Text(
                                    text = "No alerts",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
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
                    }
                }
            }

            if (state.showPagination) {
                PaginationControls(
                    page = state.page,
                    hasNextPage = state.hasNextPage,
                    isLoading = state.isLoading || state.isRefreshing,
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

@Composable
private fun AlertListItem(
    alert: AlertItem,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val containerColor = when {
        !alert.isViewed && isDarkTheme -> MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
        alert.isViewed && isDarkTheme -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)

        !alert.isViewed && !isDarkTheme -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    }

    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
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
                    text = formatAlertDate(alert.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
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

private fun formatAlertDate(rawDate: String): String {
    return try {
        val instant = Instant.parse(rawDate)

        DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
            .format(instant)
    } catch (exception: DateTimeParseException) {
        rawDate
    }
}