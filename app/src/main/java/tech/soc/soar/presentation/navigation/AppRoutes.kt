package tech.soc.soar.presentation.navigation

object AppRoutes {
    const val LOGIN = "login"
    const val TWO_FACTOR = "two_factor"
    const val HOME = "home"
    const val CHANGE_PASSWORD = "change_password"

    const val SPACE_ARGUMENT = "spaceName"
    const val SPACE = "space/{$SPACE_ARGUMENT}"

    const val ALERT_ID_ARGUMENT = "alertId"
    const val ALERT_DETAILS = "space/{$SPACE_ARGUMENT}/alerts/{$ALERT_ID_ARGUMENT}"

    fun space(spaceName: String): String {
        return "space/$spaceName"
    }

    fun alertDetails(spaceName: String, alertId: Long): String {
        return "space/$spaceName/alerts/$alertId"
    }
}