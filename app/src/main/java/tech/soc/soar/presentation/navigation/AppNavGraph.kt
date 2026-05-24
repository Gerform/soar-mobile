package tech.soc.soar.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tech.soc.soar.di.AppDependencies
import tech.soc.soar.presentation.alertdetails.AlertDetailsEffect
import tech.soc.soar.presentation.alertdetails.AlertDetailsScreen
import tech.soc.soar.presentation.alertdetails.AlertDetailsViewModel
import tech.soc.soar.presentation.alertdetails.AlertDetailsViewModelFactory
import tech.soc.soar.presentation.auth.changepassword.ChangePasswordEffect
import tech.soc.soar.presentation.auth.changepassword.ChangePasswordScreen
import tech.soc.soar.presentation.auth.changepassword.ChangePasswordViewModel
import tech.soc.soar.presentation.auth.changepassword.ChangePasswordViewModelFactory
import tech.soc.soar.presentation.auth.login.LoginEffect
import tech.soc.soar.presentation.auth.login.LoginScreen
import tech.soc.soar.presentation.auth.login.LoginViewModel
import tech.soc.soar.presentation.auth.login.LoginViewModelFactory
import tech.soc.soar.presentation.auth.twofactor.TwoFactorEffect
import tech.soc.soar.presentation.auth.twofactor.TwoFactorScreen
import tech.soc.soar.presentation.auth.twofactor.TwoFactorViewModel
import tech.soc.soar.presentation.auth.twofactor.TwoFactorViewModelFactory
import tech.soc.soar.presentation.home.HomeEffect
import tech.soc.soar.presentation.home.HomeScreen
import tech.soc.soar.presentation.home.HomeViewModel
import tech.soc.soar.presentation.home.HomeViewModelFactory
import tech.soc.soar.presentation.responses.ResponsesScreen
import tech.soc.soar.presentation.root.RootUiState
import tech.soc.soar.presentation.root.RootViewModel
import tech.soc.soar.presentation.space.SpaceEffect
import tech.soc.soar.presentation.space.SpaceEvent
import tech.soc.soar.presentation.space.SpaceScreen
import tech.soc.soar.presentation.space.SpaceViewModel
import tech.soc.soar.presentation.space.SpaceViewModelFactory

private const val UPDATED_ALERT_STATUS_KEY = "updated_alert_status"

