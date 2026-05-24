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
import tech.soc.soar.shared.domain.response.usecase.GetResponseRequestsUseCase

class ResponsesViewModel(
    private val alertId: Long,
    private val getResponseRequestsUseCase: GetResponseRequestsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ResponsesUiState())
    val state: StateFlow<ResponsesUiState> = _state.asStateFlow()

    private val _effect = Channel<ResponsesEffect>()
    val effect = _effect.receiveAsFlow()

    init {
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
                if (!state.value.isLoading && !state.value.isRefreshing) {
                    loadResponses(
                        page = state.value.page,
                        isRefresh = true
                    )
                }
            }

            ResponsesEvent.NextPageClicked -> {
                if (state.value.hasNextPage && !state.value.isLoading && !state.value.isRefreshing) {
                    loadResponses(
                        page = state.value.page + 1,
                        isRefresh = false
                    )
                }
            }

            ResponsesEvent.PreviousPageClicked -> {
                if (state.value.page > 0 && !state.value.isLoading && !state.value.isRefreshing) {
                    loadResponses(
                        page = state.value.page - 1,
                        isRefresh = false
                    )
                }
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
                    error = null
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

    private companion object {
        const val PAGE_SIZE = 50
    }
}