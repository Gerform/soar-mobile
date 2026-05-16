package tech.soc.soar.shared.domain.auth.model

data class CurrentUser(
    val uid: Int,
    val username: String,
    val mail: String,
    val roles: List<String>,
    val lastConnection: String?,
    val twoFactorConfirmed: Boolean
)