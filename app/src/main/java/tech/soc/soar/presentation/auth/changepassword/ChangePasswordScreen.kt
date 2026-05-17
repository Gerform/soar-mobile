package tech.soc.soar.presentation.auth.changepassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import tech.soc.soar.presentation.components.AppScreenScaffold

@Composable
fun ChangePasswordScreen(
    state: ChangePasswordUiState,
    onEvent: (ChangePasswordEvent) -> Unit
) {
    AppScreenScaffold(
        isHomeClickable = false,
        showLogout = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Change password",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.oldPassword,
                onValueChange = {
                    onEvent(ChangePasswordEvent.OldPasswordChanged(it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Old password") },
                isError = state.oldPasswordError != null,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            if (state.oldPasswordError != null) {
                Text(
                    text = state.oldPasswordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.newPassword,
                onValueChange = {
                    onEvent(ChangePasswordEvent.NewPasswordChanged(it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("New password") },
                isError = state.newPasswordError != null,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            if (state.newPasswordError != null) {
                Text(
                    text = state.newPasswordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.generalError != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = state.generalError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.successMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = state.successMessage,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onEvent(ChangePasswordEvent.Submit)
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Change password")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    onEvent(ChangePasswordEvent.BackClicked)
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}