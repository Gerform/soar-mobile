package tech.soc.soar.shared.domain.alert.model

object AlertStatus {

    const val NEW = "new"
    const val FALSE_POSITIVE = "false positive"
    const val EXPECTATION = "expectation"
    const val CLOSED = "closed"

    private val inactiveStatuses = setOf(
        FALSE_POSITIVE,
        CLOSED
    )

    fun isInactive(status: String): Boolean {
        return status.lowercase().trim() in inactiveStatuses
    }
}