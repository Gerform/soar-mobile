package tech.soc.soar.presentation.space

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
import tech.soc.soar.shared.domain.alert.usecase.GetAlertsPageUseCase
import tech.soc.soar.shared.domain.alert.usecase.MarkAlertViewedUseCase
import tech.soc.soar.shared.domain.auth.usecase.LogoutUseCase

class SpaceViewModel(
    private val spaceName: String,
    private val getAlertsPageUseCase: GetAlertsPageUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val markAlertViewedUseCase: MarkAlertViewedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SpaceUiState())
    val state: StateFlow<SpaceUiState> = _state.asStateFlow()

    private val _effect = Channel<SpaceEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadAlerts(
            page = 0,
            isRefresh = false
        )
    }

    fun onEvent(event: SpaceEvent) {
        when (event) {
            SpaceEvent.HomeClicked -> {
                viewModelScope.launch {
                    _effect.send(SpaceEffect.NavigateToHome)
                }
            }

            SpaceEvent.LogoutClicked -> {
                logout()
            }

            SpaceEvent.RefreshTriggered -> {
                if (!state.value.isLoading && !state.value.isRefreshing) {
                    loadAlerts(
                        page = state.value.page,
                        isRefresh = true
                    )
                }
            }

            SpaceEvent.NextPageClicked -> {
                if (state.value.hasNextPage && !state.value.isLoading && !state.value.isRefreshing) {
                    loadAlerts(
                        page = state.value.page + 1,
                        isRefresh = false
                    )
                }
            }

            SpaceEvent.PreviousPageClicked -> {
                if (state.value.page > 0 && !state.value.isLoading && !state.value.isRefreshing) {
                    loadAlerts(
                        page = state.value.page - 1,
                        isRefresh = false
                    )
                }
            }

            is SpaceEvent.AlertClicked -> {
                viewModelScope.launch {
                    markAlertViewedUseCase(event.alertId)

                    _state.update { currentState ->
                        currentState.copy(
                            alerts = currentState.alerts.map { alert ->
                                if (alert.id == event.alertId) {
                                    alert.copy(isViewed = true)
                                } else {
                                    alert
                                }
                            }
                        )
                    }

                    _effect.send(
                        SpaceEffect.NavigateToAlertDetails(
                            alertId = event.alertId
                        )
                    )
                }
            }

            is SpaceEvent.AlertStatusChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        alerts = currentState.alerts.map { alert ->
                            if (alert.id == event.alertId) {
                                alert.copy(
                                    status = event.status
                                )
                            } else {
                                alert
                            }
                        }
                    )
                }
            }
        }
    }

    private fun loadAlerts(
        page: Int,
        isRefresh: Boolean
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    error = null
                )
            }

            when (
                val result = getAlertsPageUseCase(
                    spaceName = spaceName,
                    page = page,
                    pageSize = PAGE_SIZE
                )
            ) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            alerts = result.data.alerts,
                            page = result.data.page,
                            hasNextPage = result.data.hasNextPage,
                            fromCache = result.data.fromCache,
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

    private fun logout() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            when (logoutUseCase()) {
                is AppResult.Success,
                is AppResult.Error -> {
                    _state.update {
                        it.copy(isLoading = false)
                    }

                    _effect.send(SpaceEffect.NavigateToLogin)
                }
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 50
    }
}