package tech.soc.soar.presentation.alertdetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import tech.soc.soar.presentation.components.AppScreenScaffold
import tech.soc.soar.shared.domain.alert.model.AlertStatus
import tech.soc.soar.shared.domain.response.model.ResponseActionType
import tech.soc.soar.shared.domain.response.model.ResponseTarget
import tech.soc.soar.shared.domain.response.model.SuccessfulAction
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private const val RESPONSE_TARGET_TAG = "response_target"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailsScreen(
    state: AlertDetailsUiState,
    onEvent: (AlertDetailsEvent) -> Unit
) {
    AppScreenScaffold(
        isHomeClickable = true,
        showLogout = false,
        isLoading = state.isLoading || state.isUpdatingStatus || state.isCreatingResponse,
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

                        if (state.responseStatusMessage != null) {
                            Text(
                                text = state.responseStatusMessage,
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

                        ResponseAwareRawBody(
                            rawBody = state.details.rawBody,
                            targets = state.details.responseTargets,
                            canCreateResponses = state.canCreateResponses,
                            onTargetLongPressed = { target ->
                                onEvent(
                                    AlertDetailsEvent.ResponseTargetLongPressed(
                                        target = target
                                    )
                                )
                            }
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

                        if (state.successfulActions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))

                            SuccessfulActionsSection(
                                actions = state.successfulActions,
                                canCreateResponses = state.canCreateResponses,
                                onSuccessfulActionTargetLongPressed = { action ->
                                    onEvent(
                                        AlertDetailsEvent.SuccessfulActionTargetLongPressed(
                                            action = action
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        state.pendingResponseAction?.let { action ->
            CreateResponseActionDialog(
                action = action,
                isCreating = state.isCreatingResponse,
                onDismiss = {
                    onEvent(AlertDetailsEvent.DismissResponseDialog)
                },
                onConfirm = { message ->
                    onEvent(
                        AlertDetailsEvent.CreateResponseActionConfirmed(
                            message = message
                        )
                    )
                }
            )
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

@Composable
private fun ResponseAwareRawBody(
    rawBody: String,
    targets: List<ResponseTarget>,
    canCreateResponses: Boolean,
    onTargetLongPressed: (ResponseTarget) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onBackground
    val targetColor = MaterialTheme.colorScheme.primary

    val annotatedText = remember(
        rawBody,
        targets,
        canCreateResponses
    ) {
        buildAnnotatedString {
            var currentIndex = 0

            val matches = targets
                .flatMap { target ->
                    Regex.escape(target.value)
                        .toRegex()
                        .findAll(rawBody)
                        .map { match ->
                            TargetTextRange(
                                start = match.range.first,
                                endExclusive = match.range.last + 1,
                                target = target
                            )
                        }
                        .toList()
                }
                .distinctBy { range ->
                    "${range.start}:${range.endExclusive}:${range.target.value}:${range.target.fieldName}"
                }
                .sortedWith(
                    compareBy<TargetTextRange> { it.start }
                        .thenByDescending { it.endExclusive - it.start }
                )
                .filterNonOverlapping()

            matches.forEach { range ->
                if (range.start > currentIndex) {
                    append(rawBody.substring(currentIndex, range.start))
                }

                if (canCreateResponses) {
                    pushStringAnnotation(
                        tag = RESPONSE_TARGET_TAG,
                        annotation = range.target.toAnnotation()
                    )

                    withStyle(
                        style = SpanStyle(
                            color = targetColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(rawBody.substring(range.start, range.endExclusive))
                    }

                    pop()
                } else {
                    append(rawBody.substring(range.start, range.endExclusive))
                }

                currentIndex = range.endExclusive
            }

            if (currentIndex < rawBody.length) {
                append(rawBody.substring(currentIndex))
            }
        }
    }

    var layoutResult by remember {
        mutableStateOf<TextLayoutResult?>(null)
    }

    BasicText(
        text = annotatedText,
        style = MaterialTheme.typography.bodyMedium.merge(
            TextStyle(
                color = textColor
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(annotatedText, canCreateResponses) {
                detectTapGestures(
                    onLongPress = { offset ->
                        if (!canCreateResponses) {
                            return@detectTapGestures
                        }

                        val layout = layoutResult ?: return@detectTapGestures
                        val position = layout.getOffsetForPosition(offset)

                        val annotation = annotatedText
                            .getStringAnnotations(
                                tag = RESPONSE_TARGET_TAG,
                                start = position,
                                end = position
                            )
                            .firstOrNull()
                            ?: return@detectTapGestures

                        val target = annotation.item.toResponseTarget()
                            ?: return@detectTapGestures

                        onTargetLongPressed(target)
                    }
                )
            },
        onTextLayout = { result ->
            layoutResult = result
        }
    )
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

@Composable
private fun CreateResponseActionDialog(
    action: PendingResponseAction,
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var message by remember(action) {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = {
            if (!isCreating) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = action.title
            )
        },
        text = {
            Column {
                Text(
                    text = action.targetValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                    },
                    enabled = !isCreating,
                    label = {
                        Text("Message")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isCreating && message.isNotBlank(),
                onClick = {
                    onConfirm(message)
                }
            ) {
                Text(
                    text = if (isCreating) {
                        "Sending..."
                    } else {
                        "Send"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isCreating,
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}
private data class TargetTextRange(
    val start: Int,
    val endExclusive: Int,
    val target: ResponseTarget
)

private fun List<TargetTextRange>.filterNonOverlapping(): List<TargetTextRange> {
    val result = mutableListOf<TargetTextRange>()
    var lastEnd = -1

    forEach { range ->
        if (range.start >= lastEnd) {
            result.add(range)
            lastEnd = range.endExclusive
        }
    }

    return result
}

private fun ResponseTarget.toAnnotation(): String {
    return listOf(
        fieldName,
        value,
        type.name
    ).joinToString(separator = "|")
}

private fun String.toResponseTarget(): ResponseTarget? {
    val parts = split("|")

    if (parts.size != 3) {
        return null
    }

    val type = runCatching {
        tech.soc.soar.shared.domain.response.model.ResponseTargetType.valueOf(parts[2])
    }.getOrNull() ?: return null

    return ResponseTarget(
        fieldName = parts[0],
        value = parts[1],
        type = type
    )
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

@Composable
private fun SuccessfulActionsSection(
    actions: List<SuccessfulAction>,
    canCreateResponses: Boolean,
    onSuccessfulActionTargetLongPressed: (SuccessfulAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Successful actions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        actions.forEach { action ->
            SuccessfulActionCard(
                action = action,
                canCreateResponses = canCreateResponses,
                onTargetLongPressed = onSuccessfulActionTargetLongPressed,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SuccessfulActionCard(
    action: SuccessfulAction,
    canCreateResponses: Boolean,
    onTargetLongPressed: (SuccessfulAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val canUnblock =
        canCreateResponses &&
                action.actionName.lowercase().trim() == ResponseActionType.BLOCK_IP

    Box(
        modifier = modifier
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
                    text = action.actionName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "success",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SuccessfulActionInfoLine(
                label = "Target",
                value = action.targetValue,
                canLongPress = canUnblock,
                onLongPress = {
                    onTargetLongPressed(action)
                }
            )

            SuccessfulActionInfoLine(
                label = "Approved by",
                value = action.approvedUser
            )

            SuccessfulActionInfoLine(
                label = "Space",
                value = action.spaceName
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatAlertDetailsDate(action.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SuccessfulActionInfoLine(
    label: String,
    value: String,
    canLongPress: Boolean = false,
    onLongPress: (() -> Unit)? = null
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

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(
                    enabled = canLongPress,
                    onClick = {},
                    onLongClick = {
                        onLongPress?.invoke()
                    }
                )
                .padding(
                    horizontal = if (canLongPress) 6.dp else 0.dp,
                    vertical = if (canLongPress) 3.dp else 0.dp
                )
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = if (canLongPress) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (canLongPress) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }
            )
        }
    }
}