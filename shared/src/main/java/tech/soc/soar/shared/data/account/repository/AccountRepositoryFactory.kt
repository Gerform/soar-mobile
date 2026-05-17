package tech.soc.soar.shared.data.account.repository

import android.content.Context
import tech.soc.soar.shared.data.database.DatabaseFactory
import tech.soc.soar.shared.domain.account.repository.AccountRepository

object AccountRepositoryFactory {

    fun create(context: Context): AccountRepository {
        val database = DatabaseFactory.create(context)

        return AccountRepositoryImpl(
            userAccountDao = database.userAccountDao()
        )
    }
}