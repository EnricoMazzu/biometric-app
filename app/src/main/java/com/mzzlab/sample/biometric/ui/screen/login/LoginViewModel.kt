package com.mzzlab.sample.biometric.ui.screen.login

import androidx.annotation.StringRes
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mzzlab.sample.biometric.R
import com.mzzlab.sample.biometric.common.CryptoPurpose
import com.mzzlab.sample.biometric.common.getResult
import com.mzzlab.sample.biometric.common.switch
import com.mzzlab.sample.biometric.data.BiometricRepository
import com.mzzlab.sample.biometric.data.UserRepository
import com.mzzlab.sample.biometric.data.error.InvalidCryptoLayerException
import com.mzzlab.sample.biometric.data.model.BiometricInfo
import com.mzzlab.sample.biometric.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val biometricRepository: BiometricRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginUIState> = MutableStateFlow(
        LoginUIState(
            loggedIn = userRepository.isUserLoggedIn.value
        )
    )
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.isUserLoggedIn
                .map { Pair(it, biometricRepository.getBiometricInfo()) }
                .collect { info -> reduceState(info.first, info.second) }
        }
    }

    private suspend fun reduceState(isLoggedIn: Boolean, biometricInfo: BiometricInfo) {
        val currentState = uiState.value
        val askBiometricEnrollment =
            shouldAskTokenEnrollment(isLoggedIn, currentState, biometricInfo)
        var authContext: AuthContext? = currentState.authContext
        // we want to check if enrollment is ok or not
        if (askBiometricEnrollment) {
            getResult { prepareAuthContext(CryptoPurpose.Encryption) }
                .switch(
                    success = { authContext = it },
                    error = {
                        Timber.e(it)
                    }
                )
        }
        // update state
        _uiState.update {
            it.copy(
                loggedIn = isLoggedIn,
                canLoginWithBiometry = canLoginWithBiometricToken(biometricInfo),
                askBiometricEnrollment = askBiometricEnrollment,
                authContext = authContext
            )
        }
    }

    private fun canLoginWithBiometricToken(biometricInfo: BiometricInfo) =
        (biometricInfo.biometricTokenPresent
                && biometricInfo.canAskAuthentication())

    private fun shouldAskTokenEnrollment(
        isLoggedIn: Boolean,
        currentState: LoginUIState,
        biometricInfo: BiometricInfo
    ) = (isLoggedIn && !currentState.askBiometricEnrollment
            && !biometricInfo.biometricTokenPresent
            && biometricInfo.canAskAuthentication())


    fun setUsername(username: String) {
        _uiState.value = _uiState.value.copy(usernameField = username)
    }

    fun setPassword(password: String) {
        _uiState.value = _uiState.value.copy(passwordField = password)
    }

    fun doLogin() {
        val username = _uiState.value.usernameField
        val password = _uiState.value.passwordField
        if (username.isBlank() || password.isBlank()) {
            showMessage(R.string.msg_error_username_password_required)
            return
        }
        viewModelScope.launch {
            userRepository.login(username, password)
        }
    }

    fun onAuthSucceeded(cryptoObject: CryptoObject?) {
        Timber.i("On Auth Succeeded $cryptoObject")
        val pendingAuthContext = uiState.value.authContext
        _uiState.update {
            it.copy(
                askBiometricEnrollment = false,
                authContext = null
            )
        }
        viewModelScope.launch {
            pendingAuthContext?.let { authContext ->
                if (authContext.purpose == CryptoPurpose.Encryption) {
                    startBiometricTokenEnrollment(cryptoObject!!)
                } else {
                    startLoginWithToken(cryptoObject!!)
                }
            }

        }
    }

    private suspend fun startBiometricTokenEnrollment(cryptoObject: CryptoObject) {
        val result = getResult { biometricRepository.fetchAndStoreEncryptedToken(cryptoObject) }
        result.switch(
            success = { Timber.i("fetchAndStoreEncryptedToken done") },
            error = {
                it?.let { ex ->
                    if (ex is InvalidCryptoLayerException) {
                        handleInvalidCryptoException(ex, false)
                    } else {
                        handleError(ex)
                    }
                }
            }
        )
    }

    private suspend fun startLoginWithToken(cryptoObject: CryptoObject) {
        getResult {
            val tokenAsCredential = biometricRepository.decryptToken(cryptoObject)
            doLoginWithToken(tokenAsCredential)
        }.switch(
            success = { Timber.d("Login Done") },
            error = { th ->
                if (th is InvalidCryptoLayerException) {
                    _uiState.update { it.copy(canLoginWithBiometry = false) }
                } else {
                    handleError(th)
                }
            }
        )
    }

    private fun doLoginWithToken(tokenAsCredential: String) {
        viewModelScope.launch {
            delay(100)
            userRepository.loginWithToken(tokenAsCredential)
        }
    }

    fun onAuthError(errorCode: Int, errString: CharSequence) {
        when (errorCode) {
            BiometricPrompt.ERROR_USER_CANCELED,
            BiometricPrompt.ERROR_CANCELED,
            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                Timber.i("operation is cancelled by user interaction")
            }
            else -> {
                showMessage(errString.toString())
            }
        }
        _uiState.update { it.copy(askBiometricEnrollment = false, authContext = null) }
    }

    fun requireBiometricLogin() {
        viewModelScope.launch {
            getResult { prepareAuthContext(CryptoPurpose.Decryption) }
                .switch(
                    success = { authCtx ->
                        _uiState.update {
                            it.copy(
                                askBiometricEnrollment = false,
                                authContext = authCtx
                            )
                        }
                    },
                    error = {
                        it?.let { ex ->
                            if (ex is InvalidCryptoLayerException) {
                                handleInvalidCryptoException(ex, true)
                            } else {
                                handleError(ex)
                            }
                        }
                    }
                )
        }
    }

    private suspend fun prepareAuthContext(purpose: CryptoPurpose): AuthContext {
        val cryptoObject = biometricRepository.createCryptoObject(purpose)
        return AuthContext(
            purpose = purpose,
            cryptoObject = cryptoObject
        )
    }

    private fun handleError(ex: Throwable?) {
        Timber.e(ex, "handleException: ${ex?.message}")
        ex?.let {
            SnackbarManager.showMessage(R.string.msg_error_generic)
        }
    }

    private fun handleInvalidCryptoException(ex: InvalidCryptoLayerException, isLogin: Boolean) {
        Timber.e(ex, "handleInvalidCryptoException... isLogin: $isLogin")
        if (ex.isKeyPermanentlyInvalidated) {
            SnackbarManager.showMessage(R.string.msg_error_key_permanently_invalidated)
        } else if (ex.isKeyInitFailed) {
            SnackbarManager.showMessage(R.string.msg_error_key_init_fail)
        } else {
            SnackbarManager.showMessage(R.string.msg_error_generic)
        }
        if(isLogin){
            _uiState.update { it.copy(canLoginWithBiometry = false)}
        }
    }

    private fun showMessage(message: String) {
        SnackbarManager.showMessage(message)
    }

    private fun showMessage(@StringRes messageTextId: Int) {
        SnackbarManager.showMessage(messageTextId)
    }
}