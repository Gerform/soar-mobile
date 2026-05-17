package tech.soc.soar.presentation.home

sealed interface HomeEffect {
    data object NavigateToHome : HomeEffect
    data object NavigateToLogin : HomeEffect
    data object NavigateToChangePassword : HomeEffect

    data class NavigateToSpace(
        val spaceName: String
    ) : HomeEffect
}