package tech.soc.soar.presentation.responses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.soc.soar.shared.domain.alert.usecase.GetAlertDetailsUseCase
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.response.usecase.DecideResponseRequestUseCase
import tech.soc.soar.shared.domain.response.usecase.GetResponseRequestsUseCase

class ResponsesViewModelFactory(
    private val alertId: Long,
    private val spaceName: String,
    private val getResponseRequestsUseCase: GetResponseRequestsUseCase,
    private val decideResponseRequestUseCase: DecideResponseRequestUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getAlertDetailsUseCase: GetAlertDetailsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResponsesViewModel::class.java)) {
            return ResponsesViewModel(
                alertId = alertId,
                spaceName = spaceName,
                getResponseRequestsUseCase = getResponseRequestsUseCase,
                decideResponseRequestUseCase = decideResponseRequestUseCase,
                checkSessionUseCase = checkSessionUseCase,
                getAlertDetailsUseCase = getAlertDetailsUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}