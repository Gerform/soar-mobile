package tech.soc.soar.shared.domain.auth.model

object UserRoles {

    val REQUIRED_USER_ROLES = setOf(
        "admin",
        "responder",
        "guest",
        "responsible"
    )

    fun getMainRole(roles: List<String>): String {
        return roles.firstOrNull { role ->
            role in REQUIRED_USER_ROLES
        } ?: roles.firstOrNull()
        ?: "unknown"
    }

    fun getSpaces(roles: List<String>): List<String> {
        return roles
            .filterNot { role -> role in REQUIRED_USER_ROLES }
            .distinct()
            .sorted()
    }
}