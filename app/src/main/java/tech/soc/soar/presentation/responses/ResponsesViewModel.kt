package tech.soc.soar.presentation.responses

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
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.response.model.ResponsePermissions
import tech.soc.soar.shared.domain.response.usecase.DecideResponseRequestUseCase
import tech.soc.soar.shared.domain.response.usecase.GetResponseRequestsUseCase

class ResponsesViewModel(
    private val alertId: Long,
    private val spaceName: String,
    private val getResponseRequestsUseCase: GetResponseRequestsUseCase,
    private val decideResponseRequestUseCase: DecideResponseRequestUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getAlertDetailsUseCase: GetAlertDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ResponsesUiState())
    val state: StateFlow<ResponsesUiState> = _state.asStateFlow()

    private val _effect = Channel<ResponsesEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadPermissions()

        loadResponses(
            page = 0,
            isRefresh = false
        )
    }

    fun onEvent(event: ResponsesEvent) {
        when (event) {
            ResponsesEvent.HomeClicked -> {
                viewModelScope.launch {
                    _effect.send(ResponsesEffect.NavigateToHome)
                }
            }

            ResponsesEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(ResponsesEffect.NavigateBack)
                }
            }

            ResponsesEvent.RefreshTriggered -> {
                if (!state.value.isLoading && !state.value.isRefreshing && state.value.decidingResponseId == null) {
                    loadPermissions()

                    loadResponses(
                        page = state.value.page,
                        isRefresh = true
                    )
                }
            }

            ResponsesEvent.NextPageClicked -> {
                if (state.value.hasNextPage && !state.value.isLoading && !state.value.isRefreshing && state.value.decidingResponseId == null) {
                    loadResponses(
                        page = state.value.page + 1,
                        isRefresh = false
                    )
                }
            }

            ResponsesEvent.PreviousPageClicked -> {
                if (state.value.page > 0 && !state.value.isLoading && !state.value.isRefreshing && state.value.decidingResponseId == null) {
                    loadResponses(
                        page = state.value.page - 1,
                        isRefresh = false
                    )
                }
            }

            is ResponsesEvent.DecisionSelected -> {
                decideResponse(
                    responseRequestId = event.responseRequestId,
                    decision = event.decision
                )
            }
        }
    }

    private fun loadPermissions() {
        viewModelScope.launch {
            val sessionState = checkSessionUseCase()

            val canDecideResponses = if (sessionState is SessionState.Authenticated) {
                ResponsePermissions.canDecideResponse(sessionState.session.roles)
            } else {
                false
            }

            _state.update {
                it.copy(
                    canDecideResponses = canDecideResponses
                )
            }
        }
    }

    private fun loadResponses(
        page: Int,
        isRefresh: Boolean
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    error = null,
                    statusMessage = null
                )
            }

            when (
                val result = getResponseRequestsUseCase(
                    alertId = alertId,
                    page = page,
                    pageSize = PAGE_SIZE
                )
            ) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            responses = result.data.responseRequests,
                            page = result.data.skip / result.data.limit,
                            pageSize = result.data.limit,
                            total = result.data.total,
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

    private fun decideResponse(
        responseRequestId: Long,
        decision: String
    ) {
        if (state.value.decidingResponseId != null) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    decidingResponseId = responseRequestId,
                    error = null,
                    statusMessage = null
                )
            }

            when (
                val result = decideResponseRequestUseCase(
                    responseRequestId = responseRequestId,
                    decision = decision
                )
            ) {
                is AppResult.Success -> {
                    val newResponseStatus = decision.lowercase().trim()

                    _state.update { currentState ->
                        currentState.copy(
                            decidingResponseId = null,
                            responses = currentState.responses.map { response ->
                                if (response.id == responseRequestId) {
                                    response.copy(
                                        status = newResponseStatus
                                    )
                                } else {
                                    response
                                }
                            },
                            statusMessage = result.data.status,
                            error = null
                        )
                    }

                    refreshAlertStatusAfterDecision()
                }

                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            decidingResponseId = null,
                            error = result.error.message
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 50
    }

    private fun refreshAlertStatusAfterDecision() {
        viewModelScope.launch {
            when (
                val result = getAlertDetailsUseCase(
                    alertId = alertId,
                    spaceName = spaceName
                )
            ) {
                is AppResult.Success -> {
                    _effect.send(
                        ResponsesEffect.AlertStatusUpdated(
                            alertId = alertId,
                            status = result.data.status
                        )
                    )
                }

                is AppResult.Error -> {

                }
            }
        }
    }
}