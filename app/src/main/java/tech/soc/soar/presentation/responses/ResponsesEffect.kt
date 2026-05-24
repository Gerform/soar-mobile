package tech.soc.soar.presentation.responses

sealed interface ResponsesEffect {
    data object NavigateToHome : ResponsesEffect
    data object NavigateBack : ResponsesEffect
}