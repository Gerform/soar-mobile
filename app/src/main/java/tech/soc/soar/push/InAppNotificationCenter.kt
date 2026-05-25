package tech.soc.soar.push

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class InAppNotificationState(
    val newAlertIdsBySpace: Map<String, Set<Long>> = emptyMap(),
    val approvalRequestIdsBySpace: Map<String, Set<Long>> = emptyMap(),
    val approvalRequestIdsByAlert: Map<Long, Set<Long>> = emptyMap()
) {
    fun badgeCountForSpace(spaceName: String): Int {
        val normalizedSpace = spaceName.normalizeSpaceName()

        val alertCount = newAlertIdsBySpace[normalizedSpace]?.size ?: 0
        val approvalCount = approvalRequestIdsBySpace[normalizedSpace]?.size ?: 0

        return alertCount + approvalCount
    }

    fun hasNewAlert(alertId: Long): Boolean {
        return newAlertIdsBySpace.values.any { ids ->
            alertId in ids
        }
    }

    fun approvalCountForAlert(alertId: Long): Int {
        return approvalRequestIdsByAlert[alertId]?.size ?: 0
    }

    fun hasApprovalRequest(responseRequestId: Long): Boolean {
        return approvalRequestIdsByAlert.values.any { ids ->
            responseRequestId in ids
        }
    }

    fun approvalRequestIdsForAlert(alertId: Long): Set<Long> {
        return approvalRequestIdsByAlert[alertId].orEmpty()
    }
}

object InAppNotificationCenter {

    private val _state = MutableStateFlow(InAppNotificationState())
    val state = _state.asStateFlow()

    fun recordNewAlert(
        spaceName: String,
        alertId: Long
    ) {
        val normalizedSpace = spaceName.normalizeSpaceName()

        _state.update { currentState ->
            val currentIds = currentState.newAlertIdsBySpace[normalizedSpace].orEmpty()

            currentState.copy(
                newAlertIdsBySpace = currentState.newAlertIdsBySpace +
                        (normalizedSpace to (currentIds + alertId))
            )
        }
    }

    fun recordApprovalRequest(
        spaceName: String,
        alertId: Long,
        responseRequestId: Long
    ) {
        val normalizedSpace = spaceName.normalizeSpaceName()

        _state.update { currentState ->
            val currentSpaceIds = currentState.approvalRequestIdsBySpace[normalizedSpace].orEmpty()
            val currentAlertIds = currentState.apvalRequestIdsByAlertCompat(alertId)

            currentState.copy(
                approvalRequestIdsBySpace = currentState.approvalRequestIdsBySpace +
                        (normalizedSpace to (currentSpaceIds + responseRequestId)),
                approvalRequestIdsByAlert = currentState.approvalRequestIdsByAlert +
                        (alertId to (currentAlertIds + responseRequestId))
            )
        }
    }

    fun clearNewAlert(alertId: Long) {
        _state.update { currentState ->
            currentState.copy(
                newAlertIdsBySpace = currentState.newAlertIdsBySpace
                    .mapValues { (_, ids) -> ids - alertId }
                    .filterValues { ids -> ids.isNotEmpty() }
            )
        }
    }

    fun clearApprovalRequestsForAlert(alertId: Long): Set<Long> {
        val removedIds = _state.value.approvalRequestIdsByAlert[alertId].orEmpty()

        if (removedIds.isEmpty()) {
            return emptySet()
        }

        _state.update { currentState ->
            currentState.copy(
                approvalRequestIdsByAlert = currentState.approvalRequestIdsByAlert - alertId,
                approvalRequestIdsBySpace = currentState.approvalRequestIdsBySpace
                    .mapValues { (_, ids) -> ids - removedIds }
                    .filterValues { ids -> ids.isNotEmpty() }
            )
        }

        return removedIds
    }

    fun approvalRequestIdsForAlert(alertId: Long): Set<Long> {
        return state.value.approvalRequestIdsForAlert(alertId)
    }

    private fun InAppNotificationState.apvalRequestIdsByAlertCompat(
        alertId: Long
    ): Set<Long> {
        return approvalRequestIdsByAlert[alertId].orEmpty()
    }
}

private fun String.normalizeSpaceName(): String {
    return trim().lowercase()
}