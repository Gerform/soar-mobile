package tech.soc.soar.presentation.root

sealed interface RootUiState {

    data object Loading : RootUiState

    data object Unauthenticated : RootUiState

    data object RequiresTwoFactor : RootUiState

    data object Authenticated : RootUiState
}