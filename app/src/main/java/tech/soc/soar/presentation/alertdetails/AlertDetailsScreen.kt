package tech.soc.soar.presentation.alertdetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.soc.soar.presentation.components.AppScreenScaffold
import tech.soc.soar.shared.domain.alert.model.AlertStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailsScreen(
    state: AlertDetailsUiState,
    onEvent: (AlertDetailsEvent) -> Unit
) {
    AppScreenScaffold(
        isHomeClickable = true,
        showLogout = false,
        isLoading = state.isLoading || state.isUpdatingStatus,
        onHomeClick = {
            onEvent(AlertDetailsEvent.HomeClicked)
        }
    ) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {
                onEvent(AlertDetailsEvent.RefreshTriggered)
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                state.isLoading && state.details == null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null && state.details == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                state.details != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        AlertDetailsHeader(
                            onBackClick = {
                                onEvent(AlertDetailsEvent.BackClicked)
                            },
                            onResponsesClick = {
                                onEvent(AlertDetailsEvent.ResponsesClicked)
                            }
                        )

                        if (state.details.fromCache) {
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
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Text(
                            text = state.details.rawBody,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AlertStatusRow(
                            status = state.details.status,
                            date = formatAlertDetailsDate(state.details.date),
                            isUpdating = state.isUpdatingStatus,
                            onStatusSelected = { selectedStatus ->
                                onEvent(
                                    AlertDetailsEvent.StatusSelected(
                                        status = selectedStatus
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertDetailsHeader(
    onBackClick: () -> Unit,
    onResponsesClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = onResponsesClick
        ) {
            Text(
                text = "Responses",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AlertStatusRow(
    status: String,
    date: String,
    isUpdating: Boolean,
    onStatusSelected: (String) -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    val normalizedStatus = status.lowercase().trim()
    val canChangeStatus = normalizedStatus == AlertStatus.NEW && !isUpdating

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .combinedClickable(
                        enabled = canChangeStatus,
                        onClick = {},
                        onLongClick = {
                            menuExpanded = true
                        }
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 6.dp
                    )
            ) {
                Text(
                    text = if (isUpdating) {
                        "status: $status..."
                    } else {
                        "status: $status"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("false positive")
                    },
                    onClick = {
                        menuExpanded = false
                        onStatusSelected(AlertStatus.FALSE_POSITIVE)
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("closed")
                    },
                    onClick = {
                        menuExpanded = false
                        onStatusSelected(AlertStatus.CLOSED)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private fun formatAlertDetailsDate(rawDate: String): String {
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