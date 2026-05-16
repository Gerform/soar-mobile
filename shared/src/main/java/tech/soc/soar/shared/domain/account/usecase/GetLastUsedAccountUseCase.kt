package tech.soc.soar.shared.domain.account.usecase

import tech.soc.soar.shared.domain.account.model.SavedAccount
import tech.soc.soar.shared.domain.account.repository.AccountRepository

class GetLastUsedAccountUseCase(
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(): SavedAccount? {
        return accountRepository.getLastUsedAccount()
    }
}