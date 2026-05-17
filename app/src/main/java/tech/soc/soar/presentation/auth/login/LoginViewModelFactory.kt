package tech.soc.soar.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.soc.soar.shared.domain.account.usecase.DeleteSavedAccountUseCase
import tech.soc.soar.shared.domain.account.usecase.GetLastUsedAccountUseCase
import tech.soc.soar.shared.domain.account.usecase.GetSavedAccountsUseCase
import tech.soc.soar.shared.domain.auth.usecase.LoginUseCase

class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val getSavedAccountsUseCase: GetSavedAccountsUseCase,
    private val getLastUsedAccountUseCase: GetLastUsedAccountUseCase,
    private val deleteSavedAccountUseCase: DeleteSavedAccountUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginUseCase = loginUseCase,
                getSavedAccountsUseCase = getSavedAccountsUseCase,
                getLastUsedAccountUseCase = getLastUsedAccountUseCase,
                deleteSavedAccountUseCase = deleteSavedAccountUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}