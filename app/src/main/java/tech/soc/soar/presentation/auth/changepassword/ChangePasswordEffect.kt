package tech.soc.soar.presentation.auth.changepassword

sealed interface ChangePasswordEffect {
    data object NavigateBack : ChangePasswordEffect
}