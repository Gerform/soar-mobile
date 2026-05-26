package tech.soc.soar.push

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import tech.soc.soar.shared.data.push.local.InAppNotificationEntity

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

    fun hasNotificationsForSpace(spaceName: String): Boolean {
        return badgeCountForSpace(spaceName) > 0
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

    const val TYPE_NEW_ALERT = "new_alert"
    const val TYPE_APPROVAL_REQUEST = "approval_request"

    private val _state = MutableStateFlow(InAppNotificationState())
    val state = _state.asStateFlow()

    fun restoreFromEntities(
        entities: List<InAppNotificationEntity>
    ) {
        val newAlerts = mutableMapOf<String, Set<Long>>()
        val approvalBySpace = mutableMapOf<String, Set<Long>>()
        val approvalByAlert = mutableMapOf<Long, Set<Long>>()

        entities.forEach { entity ->
            val normalizedSpace = entity.spaceName.normalizeSpaceName()

            when (entity.type) {
                TYPE_NEW_ALERT -> {
                    val current = newAlerts[normalizedSpace].orEmpty()
                    newAlerts[normalizedSpace] = current + entity.alertId
                }

                TYPE_APPROVAL_REQUEST -> {
                    val responseRequestId = entity.responseRequestId
                        ?: return@forEach

                    val currentSpace = approvalBySpace[normalizedSpace].orEmpty()
                    approvalBySpace[normalizedSpace] = currentSpace + responseRequestId

                    val currentAlert = approvalByAlert[entity.alertId].orEmpty()
                    approvalByAlert[entity.alertId] = currentAlert + responseRequestId
                }
            }
        }

        _state.value = InAppNotificationState(
            newAlertIdsBySpace = newAlerts,
            approvalRequestIdsBySpace = approvalBySpace,
            approvalRequestIdsByAlert = approvalByAlert
        )
    }

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
            val currentAlertIds = currentState.approvalRequestIdsByAlert[alertId].orEmpty()

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
}

private fun String.normalizeSpaceName(): String {
    return trim().lowercase()
}