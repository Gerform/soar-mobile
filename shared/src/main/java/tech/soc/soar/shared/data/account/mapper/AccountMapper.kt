package tech.soc.soar.shared.data.account.mapper

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tech.soc.soar.shared.data.account.local.UserAccountEntity
import tech.soc.soar.shared.domain.account.model.SavedAccount
import tech.soc.soar.shared.domain.auth.model.CurrentUser

private val json = Json {
    ignoreUnknownKeys = true
}

fun UserAccountEntity.toDomain(): SavedAccount {
    return SavedAccount(
        uid = uid,
        username = username,
        mail = mail,
        roles = runCatching {
            json.decodeFromString<List<String>>(rolesJson)
        }.getOrDefault(emptyList()),
        lastConnection = lastConnection,
        twoFactorConfirmed = twoFactorConfirmed,
        lastLoginAt = lastLoginAt,
        isLastUsed = isLastUsed
    )
}

fun CurrentUser.toEntity(
    lastLoginAt: Long
): UserAccountEntity {
    return UserAccountEntity(
        uid = uid,
        username = username,
        mail = mail,
        rolesJson = json.encodeToString(roles),
        lastConnection = lastConnection,
        twoFactorConfirmed = twoFactorConfirmed,
        lastLoginAt = lastLoginAt,
        isLastUsed = true
    )
}