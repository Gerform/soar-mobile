package tech.soc.soar.shared.data.auth.local

import tech.soc.soar.shared.domain.auth.model.CurrentUser

class UserSessionCache {

    private var currentUser: CurrentUser? = null
    private var roles: List<String> = emptyList()
    private var needTwoFactor: Boolean = false

    fun saveUser(user: CurrentUser) {
        currentUser = user
        roles = user.roles
    }

    fun getUser(): CurrentUser? {
        return currentUser
    }

    fun saveRoles(roles: List<String>) {
        this.roles = roles
    }

    fun getRoles(): List<String> {
        return roles
    }

    fun saveNeedTwoFactor(value: Boolean) {
        needTwoFactor = value
    }

    fun getNeedTwoFactor(): Boolean {
        return needTwoFactor
    }

    fun clear() {
        currentUser = null
        roles = emptyList()
        needTwoFactor = false
    }
}