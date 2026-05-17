package tech.soc.soar.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
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
import tech.soc.soar.presentation.components.AppScreenScaffold

@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit
) {
    AppScreenScaffold(
        isHomeClickable = true,
        showLogout = true,
        isLoading = state.isLoading,
        onLogoutClick = {
            onEvent(HomeEvent.LogoutClicked)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
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

                state.spaces.size == 1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 72.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Choose space",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        SpaceCircleButton(
                            spaceName = state.spaces.first(),
                            gradient = spaceGradient(0),
                            modifier = Modifier.size(150.dp),
                            onClick = {
                                onEvent(HomeEvent.SpaceClicked(state.spaces.first()))
                            }
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 72.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Choose space",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 32.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(28.dp),
                            verticalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            itemsIndexed(state.spaces) { index, space ->
                                SpaceCircleButton(
                                    spaceName = space,
                                    gradient = spaceGradient(index),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f),
                                    onClick = {
                                        onEvent(HomeEvent.SpaceClicked(space))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            TextButton(
                onClick = {
                    onEvent(HomeEvent.ChangePasswordClicked)
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp)
            ) {
                Text("Change password")
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