package tech.soc.soar.push

object SoarNotificationIds {

    fun newAlertNotificationId(alertId: Long): Int {
        return (100_000L + alertId).safeNotificationId()
    }

    fun approvalRequestNotificationId(responseRequestId: Long): Int {
        return (200_000L + responseRequestId).safeNotificationId()
    }

    private fun Long.safeNotificationId(): Int {
        return (this % Int.MAX_VALUE).toInt()
    }
}