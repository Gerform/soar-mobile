package tech.soc.soar.presentation.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.soc.soar.di.AppDependencies
import tech.soc.soar.presentation.components.AppScreenScaffold
import tech.soc.soar.presentation.push.PushPermissionRequester

@Composable
fun HomeScreen(
    state: HomeUiState,
    accountUid: String?,
    onEvent: (HomeEvent) -> Unit
) {
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
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = spaceName,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
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