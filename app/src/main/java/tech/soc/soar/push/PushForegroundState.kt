package tech.soc.soar.push

object PushForegroundState {

    @Volatile
    private var openedSpaceName: String? = null

    @Volatile
    private var openedResponsesAlertId: Long? = null

    fun setOpenedSpace(spaceName: String) {
        openedSpaceName = spaceName.trim()
    }

    fun clearOpenedSpace(spaceName: String) {
        if (openedSpaceName.equals(spaceName.trim(), ignoreCase = true)) {
            openedSpaceName = null
        }
    }

    fun isSpaceOpened(spaceName: String): Boolean {
        return openedSpaceName.equals(
            other = spaceName.trim(),
            ignoreCase = true
        )
    }

    fun setOpenedResponses(alertId: Long) {
        openedResponsesAlertId = alertId
    }

    fun clearOpenedResponses(alertId: Long) {
        if (openedResponsesAlertId == alertId) {
            openedResponsesAlertId = null
        }
    }

    fun isResponsesOpened(alertId: Long): Boolean {
        return openedResponsesAlertId == alertId
    }
}