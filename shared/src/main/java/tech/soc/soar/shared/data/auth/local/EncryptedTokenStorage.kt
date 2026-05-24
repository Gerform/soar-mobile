package tech.soc.soar.shared.data.auth.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tech.soc.soar.shared.domain.auth.model.AuthTokens
import java.io.File
import java.security.GeneralSecurityException

class EncryptedTokenStorage(
    context: Context
) : TokenStorage {

    private val appContext = context.applicationContext

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val preferences: SharedPreferences = createPreferencesSafely()

    override suspend fun saveTokens(tokens: AuthTokens) {
        preferences.edit()
            .putString(KEY_ACCESS_TOKEN, tokens.accessToken)
            .putString(KEY_REFRESH_TOKEN, tokens.refreshToken)
            .putString(KEY_TOKEN_TYPE, tokens.tokenType)
            .apply()
    }

    override suspend fun getAccessToken(): String? {
        return preferences.getString(KEY_ACCESS_TOKEN, null)
    }

    override suspend fun getRefreshToken(): String? {
        return preferences.getString(KEY_REFRESH_TOKEN, null)
    }

    override suspend fun getTokenType(): String? {
        return preferences.getString(KEY_TOKEN_TYPE, null)
    }

    override suspend fun updateAccessToken(
        accessToken: String,
        tokenType: String
    ) {
        preferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_TOKEN_TYPE, tokenType)
            .apply()
    }

    override suspend fun saveNeedTwoFactor(value: Boolean) {
        preferences.edit()
            .putBoolean(KEY_NEED_TWO_FACTOR, value)
            .apply()
    }

    override suspend fun getNeedTwoFactor(): Boolean {
        return preferences.getBoolean(KEY_NEED_TWO_FACTOR, false)
    }

    override suspend fun saveRoles(roles: List<String>) {
        preferences.edit()
            .putString(KEY_ROLES, json.encodeToString(roles))
            .apply()
    }

    override suspend fun getRoles(): List<String> {
        val rawRoles = preferences.getString(KEY_ROLES, null)
            ?: return emptyList()

        return try {
            json.decodeFromString<List<String>>(rawRoles)
        } catch (exception: Exception) {
            emptyList()
        }
    }

    override suspend fun clear() {
        preferences.edit()
            .clear()
            .apply()
    }

    private fun createPreferencesSafely(): SharedPreferences {
        return try {
            createEncryptedPreferences()
        } catch (exception: GeneralSecurityException) {
            resetEncryptedPreferences()
            createEncryptedPreferences()
        } catch (exception: RuntimeException) {
            resetEncryptedPreferences()
            createEncryptedPreferences()
        }
    }

    private fun createEncryptedPreferences(): SharedPreferences {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            appContext,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun resetEncryptedPreferences() {
        appContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

        appContext.deleteSharedPreferences(FILE_NAME)

        val sharedPrefsDir = File(appContext.applicationInfo.dataDir, "shared_prefs")
        File(sharedPrefsDir, "$FILE_NAME.xml").delete()
    }

    private companion object {
        const val FILE_NAME = "secure_auth_storage"

        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_TOKEN_TYPE = "token_type"
        const val KEY_NEED_TWO_FACTOR = "need_two_factor"
        const val KEY_ROLES = "roles"
    }
}