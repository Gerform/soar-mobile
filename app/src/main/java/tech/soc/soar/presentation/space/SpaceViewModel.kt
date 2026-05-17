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
import tech.soc.soar.shared.domain.auth.usecase.LogoutUseCase

class SpaceViewModel(
    private val spaceName: String,
    private val getAlertsPageUseCase: GetAlertsPageUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SpaceUiState())
    val state: StateFlow<SpaceUiState> = _state.asStateFlow()

    private val _effect = Channel<SpaceEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadAlerts(page = 0)
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

            SpaceEvent.NextPageClicked -> {
                if (state.value.hasNextPage && !state.value.isLoading) {
                    loadAlerts(page = state.value.page + 1)
                }
            }

            SpaceEvent.PreviousPageClicked -> {
                if (state.value.page > 0 && !state.value.isLoading) {
                    loadAlerts(page = state.value.page - 1)
                }
            }

            is SpaceEvent.AlertClicked -> {
                viewModelScope.launch {
                    _effect.send(
                        SpaceEffect.NavigateToAlertDetails(
                            alertId = event.alertId
                        )
                    )
                }
            }
        }
    }

    private fun loadAlerts(page: Int) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
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