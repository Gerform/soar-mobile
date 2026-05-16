package tech.soc.soar.presentation.auth.twofactor

data class TwoFactorUiState(
    val code: String = "",
    val isLoading: Boolean = false,
    val codeError: String? = null,
    val generalError: String? = null
)