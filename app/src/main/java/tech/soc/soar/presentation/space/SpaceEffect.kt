package tech.soc.soar.presentation.space

sealed interface SpaceEffect {
    data object NavigateToHome : SpaceEffect
    data object NavigateToLogin : SpaceEffect
}