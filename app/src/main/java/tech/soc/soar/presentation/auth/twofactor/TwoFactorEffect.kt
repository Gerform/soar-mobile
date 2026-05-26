package tech.soc.soar.presentation.auth.twofactor

sealed interface TwoFactorEffect {

    data object NavigateToHome : TwoFactorEffect

    data object NavigateToLogin : TwoFactorEffect
}