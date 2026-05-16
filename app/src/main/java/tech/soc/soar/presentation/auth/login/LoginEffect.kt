package tech.soc.soar.presentation.auth.login

sealed interface LoginEffect {

    data object NavigateToHome : LoginEffect

    data object NavigateToTwoFactor : LoginEffect
}