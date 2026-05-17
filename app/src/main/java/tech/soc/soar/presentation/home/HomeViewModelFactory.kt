package tech.soc.soar.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.auth.usecase.LogoutUseCase

class HomeViewModelFactory(
    private val logoutUseCase: LogoutUseCase,
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                logoutUseCase = logoutUseCase,
                checkSessionUseCase = checkSessionUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}