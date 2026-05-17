package tech.soc.soar.shared.domain.account.usecase

import tech.soc.soar.shared.domain.account.repository.AccountRepository

class DeleteSavedAccountUseCase(
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(uid: Int) {
        accountRepository.deleteAccount(uid)
    }
}