@Composable
fun AppNavGraph(
    rootViewModel: RootViewModel
) {
    val rootState by rootViewModel.state.collectAsState()
    val navController = rememberNavController()

    when (rootState) {
        RootUiState.Loading -> {
            LoadingScreen()
        }

        else -> {
            val startDestination = when (rootState) {
                RootUiState.Unauthenticated -> AppRoutes.LOGIN
                RootUiState.RequiresTwoFactor -> AppRoutes.TWO_FACTOR
                RootUiState.Authenticated -> AppRoutes.HOME
                RootUiState.Loading -> AppRoutes.LOGIN
            }

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable(AppRoutes.LOGIN) {
                    val loginViewModel: LoginViewModel = viewModel(
                        factory = LoginViewModelFactory(
                            loginUseCase = AppDependencies.loginUseCase,
                            getSavedAccountsUseCase = AppDependencies.getSavedAccountsUseCase,
                            getLastUsedAccountUseCase = AppDependencies.getLastUsedAccountUseCase,
                            deleteSavedAccountUseCase = AppDependencies.deleteSavedAccountUseCase
                        )
                    )

                    val loginState by loginViewModel.state.collectAsState()

                    LaunchedEffect(Unit) {
                        loginViewModel.effect.collect { effect ->
                            when (effect) {
                                LoginEffect.NavigateToHome -> {
                                    rootViewModel.onLoggedIn()

                                    navController.navigate(AppRoutes.HOME) {
                                        popUpTo(AppRoutes.LOGIN) {
                                            inclusive = true
                                        }
                                    }
                                }

                                LoginEffect.NavigateToTwoFactor -> {
                                    rootViewModel.onLoggedIn()

                                    navController.navigate(AppRoutes.TWO_FACTOR) {
                                        popUpTo(AppRoutes.LOGIN) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LoginScreen(
                        state = loginState,
                        onEvent = loginViewModel::onEvent
                    )
                }

                composable(AppRoutes.TWO_FACTOR) {
                    val twoFactorViewModel: TwoFactorViewModel = viewModel(
                        factory = TwoFactorViewModelFactory(
                            confirmTwoFactorUseCase = AppDependencies.confirmTwoFactorUseCase
                        )
                    )

                    val twoFactorState by twoFactorViewModel.state.collectAsState()

                    LaunchedEffect(Unit) {
                        twoFactorViewModel.effect.collect { effect ->
                            when (effect) {
                                TwoFactorEffect.NavigateToHome -> {
                                    rootViewModel.onTwoFactorConfirmed()

                                    navController.navigate(AppRoutes.HOME) {
                                        popUpTo(AppRoutes.TWO_FACTOR) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        }
                    }

                    TwoFactorScreen(
                        state = twoFactorState,
                        onEvent = twoFactorViewModel::onEvent
                    )
                }

                composable(AppRoutes.HOME) {
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModelFactory(
                            logoutUseCase = AppDependencies.logoutUseCase,
                            checkSessionUseCase = AppDependencies.checkSessionUseCase
                        )
                    )

                    val homeState by homeViewModel.state.collectAsState()

                    LaunchedEffect(Unit) {
                        homeViewModel.effect.collect { effect ->
                            when (effect) {
                                HomeEffect.NavigateToHome -> {
                                    navController.navigate(AppRoutes.HOME) {
                                        popUpTo(AppRoutes.HOME) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }

                                is HomeEffect.NavigateToSpace -> {
                                    navController.navigate(AppRoutes.space(effect.spaceName))
                                }

                                HomeEffect.NavigateToLogin -> {
                                    rootViewModel.onLoggedOut()

                                    navController.navigate(AppRoutes.LOGIN) {
                                        popUpTo(0) {
                                            inclusive = true
                                        }
                                    }
                                }

                                HomeEffect.NavigateToChangePassword -> {
                                    navController.navigate(AppRoutes.CHANGE_PASSWORD)
                                }
                            }
                        }
                    }

                    HomeScreen(
                        state = homeState,
                        onEvent = homeViewModel::onEvent
                    )
                }

                composable(AppRoutes.CHANGE_PASSWORD) {
                    val changePasswordViewModel: ChangePasswordViewModel = viewModel(
                        factory = ChangePasswordViewModelFactory(
                            changePasswordUseCase = AppDependencies.changePasswordUseCase
                        )
                    )

                    val changePasswordState by changePasswordViewModel.state.collectAsState()

                    LaunchedEffect(Unit) {
                        changePasswordViewModel.effect.collect { effect ->
                            when (effect) {
                                ChangePasswordEffect.NavigateBack -> {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }

                    ChangePasswordScreen(
                        state = changePasswordState,
                        onEvent = changePasswordViewModel::onEvent
                    )
                }

                composable(AppRoutes.SPACE) { backStackEntry ->
                    val spaceName = backStackEntry.arguments
                        ?.getString(AppRoutes.SPACE_ARGUMENT)
                        ?: ""

                    val spaceViewModel: SpaceViewModel = viewModel(
                        factory = SpaceViewModelFactory(
                            spaceName = spaceName,
                            getAlertsPageUseCase = AppDependencies.getAlertsPageUseCase,
                            logoutUseCase = AppDependencies.logoutUseCase,
                            markAlertViewedUseCase = AppDependencies.markAlertViewedUseCase
                        )
                    )

                    val spaceState by spaceViewModel.state.collectAsState()

                    val updatedAlertStatus by backStackEntry.savedStateHandle
                        .getStateFlow(UPDATED_ALERT_STATUS_KEY, "")
                        .collectAsState()

                    LaunchedEffect(updatedAlertStatus) {
                        if (updatedAlertStatus.isBlank()) {
                            return@LaunchedEffect
                        }

                        val parts = updatedAlertStatus.split("|")

                        if (parts.size == 2) {
                            val alertId = parts[0].toLongOrNull()
                            val status = parts[1]

                            if (alertId != null) {
                                spaceViewModel.onEvent(
                                    SpaceEvent.AlertStatusChanged(
                                        alertId = alertId,
                                        status = status
                                    )
                                )
                            }
                        }

                        backStackEntry.savedStateHandle[UPDATED_ALERT_STATUS_KEY] = ""
                    }

                    LaunchedEffect(Unit) {
                        spaceViewModel.effect.collect { effect ->
                            when (effect) {
                                SpaceEffect.NavigateToHome -> {
                                    navController.navigate(AppRoutes.HOME) {
                                        popUpTo(AppRoutes.HOME) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }

                                SpaceEffect.NavigateToLogin -> {
                                    rootViewModel.onLoggedOut()

                                    navController.navigate(AppRoutes.LOGIN) {
                                        popUpTo(0) {
                                            inclusive = true
                                        }
                                    }
                                }

                                is SpaceEffect.NavigateToAlertDetails -> {
                                    navController.navigate(
                                        AppRoutes.alertDetails(
                                            spaceName = spaceName,
                                            alertId = effect.alertId
                                        )
                                    )
                                }
                            }
                        }
                    }

                    SpaceScreen(
                        spaceName = spaceName,
                        state = spaceState,
                        onEvent = spaceViewModel::onEvent
                    )
                }

                composable(AppRoutes.ALERT_DETAILS) { backStackEntry ->
                    val spaceName = backStackEntry.arguments
                        ?.getString(AppRoutes.SPACE_ARGUMENT)
                        .orEmpty()

                    val alertId = backStackEntry.arguments
                        ?.getString(AppRoutes.ALERT_ID_ARGUMENT)
                        ?.toLongOrNull()
                        ?: 0L

                    val alertDetailsViewModel: AlertDetailsViewModel = viewModel(
                        factory = AlertDetailsViewModelFactory(
                            alertId = alertId,
                            spaceName = spaceName,
                            getAlertDetailsUseCase = AppDependencies.getAlertDetailsUseCase,
                            markAlertViewedUseCase = AppDependencies.markAlertViewedUseCase,
                            updateAlertStatusUseCase = AppDependencies.updateAlertStatusUseCase,
                            createBlockIpResponseUseCase = AppDependencies.createBlockIpResponseUseCase,
                            checkSessionUseCase = AppDependencies.checkSessionUseCase,
                            updateCachedAlertStatusUseCase = AppDependencies.updateCachedAlertStatusUseCase
                        )
                    )

                    val alertDetailsState by alertDetailsViewModel.state.collectAsState()

                    LaunchedEffect(Unit) {
                        alertDetailsViewModel.effect.collect { effect ->
                            when (effect) {
                                AlertDetailsEffect.NavigateBack -> {
                                    navController.popBackStack()
                                }

                                AlertDetailsEffect.NavigateToHome -> {
                                    navController.navigate(AppRoutes.HOME) {
                                        popUpTo(AppRoutes.HOME) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }

                                is AlertDetailsEffect.AlertStatusUpdated -> {
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(
                                            UPDATED_ALERT_STATUS_KEY,
                                            "${effect.alertId}|${effect.status}"
                                        )
                                }

                                AlertDetailsEffect.NavigateToResponses -> {
                                    navController.navigate(
                                        AppRoutes.responses(
                                            spaceName = spaceName,
                                            alertId = alertId
                                        )
                                    )
                                }
                            }
                        }
                    }

                    AlertDetailsScreen(
                        state = alertDetailsState,
                        onEvent = alertDetailsViewModel::onEvent
                    )
                }

                composable(AppRoutes.RESPONSES) {
                    ResponsesScreen(
                        onHomeClick = {
                            navController.navigate(AppRoutes.HOME) {
                                popUpTo(AppRoutes.HOME) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}