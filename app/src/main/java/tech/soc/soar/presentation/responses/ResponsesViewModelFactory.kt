package tech.soc.soar.presentation.responses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.soc.soar.shared.domain.response.usecase.GetResponseRequestsUseCase

class ResponsesViewModelFactory(
    private val alertId: Long,
    private val getResponseRequestsUseCase: GetResponseRequestsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResponsesViewModel::class.java)) {
            return ResponsesViewModel(
                alertId = alertId,
                getResponseRequestsUseCase = getResponseRequestsUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}