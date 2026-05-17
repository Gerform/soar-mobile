package tech.soc.soar.di

import android.content.Context
import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.data.account.repository.AccountRepositoryFactory
import tech.soc.soar.shared.data.alert.remote.AlertApi
import tech.soc.soar.shared.data.alert.remote.AlertApiFactory
import tech.soc.soar.shared.data.alert.repository.AlertRepositoryFactory
import tech.soc.soar.shared.data.auth.local.AuthTokenProvider
import tech.soc.soar.shared.data.auth.local.EncryptedTokenStorage
import tech.soc.soar.shared.data.auth.local.UserSessionCache
import tech.soc.soar.shared.data.auth.remote.AuthApi
import tech.soc.soar.shared.data.auth.remote.AuthApiFactory
import tech.soc.soar.shared.data.auth.repository.AuthRepositoryImpl
import tech.soc.soar.shared.data.auth.repository.SessionRepositoryImpl
import tech.soc.soar.shared.domain.account.repository.AccountRepository
import tech.soc.soar.shared.domain.account.usecase.DeleteSavedAccountUseCase
import tech.soc.soar.shared.domain.account.usecase.GetLastUsedAccountUseCase
import tech.soc.soar.shared.domain.account.usecase.GetSavedAccountsUseCase
import tech.soc.soar.shared.domain.alert.repository.AlertRepository
import tech.soc.soar.shared.domain.alert.usecase.GetAlertsPageUseCase
import tech.soc.soar.shared.domain.auth.repository.AuthRepository
import tech.soc.soar.shared.domain.auth.repository.SessionRepository
import tech.soc.soar.shared.domain.auth.usecase.ChangePasswordUseCase
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.auth.usecase.ConfirmTwoFactorUseCase
import tech.soc.soar.shared.domain.auth.usecase.LoginUseCase
import tech.soc.soar.shared.domain.auth.usecase.LogoutUseCase
import tech.soc.soar.shared.domain.auth.usecase.RefreshSessionUseCase

object AppDependencies {

    private const val AUTH_BASE_URL = "http://192.168.0.244:8000"
    private const val ALERT_BASE_URL = "http://192.168.0.244:8080"

    private var initialized: Boolean = false

    lateinit var authApi: AuthApi
        private set

    lateinit var alertApi: AlertApi
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var alertRepository: AlertRepository
        private set

    lateinit var sessionRepository: SessionRepository
        private set

    lateinit var loginUseCase: LoginUseCase
        private set

    lateinit var confirmTwoFactorUseCase: ConfirmTwoFactorUseCase
        private set

    lateinit var checkSessionUseCase: CheckSessionUseCase
        private set

    lateinit var refreshSessionUseCase: RefreshSessionUseCase
        private set

    lateinit var logoutUseCase: LogoutUseCase
        private set

    lateinit var changePasswordUseCase: ChangePasswordUseCase
        private set

    lateinit var getSavedAccountsUseCase: GetSavedAccountsUseCase
        private set

    lateinit var getLastUsedAccountUseCase: GetLastUsedAccountUseCase
        private set

    lateinit var deleteSavedAccountUseCase: DeleteSavedAccountUseCase
        private set

    lateinit var getAlertsPageUseCase: GetAlertsPageUseCase
        private set

    fun initialize(context: Context) {
        if (initialized) return

        val appContext = context.applicationContext

        val tokenStorage = EncryptedTokenStorage(
            context = appContext
        )

        val userSessionCache = UserSessionCache()

        val tokenProvider = AuthTokenProvider(
            tokenStorage = tokenStorage
        )

        authApi = AuthApiFactory.create(
            apiConfig = ApiConfig(
                baseUrl = AUTH_BASE_URL
            ),
            tokenProvider = tokenProvider
        )

        alertApi = AlertApiFactory.create(
            apiConfig = ApiConfig(
                baseUrl = ALERT_BASE_URL
            ),
            tokenProvider = tokenProvider
        )

        val accountRepository: AccountRepository = AccountRepositoryFactory.create(
            context = appContext
        )

        alertRepository = AlertRepositoryFactory.create(
            context = appContext,
            alertApi = alertApi
        )

        authRepository = AuthRepositoryImpl(
            authApi = authApi
        )

        sessionRepository = SessionRepositoryImpl(
            tokenStorage = tokenStorage,
            userSessionCache = userSessionCache,
            accountRepository = accountRepository
        )

        loginUseCase = LoginUseCase(
            authRepository = authRepository,
            sessionRepository = sessionRepository
        )

        confirmTwoFactorUseCase = ConfirmTwoFactorUseCase(
            authRepository = authRepository,
            sessionRepository = sessionRepository
        )

        checkSessionUseCase = CheckSessionUseCase(
            sessionRepository = sessionRepository
        )

        refreshSessionUseCase = RefreshSessionUseCase(
            authRepository = authRepository,
            sessionRepository = sessionRepository
        )

        logoutUseCase = LogoutUseCase(
            authRepository = authRepository,
            sessionRepository = sessionRepository
        )

        changePasswordUseCase = ChangePasswordUseCase(
            authRepository = authRepository
        )

        getSavedAccountsUseCase = GetSavedAccountsUseCase(
            accountRepository = accountRepository
        )

        getLastUsedAccountUseCase = GetLastUsedAccountUseCase(
            accountRepository = accountRepository
        )

        deleteSavedAccountUseCase = DeleteSavedAccountUseCase(
            accountRepository = accountRepository
        )

        getAlertsPageUseCase = GetAlertsPageUseCase(
            alertRepository = alertRepository,
            checkSessionUseCase = checkSessionUseCase,
            refreshSessionUseCase = refreshSessionUseCase,
            getLastUsedAccountUseCase = getLastUsedAccountUseCase
        )

        initialized = true
    }
}