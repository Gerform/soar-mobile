package tech.soc.soar.presentation.auth.changepassword

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false,
    val oldPasswordError: String? = null,
    val newPasswordError: String? = null,
    val generalError: String? = null,
    val successMessage: String? = null
)