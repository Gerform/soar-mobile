package tech.soc.soar.shared.domain.account.repository

import kotlinx.coroutines.flow.Flow
import tech.soc.soar.shared.domain.account.model.SavedAccount
import tech.soc.soar.shared.domain.auth.model.CurrentUser

interface AccountRepository {

    fun observeAccounts(): Flow<List<SavedAccount>>

    suspend fun getAccounts(): List<SavedAccount>

    suspend fun getLastUsedAccount(): SavedAccount?

    suspend fun saveLoggedInUser(user: CurrentUser)
}