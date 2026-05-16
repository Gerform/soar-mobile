package tech.soc.soar.shared.domain.account.model

data class SavedAccount(
    val uid: Int,
    val username: String,
    val mail: String,
    val roles: List<String>,
    val lastConnection: String?,
    val twoFactorConfirmed: Boolean,
    val lastLoginAt: Long,
    val isLastUsed: Boolean
)