package tech.soc.soar.presentation.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.soc.soar.shared.domain.alert.usecase.GetAlertsPageUseCase
import tech.soc.soar.shared.domain.auth.usecase.LogoutUseCase

class SpaceViewModelFactory(
    private val spaceName: String,
    private val getAlertsPageUseCase: GetAlertsPageUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpaceViewModel::class.java)) {
            return SpaceViewModel(
                spaceName = spaceName,
                getAlertsPageUseCase = getAlertsPageUseCase,
                logoutUseCase = logoutUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}