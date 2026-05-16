package tech.soc.soar.shared.data.account.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {

    @Query("SELECT * FROM user_accounts ORDER BY last_login_at DESC")
    fun observeAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts ORDER BY last_login_at DESC")
    suspend fun getAccounts(): List<UserAccountEntity>

    @Query("SELECT * FROM user_accounts WHERE is_last_used = 1 LIMIT 1")
    suspend fun getLastUsedAccount(): UserAccountEntity?

    @Query("UPDATE user_accounts SET is_last_used = 0")
    suspend fun clearLastUsed()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAccount(account: UserAccountEntity)

    @Query("DELETE FROM user_accounts WHERE uid = :uid")
    suspend fun deleteAccount(uid: Int)
}