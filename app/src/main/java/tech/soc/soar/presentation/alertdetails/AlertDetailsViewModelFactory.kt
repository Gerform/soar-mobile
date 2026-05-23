package tech.soc.soar.presentation.alertdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.soc.soar.shared.domain.alert.usecase.GetAlertDetailsUseCase
import tech.soc.soar.shared.domain.alert.usecase.MarkAlertViewedUseCase

class AlertDetailsViewModelFactory(
    private val alertId: Long,
    private val spaceName: String,
    private val getAlertDetailsUseCase: GetAlertDetailsUseCase,
    private val markAlertViewedUseCase: MarkAlertViewedUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertDetailsViewModel::class.java)) {
            return AlertDetailsViewModel(
                alertId = alertId,
                spaceName = spaceName,
                getAlertDetailsUseCase = getAlertDetailsUseCase,
                markAlertViewedUseCase = markAlertViewedUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}