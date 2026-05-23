package tech.soc.soar.presentation.alertdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.soc.soar.shared.domain.alert.usecase.GetAlertDetailsUseCase
import tech.soc.soar.shared.domain.alert.usecase.MarkAlertViewedUseCase
import tech.soc.soar.shared.domain.alert.usecase.UpdateAlertStatusUseCase

class AlertDetailsViewModelFactory(
    private val alertId: Long,
    private val spaceName: String,
    private val getAlertDetailsUseCase: GetAlertDetailsUseCase,
    private val markAlertViewedUseCase: MarkAlertViewedUseCase,
    private val updateAlertStatusUseCase: UpdateAlertStatusUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertDetailsViewModel::class.java)) {
            return AlertDetailsViewModel(
                alertId = alertId,
                spaceName = spaceName,
                getAlertDetailsUseCase = getAlertDetailsUseCase,
                markAlertViewedUseCase = markAlertViewedUseCase,
                updateAlertStatusUseCase = updateAlertStatusUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}