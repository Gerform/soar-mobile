package tech.soc.soar.presentation.home

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)