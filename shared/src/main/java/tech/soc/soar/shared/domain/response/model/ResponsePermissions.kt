package tech.soc.soar.shared.domain.response.model

object ResponsePermissions {

    private val allowedRoles = setOf(
        "admin",
        "responder"
    )

    fun canCreateResponse(roles: List<String>): Boolean {
        return roles.any { role ->
            role.lowercase().trim() in allowedRoles
        }
    }
}