package tech.soc.soar.presentation.auth.twofactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.soc.soar.shared.domain.auth.usecase.ConfirmTwoFactorUseCase

class TwoFactorViewModelFactory(
    private val confirmTwoFactorUseCase: ConfirmTwoFactorUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TwoFactorViewModel::class.java)) {
            return TwoFactorViewModel(
                confirmTwoFactorUseCase = confirmTwoFactorUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}