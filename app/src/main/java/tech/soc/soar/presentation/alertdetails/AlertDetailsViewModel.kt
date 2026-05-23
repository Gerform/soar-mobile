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

class AlertDetailsViewModel(
    private val alertId: Long,
    private val spaceName: String,
    private val getAlertDetailsUseCase: GetAlertDetailsUseCase,
    private val markAlertViewedUseCase: MarkAlertViewedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AlertDetailsUiState())
    val state: StateFlow<AlertDetailsUiState> = _state.asStateFlow()

    private val _effect = Channel<AlertDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadDetails()
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
        }
    }

    private fun loadDetails() {
        viewModelScope.launch {
            markAlertViewedUseCase(alertId)

            _state.update {
                it.copy(
                    isLoading = true,
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
                            details = result.data,
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
}