package tech.soc.soar.shared.domain.response.model

object ResponsePermissions {

    private val createResponseRoles = setOf(
        "admin",
        "responder"
    )

    private val decideResponseRoles = setOf(
        "responsible"
    )

    fun canCreateResponse(roles: List<String>): Boolean {
        return roles.any { role ->
            role.lowercase().trim() in createResponseRoles
        }
    }

    fun canDecideResponse(roles: List<String>): Boolean {
        return roles.any { role ->
            role.lowercase().trim() in decideResponseRoles
        }
    }
}