package tech.soc.soar.presentation.auth.login

import tech.soc.soar.shared.domain.account.model.SavedAccount

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val savedAccounts: List<SavedAccount> = emptyList(),
    val isAccountPickerVisible: Boolean = false
)