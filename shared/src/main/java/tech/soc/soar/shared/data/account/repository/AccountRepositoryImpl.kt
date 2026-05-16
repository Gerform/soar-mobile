package tech.soc.soar.shared.data.account.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tech.soc.soar.shared.data.account.local.UserAccountDao
import tech.soc.soar.shared.data.account.mapper.toDomain
import tech.soc.soar.shared.data.account.mapper.toEntity
import tech.soc.soar.shared.domain.account.model.SavedAccount
import tech.soc.soar.shared.domain.account.repository.AccountRepository
import tech.soc.soar.shared.domain.auth.model.CurrentUser

class AccountRepositoryImpl(
    private val userAccountDao: UserAccountDao
) : AccountRepository {

    override fun observeAccounts(): Flow<List<SavedAccount>> {
        return userAccountDao.observeAccounts()
            .map { accounts ->
                accounts.map { it.toDomain() }
            }
    }

    override suspend fun getAccounts(): List<SavedAccount> {
        return userAccountDao.getAccounts()
            .map { it.toDomain() }
    }

    override suspend fun getLastUsedAccount(): SavedAccount? {
        return userAccountDao.getLastUsedAccount()
            ?.toDomain()
    }

    override suspend fun saveLoggedInUser(user: CurrentUser) {
        userAccountDao.clearLastUsed()

        userAccountDao.upsertAccount(
            user.toEntity(
                lastLoginAt = System.currentTimeMillis()
            )
        )
    }
}