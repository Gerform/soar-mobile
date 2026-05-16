package tech.soc.soar.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase

class RootViewModelFactory(
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RootViewModel::class.java)) {
            return RootViewModel(
                checkSessionUseCase = checkSessionUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}