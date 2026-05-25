package tech.soc.soar.presentation.alertdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.alert.model.AlertStatus
import tech.soc.soar.shared.domain.alert.usecase.GetAlertDetailsUseCase
import tech.soc.soar.shared.domain.alert.usecase.MarkAlertViewedUseCase
import tech.soc.soar.shared.domain.alert.usecase.UpdateAlertStatusUseCase
import tech.soc.soar.shared.domain.alert.usecase.UpdateCachedAlertStatusUseCase
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.response.model.ResponseActionType
import tech.soc.soar.shared.domain.response.model.ResponsePermissions
import tech.soc.soar.shared.domain.response.model.ResponseTargetType
import tech.soc.soar.shared.domain.response.usecase.CreateResponseActionUseCase
import tech.soc.soar.shared.domain.response.usecase.GetSuccessfulActionsUseCase

class AlertDetailsViewModel(
    private val alertId: Long,
    private val spaceName: String,
    private val getAlertDetailsUseCase: GetAlertDetailsUseCase,
    private val markAlertViewedUseCase: MarkAlertViewedUseCase,
    private val updateAlertStatusUseCase: UpdateAlertStatusUseCase,
    private val createResponseActionUseCase: CreateResponseActionUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val updateCachedAlertStatusUseCase: UpdateCachedAlertStatusUseCase,
    private val getSuccessfulActionsUseCase: GetSuccessfulActionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AlertDetailsUiState())
    val state: StateFlow<AlertDetailsUiState> = _state.asStateFlow()

    private val _effect = Channel<AlertDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadDetails(isRefresh = false)
        loadSuccessfulActions()
        loadResponsePermissions()
    }

    fun onEvent(event: AlertDetailsEvent) {
        when (event) {
            AlertDetailsEvent.HomeClicked -> {
                viewModelScope.launch {
                    _effect.send(AlertDetailsEffect.NavigateToHome)
                }
            }

            AlertDetailsEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(AlertDetailsEffect.NavigateBack)
                }
            }

            AlertDetailsEvent.ResponsesClicked -> {
                viewModelScope.launch {
                    _effect.send(AlertDetailsEffect.NavigateToResponses)
                }
            }

            AlertDetailsEvent.RefreshTriggered -> {
                if (!state.value.isLoading && !state.value.isRefreshing && !state.value.isUpdatingStatus && !state.value.isCreatingResponse) {
                    loadDetails(isRefresh = true)
                    loadSuccessfulActions()
                    loadResponsePermissions()
                }
            }

            AlertDetailsEvent.DismissResponseDialog -> {
                _state.update {
                    it.copy(
                        pendingResponseAction = null
                    )
                }
            }

            is AlertDetailsEvent.StatusSelected -> {
                updateStatus(event.status)
            }

            is AlertDetailsEvent.ResponseTargetLongPressed -> {
                if (!state.value.canCreateResponses) {
                    return
                }

                _state.update {
                    it.copy(
                        pendingResponseAction = PendingResponseAction(
                            type = ResponseActionType.BLOCK_IP,
                            title = "Block IP",
                            targetValue = event.target.value,
                            fieldName = event.target.fieldName
                        ),
                        error = null,
                        responseStatusMessage = null
                    )
                }
            }

            is AlertDetailsEvent.SuccessfulActionTargetLongPressed -> {
                if (!state.value.canCreateResponses) {
                    return
                }

                val normalizedActionName = event.action.actionName.lowercase().trim()

                if (normalizedActionName != ResponseActionType.BLOCK_IP) {
                    return
                }

                _state.update {
                    it.copy(
                        pendingResponseAction = PendingResponseAction(
                            type = ResponseActionType.UNBLOCK_IP,
                            title = "Unblock IP",
                            targetValue = event.action.targetValue,
                            fieldName = null
                        ),
                        error = null,
                        responseStatusMessage = null
                    )
                }
            }

            is AlertDetailsEvent.CreateResponseActionConfirmed -> {
                createResponseAction(event.message)
            }

            is AlertDetailsEvent.ExternalAlertStatusChanged -> {
                applyExternalAlertStatus(
                    alertId = event.alertId,
                    status = event.status
                )
            }
        }
    }

    private fun loadDetails(isRefresh: Boolean) {
        viewModelScope.launch {
            markAlertViewedUseCase(alertId)

            _state.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    error = null
                )
            }

            when (
                val result = getAlertDetailsUseCase(
                    alertId = alertId,
                    spaceName = spaceName
                )
            ) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            details = result.data,
                            error = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.error.message
                        )
                    }
                }
            }
        }
    }

    private fun loadResponsePermissions() {
        viewModelScope.launch {
            val sessionState = checkSessionUseCase()

            val canCreateResponses = if (sessionState is SessionState.Authenticated) {
                ResponsePermissions.canCreateResponse(sessionState.session.roles)
            } else {
                false
            }

            _state.update {
                it.copy(
                    canCreateResponses = canCreateResponses
                )
            }
        }
    }

    private fun updateStatus(newStatus: String) {
        val currentDetails = state.value.details ?: return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isUpdatingStatus = true,
                    error = null,
                    responseStatusMessage = null
                )
            }

            when (
                val result = updateAlertStatusUseCase(
                    alertId = currentDetails.id,
                    currentStatus = currentDetails.status,
                    newStatus = newStatus
                )
            ) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            isUpdatingStatus = false,
                            details = currentDetails.copy(
                                status = result.data
                            ),
                            error = null
                        )
                    }

                    _effect.send(
                        AlertDetailsEffect.AlertStatusUpdated(
                            alertId = currentDetails.id,
                            status = result.data
                        )
                    )
                }

                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            isUpdatingStatus = false,
                            error = result.error.message
                        )
                    }
                }
            }
        }
    }

    private fun createResponseAction(message: String) {
        val action = state.value.pendingResponseAction ?: return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isCreatingResponse = true,
                    error = null,
                    responseStatusMessage = null
                )
            }

            when (
                val result = createResponseActionUseCase(
                    actionType = action.type,
                    alertId = alertId,
                    targetValue = action.targetValue,
                    fieldName = action.fieldName,
                    message = message
                )
            ) {
                is AppResult.Success -> {
                    val newAlertStatus = AlertStatus.EXPECTATION

                    updateCachedAlertStatusUseCase(
                        alertId = alertId,
                        status = newAlertStatus
                    )

                    _state.update { currentState ->
                        currentState.copy(
                            isCreatingResponse = false,
                            pendingResponseAction = null,
                            responseStatusMessage = result.data.status,
                            details = currentState.details?.copy(
                                status = newAlertStatus
                            ),
                            error = null
                        )
                    }

                    _effect.send(
                        AlertDetailsEffect.AlertStatusUpdated(
                            alertId = alertId,
                            status = newAlertStatus
                        )
                    )
                }

                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            isCreatingResponse = false,
                            error = result.error.message
                        )
                    }
                }
            }
        }
    }
    private fun loadSuccessfulActions() {
        viewModelScope.launch {
            when (
                val result = getSuccessfulActionsUseCase(
                    alertId = alertId,
                    page = 0,
                    pageSize = SUCCESSFUL_ACTIONS_PAGE_SIZE
                )
            ) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            successfulActions = result.data.successfulActions
                        )
                    }
                }

                is AppResult.Error -> {

                }
            }
        }
    }

    private fun applyExternalAlertStatus(
        alertId: Long,
        status: String
    ) {
        if (alertId != this.alertId) {
            return
        }

        viewModelScope.launch {
            updateCachedAlertStatusUseCase(
                alertId = alertId,
                status = status
            )

            _state.update { currentState ->
                currentState.copy(
                    details = currentState.details?.copy(
                        status = status
                    )
                )
            }

            _effect.send(
                AlertDetailsEffect.AlertStatusUpdated(
                    alertId = alertId,
                    status = status
                )
            )
        }
    }

    private companion object {
        const val SUCCESSFUL_ACTIONS_PAGE_SIZE = 50
    }
}