package com.mzzlab.sample.biometric.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mzzlab.sample.biometric.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> by lazy {
        MutableStateFlow(HomeUiState(
            loggedIn = userRepository.isUserLoggedIn.value
        ))
    }

    val uiState: StateFlow<HomeUiState> by lazy {
        _uiState.asStateFlow()
    }

    init {
        viewModelScope.launch {
            userRepository.
            isUserLoggedIn.collect {
                _uiState.value = uiState.value.copy(
                    loggedIn = it
                )
            }
        }
    }


    fun logout(){
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}