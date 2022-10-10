package com.mzzlab.sample.biometric.ui.screen.login

import android.content.res.Resources
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.mzzlab.sample.biometric.R
import com.mzzlab.sample.biometric.common.CryptoPurpose
import com.mzzlab.sample.biometric.ui.navigation.HomeRoute
import com.mzzlab.sample.biometric.ui.navigation.LoginRoute
import com.mzzlab.sample.biometric.ui.theme.BiometricAppTheme

@ExperimentalLifecycleComposeApi
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onUserLoginReady: () -> Unit = {},
) {
    val uiState: LoginUIState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigateToHome by remember(uiState) {
        derivedStateOf { uiState.loggedIn && !uiState.askBiometricEnrollment }
    }
    val focusManager = LocalFocusManager.current

    if (navigateToHome) {
        LaunchedEffect(key1 = Unit) {
            onUserLoginReady()
        }
    }

    val promptContainerState = rememberPromptContainerState()
    BiometricPromptContainer(
        promptContainerState,
        onAuthSucceeded = { cryptoObj ->
            viewModel.onAuthSucceeded(cryptoObj)
        },
        onAuthError = { authErr ->
            viewModel.onAuthError(authErr.errorCode, authErr.errString)
        }
    )

    uiState.authContext?.let { auth ->
        val resources = LocalContext.current.resources
        LaunchedEffect(key1 = auth) {
            val promptInfo = createPromptInfo(auth.purpose, resources)
            promptContainerState.authenticate(promptInfo, auth.cryptoObject)
        }
    }

    Column(
        modifier = modifier.padding(top = 120.dp),
        horizontalAlignment = CenterHorizontally,
    ) {
        OutlinedTextField(
            value = uiState.usernameField,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            placeholder = {
                Text(text = stringResource(id = R.string.username_placeholder))
            },
            onValueChange = {
                viewModel.setUsername(it)
            })

        FormSpacer()

        OutlinedTextField(
            value = uiState.passwordField,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus(force = true)
                    viewModel.doLogin()
                }
            ),
            visualTransformation = PasswordVisualTransformation(),
            placeholder = {
                Text(text = stringResource(id = R.string.password_placeholder))
            },
            onValueChange = {
                viewModel.setPassword(it)
            })

        FormSpacer()

        Button(
            onClick = {
                focusManager.clearFocus(force = true)
                viewModel.doLogin()
            }
        ) {
            Text(text = stringResource(id = R.string.login_button_text))
        }

        FormSpacer()

        UseBiometricLoginButton(
            visible = uiState.canLoginWithBiometry,
            onClick = {
                viewModel.requireBiometricLogin()
            }
        )
    }
}

@Composable
fun FormSpacer() {
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun UseBiometricLoginButton(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onClick: () -> Unit
) {
    if (visible) {
        OutlinedButton(
            modifier = modifier,
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_fingerprint_24),
                contentDescription = stringResource(id = R.string.button_biometric_content_descr),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.button_biometric_text))

        }
    }
}


@ExperimentalLifecycleComposeApi
fun NavGraphBuilder.addLoginRoute(navController: NavController) {
    composable(
        route = LoginRoute.route
    ) {
        val viewModel: LoginViewModel = hiltViewModel()
        LoginScreen(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            onUserLoginReady = {
                navController.navigate(
                    route = HomeRoute.route,
                    navOptions = NavOptions
                        .Builder()
                        .setPopUpTo(LoginRoute.route, true)
                        .build()
                )
            }
        )
    }
}

@Composable
@ExperimentalLifecycleComposeApi
@Preview(name = "Login Preview")
fun LoginScreenPreview() {
    BiometricAppTheme {
        Surface(Modifier.fillMaxSize()) {
            LoginScreen()
        }
    }
}

fun createPromptInfo(purpose: CryptoPurpose, resources: Resources): BiometricPrompt.PromptInfo {
    return if (purpose == CryptoPurpose.Encryption) {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.prompt_title_enroll_token))
            .setSubtitle(resources.getString(R.string.prompt_subtitle_enroll_token))
            .setNegativeButtonText(resources.getString(R.string.prompt_cancel))
            .build()
    } else {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.prompt_title_login))
            .setSubtitle(resources.getString(R.string.prompt_subtitle_login))
            .setNegativeButtonText(resources.getString(R.string.prompt_cancel))
            .build()
    }
}

