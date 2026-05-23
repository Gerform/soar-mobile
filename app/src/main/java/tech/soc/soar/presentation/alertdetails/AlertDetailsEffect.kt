package tech.soc.soar.presentation.alertdetails

sealed interface AlertDetailsEffect {
    data object NavigateToHome : AlertDetailsEffect
    data object NavigateBack : AlertDetailsEffect
}