package tech.soc.soar.presentation.alertdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.soc.soar.shared.domain.alert.usecase.GetAlertDetailsUseCase
import tech.soc.soar.shared.domain.alert.usecase.MarkAlertViewedUseCase
import tech.soc.soar.shared.domain.alert.usecase.UpdateAlertStatusUseCase
import tech.soc.soar.shared.domain.alert.usecase.UpdateCachedAlertStatusUseCase
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.response.usecase.CreateResponseActionUseCase
import tech.soc.soar.shared.domain.response.usecase.GetSuccessfulActionsUseCase

class AlertDetailsViewModelFactory(
    private val alertId: Long,
    private val spaceName: String,
    private val getAlertDetailsUseCase: GetAlertDetailsUseCase,
    private val markAlertViewedUseCase: MarkAlertViewedUseCase,
    private val updateAlertStatusUseCase: UpdateAlertStatusUseCase,
    private val createResponseActionUseCase: CreateResponseActionUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val updateCachedAlertStatusUseCase: UpdateCachedAlertStatusUseCase,
    private val getSuccessfulActionsUseCase: GetSuccessfulActionsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertDetailsViewModel::class.java)) {
            return AlertDetailsViewModel(
                alertId = alertId,
                spaceName = spaceName,
                getAlertDetailsUseCase = getAlertDetailsUseCase,
                markAlertViewedUseCase = markAlertViewedUseCase,
                updateAlertStatusUseCase = updateAlertStatusUseCase,
                createResponseActionUseCase = createResponseActionUseCase,
                checkSessionUseCase = checkSessionUseCase,
                updateCachedAlertStatusUseCase = updateCachedAlertStatusUseCase,
                getSuccessfulActionsUseCase = getSuccessfulActionsUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}