package tech.soc.soar.presentation.responses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.soc.soar.presentation.components.AppScreenScaffold
import tech.soc.soar.push.PushEvent
import tech.soc.soar.push.PushEventBus
import tech.soc.soar.push.PushForegroundState
import tech.soc.soar.shared.domain.response.model.ResponseDecision
import tech.soc.soar.shared.domain.response.model.ResponseRequest
import tech.soc.soar.shared.domain.response.model.ResponseRequestStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponsesScreen(
    alertId: Long,
    state: ResponsesUiState,
    onEvent: (ResponsesEvent) -> Unit
) {

    val listState = rememberLazyListState()

    DisposableEffect(alertId) {
        PushForegroundState.setOpenedResponses(alertId)

        onDispose {
            PushForegroundState.clearOpenedResponses(alertId)
        }
    }

    LaunchedEffect(alertId) {
        PushEventBus.events.collect { event ->
            when (event) {
                is PushEvent.ResponsesShouldRefresh -> {
                    if (event.alertId == alertId) {
                        onEvent(
                            ResponsesEvent.PushRefreshReceived(
                                scrollToTop = event.scrollToTop
                            )
                        )
                    }
                }

                is PushEvent.SpaceShouldRefresh -> Unit
            }
        }
    }

    LaunchedEffect(state.scrollToTopSignal) {
        if (state.scrollToTopSignal > 0) {
            listState.animateScrollToItem(0)
        }
    }

    AppScreenScaffold(
        isHomeClickable = true,
        showLogout = false,
        isLoading = state.isLoading || state.decidingResponseId != null,
        onHomeClick = {
            onEvent(ResponsesEvent.HomeClicked)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ResponsesHeader(
                onBackClick = {
                    onEvent(ResponsesEvent.BackClicked)
                }
            )

            if (state.fromCache) {
                Text(
                    text = "Offline data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (state.statusMessage != null) {
                Text(
                    text = state.statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (state.error != null && state.responses.isNotEmpty()) {
                Text(
                    text = state.error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = {
                    onEvent(ResponsesEvent.RefreshTriggered)
                },
                modifier = Modifier.weight(1f)
            ) {
                when {
                    state.isLoading && state.responses.isEmpty() -> {
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

                    state.error != null && state.responses.isEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 120.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Text(
                                    text = state.error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    state.responses.isEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 120.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Text(
                                    text = "No responses",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = state.responses,
                                key = { response -> response.id }
                            ) { response ->
                                ResponseRequestCard(
                                    response = response,
                                    canDecideResponses = state.canDecideResponses,
                                    isDeciding = state.decidingResponseId == response.id,
                                    onApproveClick = {
                                        onEvent(
                                            ResponsesEvent.DecisionSelected(
                                                responseRequestId = response.id,
                                                decision = ResponseDecision.APPROVED
                                            )
                                        )
                                    },
                                    onRejectClick = {
                                        onEvent(
                                            ResponsesEvent.DecisionSelected(
                                                responseRequestId = response.id,
                                                decision = ResponseDecision.REJECTED
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (state.showPagination) {
                ResponsesPaginationControls(
                    page = state.page,
                    hasNextPage = state.hasNextPage,
                    isLoading = state.isLoading || state.isRefreshing || state.decidingResponseId != null,
                    onPreviousClick = {
                        onEvent(ResponsesEvent.PreviousPageClicked)
                    },
                    onNextClick = {
                        onEvent(ResponsesEvent.NextPageClicked)
                    }
                )
            }
        }
    }
}

@Composable
private fun ResponsesHeader(
    onBackClick: () -> Unit
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

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Responses",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ResponseRequestCard(
    response: ResponseRequest,
    canDecideResponses: Boolean,
    isDeciding: Boolean,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    val showDecisionActions =
        canDecideResponses &&
                response.status.lowercase().trim() == ResponseRequestStatus.CONFIRMATION

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = response.actionName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = response.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ResponseInfoLine(
                label = "Target",
                value = response.targetValue
            )

            ResponseInfoLine(
                label = "Message",
                value = response.message
            )

            ResponseInfoLine(
                label = "Created by",
                value = response.createdByUser
            )

            ResponseInfoLine(
                label = "Space",
                value = response.spaceName
            )

            Spacer(modifier = Modifier.height(12.dp))

            ResponseDatesRow(
                createdAt = response.createdAt,
                updatedAt = response.updatedAt
            )

            if (showDecisionActions) {
                Spacer(modifier = Modifier.height(10.dp))

                ResponseDecisionActions(
                    isDeciding = isDeciding,
                    onApproveClick = onApproveClick,
                    onRejectClick = onRejectClick
                )
            }
        }
    }
}

@Composable
private fun ResponseInfoLine(
    label: String,
    value: String
) {
    if (value.isBlank()) {
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ResponseDatesRow(
    createdAt: String,
    updatedAt: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Created",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = formatResponseDate(createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Updated",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End
            )

            Text(
                text = formatResponseDate(updatedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun ResponseDecisionActions(
    isDeciding: Boolean,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            enabled = !isDeciding,
            onClick = onApproveClick
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Approve response",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        IconButton(
            enabled = !isDeciding,
            onClick = onRejectClick
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Reject response",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ResponsesPaginationControls(
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            enabled = page > 0 && !isLoading,
            onClick = onPreviousClick
        ) {
            Text("Previous")
        }

        Text(
            text = "Page ${page + 1}",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        TextButton(
            enabled = hasNextPage && !isLoading,
            onClick = onNextClick
        ) {
            Text("Next")
        }
    }
}

private fun formatResponseDate(rawDate: String): String {
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