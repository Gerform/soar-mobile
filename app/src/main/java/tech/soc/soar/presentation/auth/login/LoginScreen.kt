package tech.soc.soar.presentation.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import tech.soc.soar.shared.domain.account.model.SavedAccount

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SOAR",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = {
                onEvent(LoginEvent.UsernameChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Username")
            },
            isError = state.usernameError != null,
            singleLine = true,
            enabled = !state.isUsernameLocked
        )

        if (state.usernameError != null) {
            Text(
                text = state.usernameError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = {
                onEvent(LoginEvent.PasswordChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Password")
            },
            isError = state.passwordError != null,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        if (state.passwordError != null) {
            Text(
                text = state.passwordError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        TextButton(
            onClick = {
                onEvent(LoginEvent.OtherAccountClicked)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log in to another account")
        }

        if (state.generalError != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.generalError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onEvent(LoginEvent.Submit)
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Login")
            }
        }
    }

    if (state.isAccountPickerVisible) {
        AlertDialog(
            onDismissRequest = {
                onEvent(LoginEvent.DismissAccountPicker)
            },
            title = {
                Text("Choose account")
            },
            text = {
                Column {
                    state.savedAccounts.forEach { account ->
                        val isSelected = state.selectedAccountUid == account.uid

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
                                .combinedClickable(
                                    onClick = {
                                        onEvent(LoginEvent.SavedAccountSelected(account))
                                    },
                                    onLongClick = {
                                        onEvent(LoginEvent.SavedAccountLongPressed(account))
                                    }
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = account.username,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = account.mail,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = account.mainRole(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    TextButton(
                        onClick = {
                            onEvent(LoginEvent.AddNewUserClicked)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add new user")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        onEvent(LoginEvent.DismissAccountPicker)
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    val accountPendingDelete = state.accountPendingDelete

    if (accountPendingDelete != null) {
        AlertDialog(
            onDismissRequest = {
                onEvent(LoginEvent.DismissDeleteAccountDialog)
            },
            title = {
                Text("Delete account")
            },
            text = {
                Text(
                    text = "Remove ${accountPendingDelete.username} from this device?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(LoginEvent.ConfirmDeleteAccount)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onEvent(LoginEvent.DismissDeleteAccountDialog)
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

private val REQUIRED_USER_ROLES = setOf(
    "admin",
    "responder",
    "guest",
    "responsible"
)

private fun SavedAccount.mainRole(): String {
    return roles.firstOrNull { it in REQUIRED_USER_ROLES }
        ?: roles.firstOrNull()
        ?: "unknown"
}