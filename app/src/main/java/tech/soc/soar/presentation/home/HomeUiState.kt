package tech.soc.soar.presentation.home

data class HomeUiState(
    val isLoading: Boolean = false,
    val spaces: List<String> = emptyList(),
    val currentAccountUid: String? = null,
    val error: String? = null
)