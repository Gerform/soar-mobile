package tech.soc.soar.di

import android.content.Context
import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.data.auth.local.AuthTokenProvider
import tech.soc.soar.shared.data.auth.local.EncryptedTokenStorage
import tech.soc.soar.shared.data.auth.local.UserSessionCache
import tech.soc.soar.shared.data.auth.remote.AuthApi
import tech.soc.soar.shared.data.auth.remote.AuthApiFactory
import tech.soc.soar.shared.data.auth.repository.AuthRepositoryImpl
import tech.soc.soar.shared.data.auth.repository.SessionRepositoryImpl
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

    private var initialized: Boolean = false

    lateinit var authApi: AuthApi
        private set

    lateinit var authRepository: AuthRepository
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

    fun initialize(context: Context) {
        if (initialized) return

        val appContext = context.applicationContext

        val apiConfig = ApiConfig(
            baseUrl = AUTH_BASE_URL
        )

        val tokenStorage = EncryptedTokenStorage(
            context = appContext
        )

        val userSessionCache = UserSessionCache()

        val tokenProvider = AuthTokenProvider(
            tokenStorage = tokenStorage
        )

        authApi = AuthApiFactory.create(
            apiConfig = apiConfig,
            tokenProvider = tokenProvider
        )

        authRepository = AuthRepositoryImpl(
            authApi = authApi
        )

        sessionRepository = SessionRepositoryImpl(
            tokenStorage = tokenStorage,
            userSessionCache = userSessionCache
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

        initialized = true
    }}