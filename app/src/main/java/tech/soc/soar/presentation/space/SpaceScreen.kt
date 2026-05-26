package tech.soc.soar.presentation.space

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.launch
import tech.soc.soar.presentation.components.AppScreenScaffold
import tech.soc.soar.presentation.components.NotificationBadge
import tech.soc.soar.push.InAppNotificationCenter
import tech.soc.soar.push.PushEvent
import tech.soc.soar.push.PushEventBus
import tech.soc.soar.push.PushForegroundState
import tech.soc.soar.push.SoarNotificationIds
import tech.soc.soar.shared.data.push.local.InAppNotificationStorage
import tech.soc.soar.shared.domain.alert.model.AlertItem
import tech.soc.soar.shared.domain.alert.model.AlertStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceScreen(
    spaceName: String,
    accountUid: String?,
    state: SpaceUiState,
    onEvent: (SpaceEvent) -> Unit
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val notificationState by InAppNotificationCenter.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(spaceName) {
        PushForegroundState.setOpenedSpace(spaceName)

        onDispose {
            PushForegroundState.clearOpenedSpace(spaceName)
        }
    }

    DisposableEffect(
        lifecycleOwner,
        spaceName,
        notificationState
    ) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (notificationState.hasNotificationsForSpace(spaceName)) {
                    onEvent(
                        SpaceEvent.PushRefreshReceived(
                            scrollToTop = true
                        )
                    )
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(spaceName) {
        PushEventBus.events.collect { event ->
            when (event) {
                is PushEvent.SpaceShouldRefresh -> {
                    if (event.spaceName.equals(spaceName, ignoreCase = true)) {
                        onEvent(
                            SpaceEvent.PushRefreshReceived(
                                scrollToTop = event.scrollToTop
                            )
                        )
                    }
                }

                is PushEvent.ResponsesShouldRefresh -> Unit
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
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                SpaceHeader(
                                    spaceName = spaceName,
                                    fromCache = state.fromCache,
                                    error = null
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(96.dp))
                                CircularProgressIndicator()
                            }
                        }
                    }

                    state.error != null && state.alerts.isEmpty() -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                SpaceHeader(
                                    spaceName = spaceName,
                                    fromCache = state.fromCache,
                                    error = null
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(96.dp))

                                Text(
                                    text = state.error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    state.alerts.isEmpty() -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                SpaceHeader(
                                    spaceName = spaceName,
                                    fromCache = state.fromCache,
                                    error = null
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(96.dp))

                                Text(
                                    text = "No alerts",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                SpaceHeader(
                                    spaceName = spaceName,
                                    fromCache = state.fromCache,
                                    error = state.error
                                )
                            }

                            items(
                                items = state.alerts,
                                key = { alert -> alert.id }
                            ) { alert ->
                                val approvalCount = notificationState.approvalCountForAlert(alert.id)
                                val hasNewAlertNotification = notificationState.hasNewAlert(alert.id)

                                val badgeCount = approvalCount + if (hasNewAlertNotification) {
                                    1
                                } else {
                                    0
                                }

                                AlertListItem(
                                    alert = alert,
                                    badgeCount = badgeCount,
                                    forceHighlight = hasNewAlertNotification || approvalCount > 0,
                                    onClick = {
                                        if (hasNewAlertNotification) {
                                            if (!accountUid.isNullOrBlank()) {
                                                coroutineScope.launch {
                                                    InAppNotificationStorage.clearNewAlert(
                                                        context = context,
                                                        accountUid = accountUid,
                                                        alertId = alert.id
                                                    )

                                                    InAppNotificationCenter.clearNewAlert(alert.id)
                                                }
                                            } else {
                                                InAppNotificationCenter.clearNewAlert(alert.id)
                                            }

                                            NotificationManagerCompat.from(context)
                                                .cancel(
                                                    SoarNotificationIds.newAlertNotificationId(
                                                        alert.id
                                                    )
                                                )
                                        }

                                        onEvent(
                                            SpaceEvent.AlertClicked(
                                                alertId = alert.id
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
private fun SpaceHeader(
    spaceName: String,
    fromCache: Boolean,
    error: String?
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

    if (fromCache) {
        Text(
            text = "Offline data",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun AlertListItem(
    alert: AlertItem,
    badgeCount: Int,
    forceHighlight: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val shouldHighlightAsNew =
        alert.shouldBeHighlightedAsNew() || forceHighlight

    val containerColor = when {
        shouldHighlightAsNew && isDarkTheme -> {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
        }

        !shouldHighlightAsNew && isDarkTheme -> {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        }

        shouldHighlightAsNew && !isDarkTheme -> {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        }

        else -> {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        }
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
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = alert.reason,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 28.dp)
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

        NotificationBadge(
            count = badgeCount,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 6.dp, y = (-6).dp)
        )
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

private fun AlertItem.isInactiveStatus(): Boolean {
    return AlertStatus.isInactive(status)
}

private fun AlertItem.shouldBeHighlightedAsNew(): Boolean {
    return !isViewed && !isInactiveStatus()
}