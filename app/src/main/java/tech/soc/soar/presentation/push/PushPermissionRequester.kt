package tech.soc.soar.presentation.push

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.push.usecase.IsMobilePushEnabledForAccountUseCase
import tech.soc.soar.shared.domain.push.usecase.MarkMobilePushEnabledForAccountUseCase
import tech.soc.soar.shared.domain.push.usecase.RegisterMobilePushTokenUseCase

@Composable
fun PushPermissionRequester(
    accountUid: String,
    registerMobilePushTokenUseCase: RegisterMobilePushTokenUseCase,
    isMobilePushEnabledForAccountUseCase: IsMobilePushEnabledForAccountUseCase,
    markMobilePushEnabledForAccountUseCase: MarkMobilePushEnabledForAccountUseCase
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val deviceId = remember {
        getDeviceId(context)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var shouldRegisterAfterPermissionGranted by remember {
        mutableStateOf(false)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && shouldRegisterAfterPermissionGranted) {
            shouldRegisterAfterPermissionGranted = false

            coroutineScope.launch {
                registerPushTokenForAccount(
                    context = context,
                    accountUid = accountUid,
                    deviceId = deviceId,
                    registerMobilePushTokenUseCase = registerMobilePushTokenUseCase,
                    markMobilePushEnabledForAccountUseCase = markMobilePushEnabledForAccountUseCase
                )
            }
        } else {
            shouldRegisterAfterPermissionGranted = false
        }
    }

    LaunchedEffect(accountUid, deviceId) {
        if (accountUid.isBlank()) {
            return@LaunchedEffect
        }

        val alreadyEnabledForThisAccount = withContext(Dispatchers.IO) {
            isMobilePushEnabledForAccountUseCase(
                accountUid = accountUid,
                deviceId = deviceId
            )
        }

        if (!alreadyEnabledForThisAccount) {
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text("Enable notifications?")
            },
            text = {
                Text("Do you want to receive SOAR notifications for this account on this phone?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false

                        if (hasNotificationPermission(context)) {
                            coroutineScope.launch {
                                registerPushTokenForAccount(
                                    context = context,
                                    accountUid = accountUid,
                                    deviceId = deviceId,
                                    registerMobilePushTokenUseCase = registerMobilePushTokenUseCase,
                                    markMobilePushEnabledForAccountUseCase = markMobilePushEnabledForAccountUseCase
                                )
                            }
                        } else {
                            shouldRegisterAfterPermissionGranted = true

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }
                ) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Not now")
                }
            }
        )
    }
}

private fun hasNotificationPermission(
    context: Context
): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

private suspend fun registerPushTokenForAccount(
    context: Context,
    accountUid: String,
    deviceId: String,
    registerMobilePushTokenUseCase: RegisterMobilePushTokenUseCase,
    markMobilePushEnabledForAccountUseCase: MarkMobilePushEnabledForAccountUseCase
) {
    withContext(Dispatchers.IO) {
        val token = FirebaseMessaging.getInstance()
            .token
            .await()

        val deviceName = getDeviceName()

        when (
            registerMobilePushTokenUseCase(
                token = token,
                deviceId = deviceId,
                deviceName = deviceName
            )
        ) {
            is AppResult.Success -> {
                markMobilePushEnabledForAccountUseCase(
                    accountUid = accountUid,
                    deviceId = deviceId
                )
            }

            is AppResult.Error -> {
            }
        }
    }
}

private fun getDeviceId(
    context: Context
): String {
    return Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "unknown"
}

private fun getDeviceName(): String {
    return listOf(
        Build.MANUFACTURER,
        Build.MODEL
    )
        .joinToString(separator = " ")
        .trim()
        .ifBlank {
            "Android device"
        }
}