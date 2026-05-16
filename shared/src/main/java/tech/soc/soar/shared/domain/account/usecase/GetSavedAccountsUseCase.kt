package tech.soc.soar.shared.domain.account.usecase

import kotlinx.coroutines.flow.Flow
import tech.soc.soar.shared.domain.account.model.SavedAccount
import tech.soc.soar.shared.domain.account.repository.AccountRepository

class GetSavedAccountsUseCase(
    private val accountRepository: AccountRepository
) {

    operator fun invoke(): Flow<List<SavedAccount>> {
        return accountRepository.observeAccounts()
    }
}