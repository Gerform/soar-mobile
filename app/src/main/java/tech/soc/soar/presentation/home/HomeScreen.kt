package tech.soc.soar.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.soc.soar.di.AppDependencies
import tech.soc.soar.presentation.components.AppScreenScaffold
import tech.soc.soar.presentation.components.NotificationBadge
import tech.soc.soar.presentation.push.PushPermissionRequester
import tech.soc.soar.push.InAppNotificationCenter
import tech.soc.soar.push.InAppNotificationState
import tech.soc.soar.shared.data.push.local.InAppNotificationStorage

@Composable
fun HomeScreen(
    state: HomeUiState,
    accountUid: String?,
    onEvent: (HomeEvent) -> Unit
) {
    val context = LocalContext.current
    val notificationState by InAppNotificationCenter.state.collectAsState()

    LaunchedEffect(accountUid) {
        if (!accountUid.isNullOrBlank()) {
            val entities = InAppNotificationStorage.getByAccountUid(
                context = context,
                accountUid = accountUid
            )

            InAppNotificationCenter.restoreFromEntities(entities)
        }
    }

    if (!accountUid.isNullOrBlank()) {
        PushPermissionRequester(
            accountUid = accountUid,
            registerMobilePushTokenUseCase = AppDependencies.registerMobilePushTokenUseCase,
            isMobilePushEnabledForAccountUseCase = AppDependencies.isMobilePushEnabledForAccountUseCase,
            markMobilePushEnabledForAccountUseCase = AppDependencies.markMobilePushEnabledForAccountUseCase
        )
    }

    AppScreenScaffold(
        isHomeClickable = true,
        showLogout = true,
        isLoading = state.isLoading,
        onLogoutClick = {
            onEvent(HomeEvent.LogoutClicked)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    state.spaces.isEmpty() -> {
                        Text(
                            text = "No available spaces",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        SpacesGrid(
                            spaces = state.spaces,
                            notificationState = notificationState,
                            onSpaceClick = { space ->
                                onEvent(HomeEvent.SpaceClicked(space))
                            }
                        )
                    }
                }

                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }

            TextButton(
                onClick = {
                    onEvent(HomeEvent.ChangePasswordClicked)
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp)
            ) {
                Text("Change password")
            }
        }
    }
}

@Composable
private fun SpacesGrid(
    spaces: List<String>,
    notificationState: InAppNotificationState,
    onSpaceClick: (String) -> Unit
) {
    val columns = if (spaces.size == 1) {
        GridCells.Fixed(1)
    } else {
        GridCells.Fixed(2)
    }

    LazyVerticalGrid(
        columns = columns,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 28.dp,
            bottom = 24.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {
            Text(
                text = "Choose space",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (spaces.size == 1) {
            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SpaceCircleButton(
                        spaceName = spaces.first(),
                        badgeCount = notificationState.badgeCountForSpace(spaces.first()),
                        gradient = spaceGradient(0),
                        modifier = Modifier.size(150.dp),
                        onClick = {
                            onSpaceClick(spaces.first())
                        }
                    )
                }
            }
        } else {
            itemsIndexed(spaces) { index, space ->
                SpaceCircleButton(
                    spaceName = space,
                    badgeCount = notificationState.badgeCountForSpace(space),
                    gradient = spaceGradient(index),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    onClick = {
                        onSpaceClick(space)
                    }
                )
            }
        }
    }
}

@Composable
private fun SpaceCircleButton(
    spaceName: String,
    badgeCount: Int,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val glassOverlay = if (isDarkTheme) {
        Color.Black.copy(alpha = 0.36f)
    } else {
        Color.White.copy(alpha = 0.34f)
    }

    val glassHighlight = if (isDarkTheme) {
        Color.White.copy(alpha = 0.10f)
    } else {
        Color.White.copy(alpha = 0.42f)
    }

    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.24f)
    } else {
        Color.White.copy(alpha = 0.65f)
    }

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(gradient)
                .background(glassOverlay)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = borderColor
                    ),
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                glassHighlight,
                                Color.Transparent
                            ),
                            center = androidx.compose.ui.geometry.Offset(
                                x = 70f,
                                y = 45f
                            ),
                            radius = 180f
                        )
                    )
            )

            Text(
                text = spaceName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        NotificationBadge(
            count = badgeCount,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-4).dp)
        )
    }
}

private fun spaceGradient(index: Int): Brush {
    val gradients = listOf(
        listOf(Color(0xFF3A7BD5), Color(0xFF00D2FF)),
        listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
        listOf(Color(0xFF11998E), Color(0xFF38EF7D)),
        listOf(Color(0xFF4776E6), Color(0xFF8E54E9)),
        listOf(Color(0xFF614385), Color(0xFF516395)),
        listOf(Color(0xFF1D976C), Color(0xFF93F9B9))
    )

    return Brush.linearGradient(
        colors = gradients[index % gradients.size]
    )
}