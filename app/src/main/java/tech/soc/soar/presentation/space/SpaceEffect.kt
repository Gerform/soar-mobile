package tech.soc.soar.presentation.space

sealed interface SpaceEffect {
    data object NavigateToHome : SpaceEffect
    data object NavigateToLogin : SpaceEffect

    data class NavigateToAlertDetails(
        val alertId: Long
    ) : SpaceEffect
}