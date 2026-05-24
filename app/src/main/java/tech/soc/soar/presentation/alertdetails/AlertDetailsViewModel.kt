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
import tech.soc.soar.shared.domain.alert.usecase.GetAlertDetailsUseCase
import tech.soc.soar.shared.domain.alert.usecase.MarkAlertViewedUseCase
import tech.soc.soar.shared.domain.alert.usecase.UpdateAlertStatusUseCase

class AlertDetailsViewModel(
    private val alertId: Long,
    private val spaceName: String,
    private val getAlertDetailsUseCase: GetAlertDetailsUseCase,
    private val markAlertViewedUseCase: MarkAlertViewedUseCase,
    private val updateAlertStatusUseCase: UpdateAlertStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AlertDetailsUiState())
    val state: StateFlow<AlertDetailsUiState> = _state.asStateFlow()

    private val _effect = Channel<AlertDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadDetails(isRefresh = false)
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
                if (!state.value.isLoading && !state.value.isRefreshing && !state.value.isUpdatingStatus) {
                    loadDetails(isRefresh = true)
                }
            }

            is AlertDetailsEvent.StatusSelected -> {
                updateStatus(event.status)
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

    private fun updateStatus(newStatus: String) {
        val currentDetails = state.value.details ?: return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isUpdatingStatus = true,
                    error = null
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
}